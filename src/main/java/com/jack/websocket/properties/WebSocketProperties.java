package com.jack.websocket.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author: Jack
 * @create: 2021-12-16 15:13
 */
@ConfigurationProperties(prefix = "jack.websocket")
public class WebSocketProperties {

    private String path = "/ws";

    private Long maxConnectedTime = 60L * 60 * 6;

    private Boolean printConnectedInfo = false;

    private String[] allowOrigin = new String[]{"*"};

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getMaxConnectedTime() {
        return maxConnectedTime;
    }

    public void setMaxConnectedTime(Long maxConnectedTime) {
        this.maxConnectedTime = maxConnectedTime;
    }

    public Boolean getPrintConnectedInfo() {
        return printConnectedInfo;
    }

    public void setPrintConnectedInfo(Boolean printConnectedInfo) {
        this.printConnectedInfo = printConnectedInfo;
    }

    public String[] getAllowOrigin() {
        return allowOrigin;
    }

    public void setAllowOrigin(String[] allowOrigin) {
        this.allowOrigin = allowOrigin;
    }
}
