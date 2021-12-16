package com.jack.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * @author: Jack
 * @create: 2021-12-09 20:11
 */
public class WebSocketClient {

    private Logger logger = LoggerFactory.getLogger(WebSocketClient.class);

    private final WebSocketSession webSocketSession;

    private final Identity identity;

    public WebSocketClient(WebSocketSession webSocketSession, Identity identity) {
        this.webSocketSession = webSocketSession;
        this.identity = identity;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void pushMessageToClient(String message) {
        synchronized (webSocketSession) {
            try {
                webSocketSession.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                logger.error("An error occurred while sending the message", e);
            }
        }
    }
}
