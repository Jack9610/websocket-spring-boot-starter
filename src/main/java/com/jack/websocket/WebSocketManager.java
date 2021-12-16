package com.jack.websocket;


import com.jack.websocket.properties.WebSocketProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author: Jack
 * @create: 2021-12-09 20:19
 */
public class WebSocketManager implements InitializingBean, DisposableBean {

    private Logger logger = LoggerFactory.getLogger(WebSocketManager.class);

    private static final String CLIENT_KEY_PREFIX = "webSocket:";

    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private WebSocketProperties webSocketProperties;

    private ThreadLocal<DateFormat> dateFormat = ThreadLocal
            .withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    private static ConcurrentHashMap<String, WebSocketClient> clients = new ConcurrentHashMap<String, WebSocketClient>(256);

    public WebSocketManager(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void storedClient(WebSocketClient webSocketClient) {
        Identity identity = webSocketClient.getIdentity();
        String clientKey = getClientKey(identity.getIdentityId());
        clients.put(clientKey, webSocketClient);
        stringRedisTemplate.opsForValue().set(clientKey, dateFormat.get().format(new Date()));
        stringRedisTemplate.expire(clientKey, webSocketProperties.getMaxConnectedTime(), TimeUnit.MINUTES);
    }

    public void removeClient(String identityId) {
        String clientKey = getClientKey(identityId);
        clients.remove(clientKey);
        stringRedisTemplate.delete(getClientKey(identityId));
    }

    public WebSocketClient getClient(String identityId) {
        String clientKey = getClientKey(identityId);
        return clients.get(clientKey);
    }

    public static String getClientKey(String identityId) {
        return CLIENT_KEY_PREFIX + identityId;
    }
    public Collection<WebSocketClient> allClient() {
        return clients.values();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (webSocketProperties.getPrintConnectedInfo()) {
            printCurrentConnectionInfo();
        }
    }

    private void printCurrentConnectionInfo() {
        ScheduledExecutorService scheduler = getSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            Integer currentConnectedCount = getCurrentConnectedCount();
            logger.info("currentTime: {}, currentConnectedCount:{}", dateFormat.get().format(new Date()), currentConnectedCount);
        }, 1, 30, TimeUnit.SECONDS);
    }

    private ScheduledExecutorService getSingleThreadScheduledExecutor() {
        return Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "WebSocket-Schedule-Thread");
            thread.setDaemon(true);
            return thread;
        });
    }

    private Integer getCurrentConnectedCount() {
        return clients.values().size();
    }

    @Override
    public void destroy() throws Exception {
        stringRedisTemplate.delete(allClientKey());
    }

    public Set<String> allClientKey() {
        return clients.keySet();
    }
}
