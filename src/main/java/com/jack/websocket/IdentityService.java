package com.jack.websocket;

/**
 * @author: Jack
 * @create: 2021-12-12 22:52
 */
public interface IdentityService {

    Identity getIdentity(String authentication);
}
