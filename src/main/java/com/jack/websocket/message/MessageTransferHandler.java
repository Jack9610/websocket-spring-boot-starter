package com.jack.websocket.message;

import com.alibaba.fastjson.JSONObject;
import com.jack.websocket.RecipientType;
import com.jack.websocket.WebSocketClient;
import com.jack.websocket.WebSocketManager;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.jack.websocket.RecipientType.ALL;


/**
 * @author: Jack
 * @create: 2021-12-12 21:19
 */
@Component
public class MessageTransferHandler {

    private StringRedisTemplate stringRedisTemplate;

    public MessageTransferHandler(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void publishMessage(RecipientType type, String content, List<String> receivers) {
        TransferMessage message = buildMessage(type, content, receivers);
        stringRedisTemplate.convertAndSend(type.getTopic(), JSONObject.toJSONString(message));
    }

    private TransferMessage buildMessage(RecipientType type, String content, List<String> receivers) {
        return new TransferMessage(content, receivers, type);
    }

    @Configuration
    @AutoConfigureAfter(RedisAutoConfiguration.class)
    public static class SubscribeMessageConfiguration {

        @Resource
        private WebSocketManager webSocketManager;

        @Resource
        private RedisConnectionFactory redisConnectionFactory;

        @Bean
        public RedisMessageListenerContainer redisMessageListenerContainer() {
            RedisMessageListenerContainer container = new RedisMessageListenerContainer();
            container.setConnectionFactory(redisConnectionFactory);
            container.setTaskExecutor(getExecutor());
            container.addMessageListener((message, pattern) -> {
                String topic = new String(Objects.requireNonNull(pattern));
                RecipientType type = RecipientType.converter(topic);
                TransferMessage transferMessage = JSONObject.parseObject(message.toString(), TransferMessage.class);
                handleMessageReceived(type, transferMessage);
            }, RecipientType.allTopics());
            return container;
        }

        private void handleMessageReceived(RecipientType type, TransferMessage message) {
            String content = message.getContent();
            if (ALL.equals(type)) {
                webSocketManager.allClient().parallelStream().
                        forEach(webSocketClient -> {
                            webSocketClient.pushMessageToClient(content);
                        });
            } else {
                List<String> receivers = message.getReceivers();
                receivers.parallelStream()
                        .forEach(receiver -> {
                            WebSocketClient webSocketClient = webSocketManager.getClient(receiver);
                            webSocketClient.pushMessageToClient(content);
                        });
            }
        }

        // 提供一个不限量的线程池
        private Executor getExecutor() {
            return Executors.newCachedThreadPool();
        }
    }
}
