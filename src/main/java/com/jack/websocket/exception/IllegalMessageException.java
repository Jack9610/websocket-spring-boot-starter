package com.jack.websocket.exception;

/**
 * @author: Jack
 * @create: 2021-12-16 14:09
 */
public class IllegalMessageException extends RuntimeException {

    public IllegalMessageException() {
        super();
    }

    public IllegalMessageException(String message) {
        super(message);
    }

    public IllegalMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
