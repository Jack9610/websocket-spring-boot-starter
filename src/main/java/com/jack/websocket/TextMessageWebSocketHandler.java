package com.jack.websocket;

import com.jack.websocket.message.MessageHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Objects;

/**
 * @author: Jack
 * @create: 2021-12-09 21:43
 */
public class TextMessageWebSocketHandler extends AbstractWebSocketHandler {

    private Logger logger = LoggerFactory.getLogger(TextMessageWebSocketHandler.class);

    private WebSocketManager webSocketManager;

    private MessageHandlerContext messageHandlerContext;

    public TextMessageWebSocketHandler(WebSocketManager webSocketManager, MessageHandlerContext messageHandlerContext) {
        this.webSocketManager = webSocketManager;
        this.messageHandlerContext = messageHandlerContext;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        if (Objects.equals(payload, WebsocketConstant.PING)) {
            session.sendMessage(new TextMessage(WebsocketConstant.PONG));
            return;
        }
        messageHandlerContext.handleCompositeMessage(payload, getIdentity(session));
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Identity identity = getIdentity(session);
        logger.info("webSocket connection established, identityId:[{}]", identity.getIdentityId());
        WebSocketClient webSocketClient = new WebSocketClient(session, identity);
        webSocketManager.storedClient(webSocketClient);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Identity identity = getIdentity(session);
        logger.info("webSocket connection closed, identityId:[{}]", identity.getIdentityId());
        webSocketManager.removeClient(identity.getIdentityId());
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        Identity identity = getIdentity(session);
        logger.warn("webSocket transport error, identityId:[{}]", identity.getIdentityId());
        super.handleTransportError(session, exception);
    }

    private Identity getIdentity(WebSocketSession session) {
        return (Identity) session.getAttributes().get(WebsocketConstant.CLIENT_KEY);
    }
}
