package com.jack.websocket.message;

import com.alibaba.fastjson.JSONObject;
import com.jack.websocket.Identity;
import com.jack.websocket.WebsocketConstant;
import com.jack.websocket.exception.IllegalMessageException;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author: Jack
 * @create: 2021-12-16 13:51
 */
public class MessageHandlerContext implements InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private Map<String, MessageHandler> handlerMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, MessageHandler> beanMap = applicationContext.getBeansOfType(MessageHandler.class);
        beanMap.forEach((beanName, bean) -> {
            String scene = bean.getScene();
            handlerMap.put(scene, bean);
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public MessageHandler getMessageHandler(String scene) {
        return handlerMap.get(scene);
    }

    @SuppressWarnings("unchecked")
    public void handleCompositeMessage(String compositeMessage, Identity identity) {
        String scene = getScene(compositeMessage);
        MessageHandler messageHandler = getMessageHandler(scene);
        Type messageType = getMessageType(messageHandler);
        Object paramObj = transToObj(compositeMessage, (Class<?>) messageType);
        messageHandler.HandlerMessage(paramObj, identity);
    }

    private <T> T transToObj(String compositeMessage, Class<T> tClass) {
        try {
            JSONObject messageObj = parseObject(compositeMessage);
            String data = messageObj.getString("data");
            return JSONObject.parseObject(data, tClass);
        } catch (Exception e) {
            throw new IllegalMessageException("不合法的消息!", e);
        }
    }

    private String getScene(String compositeMessage) {
        JSONObject messageObj = parseObject(compositeMessage);
        return messageObj.getString(WebsocketConstant.SCENE);
    }

    private JSONObject parseObject(String compositeMessage) {
        try {
            return JSONObject.parseObject(compositeMessage);
        } catch (Exception e) {
            throw new IllegalMessageException("不合法的消息!", e);
        }
    }

    private static Type getMessageType(MessageHandler messageHandler) {
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(messageHandler);

        Type matchedGenericInterface = null;
        while (Objects.nonNull(targetClass)) {
            Type[] interfaces = targetClass.getGenericInterfaces();
            if (Objects.nonNull(interfaces)) {
                for (Type type : interfaces) {
                    if (type instanceof ParameterizedType &&
                            (Objects.equals(((ParameterizedType) type).getRawType(), MessageHandler.class))) {
                        matchedGenericInterface = type;
                        break;
                    }
                }
            }
            targetClass = targetClass.getSuperclass();
        }
        if (Objects.isNull(matchedGenericInterface)) {
            return Object.class;
        }

        Type[] actualTypeArguments = ((ParameterizedType) matchedGenericInterface).getActualTypeArguments();
        if (Objects.nonNull(actualTypeArguments) && actualTypeArguments.length > 0) {
            return actualTypeArguments[0];
        }
        return Object.class;
    }
}
