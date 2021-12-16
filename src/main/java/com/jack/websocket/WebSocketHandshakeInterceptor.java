package com.jack.websocket;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author: Jack
 * @create: 2021-12-09 20:01
 */
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private IdentityService identityService;

    private StringRedisTemplate stringRedisTemplate;

    public WebSocketHandshakeInterceptor(IdentityService identityService,
                                         StringRedisTemplate stringRedisTemplate) {
        this.identityService = identityService;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        Identity identity = getIdentity(request);
        String clientKey = WebSocketManager.getClientKey(identity.getIdentityId());
        attributes.put(WebsocketConstant.CLIENT_KEY, identity);
        Boolean isConnected = stringRedisTemplate.hasKey(clientKey);
        return isConnected == null || !isConnected;
    }

    private Identity getIdentity(ServerHttpRequest request) {
        ServletServerHttpRequest req = (ServletServerHttpRequest) request;
        HttpServletRequest servletRequest = req.getServletRequest();
        String authentication = servletRequest.getParameter("Authorization");
        return identityService.getIdentity(authentication);
    }

    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
