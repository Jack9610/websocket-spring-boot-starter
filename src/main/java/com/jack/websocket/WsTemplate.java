package com.jack.websocket;

import com.jack.websocket.message.MessageTransferHandler;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: Jack
 * @create: 2021-12-12 22:45
 */
public class WsTemplate {

    private MessageTransferHandler messageTransferHandler;

    private WebSocketManager webSocketManager;

    public WsTemplate(MessageTransferHandler messageTransferHandler, WebSocketManager webSocketManager) {
        this.messageTransferHandler = messageTransferHandler;
        this.webSocketManager = webSocketManager;
    }

    public void sendMessageToPart(String message, List<Identity> identities) {
        List<String> identityIds = identities.stream().map(Identity::getIdentityId).collect(Collectors.toList());
        messageTransferHandler.publishMessage(RecipientType.PART, message, identityIds);
    }

    public void sendMessageToAll(String message) {
        messageTransferHandler.publishMessage(RecipientType.ALL, message, null);
    }

    public void sendMessageToSingle(String message, Identity identity) {
        WebSocketClient client = webSocketManager.getClient(identity.getIdentityId());
        if (client != null) {
            client.pushMessageToClient(message);
        } else {
            messageTransferHandler.publishMessage(RecipientType.SINGLE, message, Collections.singletonList(identity.getIdentityId()));
        }
    }
}
