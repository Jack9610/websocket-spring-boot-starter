package com.jack.websocket.config;

import com.jack.websocket.TextMessageWebSocketHandler;
import com.jack.websocket.WebSocketHandshakeInterceptor;
import com.jack.websocket.properties.WebSocketProperties;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;

/**
 * @author: Jack
 * @create: 2021-12-09 21:25
 */
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Resource
    private WebSocketHandshakeInterceptor webSocketHandshakeInterceptor;

    @Resource
    private TextMessageWebSocketHandler textMessageWebSocketHandler;

    @Resource
    private WebSocketProperties webSocketProperties;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(textMessageWebSocketHandler, webSocketProperties.getPath())
                .setAllowedOrigins(webSocketProperties.getAllowOrigin())
                .addInterceptors(webSocketHandshakeInterceptor);
    }
}
