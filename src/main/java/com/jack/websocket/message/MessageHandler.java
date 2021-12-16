package com.jack.websocket.message;


import com.jack.websocket.Identity;

/**
 * @author: Jack
 * @create: 2021-12-15 16:01
 */
public interface MessageHandler<T> {

    void HandlerMessage(T message, Identity identity);

    String getScene();
}
