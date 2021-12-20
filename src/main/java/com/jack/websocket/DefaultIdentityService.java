package com.jack.websocket;

/**
 * @author: Jack
 * @create: 2021-12-13 21:50
 */
public class DefaultIdentityService implements IdentityService {

    @Override
    public Identity getIdentity(String authorization) {
        return () -> authorization;
    }
}
