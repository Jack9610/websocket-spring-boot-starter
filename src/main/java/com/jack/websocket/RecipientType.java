package com.jack.websocket;

import org.springframework.data.redis.listener.PatternTopic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author: Jack
 * @create: 2021-12-09 20:31
 */
public enum RecipientType {

    ALL("websocketMessage:all"),
    PART("websocketMessage:part"),
    SINGLE("websocketMessage:single");

    private String topic;

    RecipientType(String topic) {
        this.topic = topic;
    }

    public String getTopic() {
        return topic;
    }

    public static List<PatternTopic> allTopics() {
        List<PatternTopic> topics = new ArrayList<>();
        for (RecipientType type : values()) {
            topics.add(new PatternTopic(type.topic));
        }
        return topics;
    }

    public static RecipientType converter(String topic) {
        for (RecipientType type : values()) {
            if (Objects.equals(topic, type.topic)) {
                return type;
            }
        }
        return null;
    }
}
