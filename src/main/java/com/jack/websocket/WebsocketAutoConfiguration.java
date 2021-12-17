package com.jack.websocket;

import com.jack.websocket.message.MessageHandlerContext;
import com.jack.websocket.message.MessageTransferHandler;
import com.jack.websocket.properties.WebSocketProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import javax.annotation.Resource;

/**
 * @author: Jack
 * @create: 2021-12-09 19:37
 */
@Configuration(proxyBeanMethods = true)
@EnableWebSocket
@ConditionalOnClass(RedisAutoConfiguration.class)
@Import({WebSocketManager.class, MessageTransferHandler.SubscribeMessageConfiguration.class,
        MessageHandlerContext.class})
@EnableConfigurationProperties(WebSocketProperties.class)
public class WebsocketAutoConfiguration {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Bean
    @ConditionalOnMissingBean
    public IdentityService identityService() {
        return new DefaultIdentityService();
    }

    @Bean
    public TextMessageWebSocketHandler textMessageWebSocketHandler(WebSocketManager webSocketManager,
                                                                   MessageHandlerContext messageHandlerContext) {
        return new TextMessageWebSocketHandler(webSocketManager, messageHandlerContext);
    }

    @Bean
    public WebSocketHandshakeInterceptor webSocketHandshakeInterceptor(IdentityService identityService) {
        return new WebSocketHandshakeInterceptor(identityService, stringRedisTemplate);
    }

    @Bean
    public MessageTransferHandler messageTransferHandler() {
        return new MessageTransferHandler(stringRedisTemplate);
    }

    @Bean
    public WsTemplate wsTemplate(MessageTransferHandler messageTransferHandler, WebSocketManager webSocketManager) {
        return new WsTemplate(messageTransferHandler, webSocketManager);
    }
}
