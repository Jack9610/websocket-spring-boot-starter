package com.jack.websocket.message;

import com.jack.websocket.RecipientType;

import java.util.List;

/**
 * @author: Jack
 * @create: 2021-12-12 21:51
 */
public class TransferMessage {

    private String content;

    private List<String> receivers;

    public TransferMessage(String content, List<String> receivers, RecipientType type) {
        this.content = content;
        this.receivers = receivers;
    }

    public String getContent() {
        return content;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

}
