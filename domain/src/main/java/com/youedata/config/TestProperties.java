package com.youedata.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by liyanlu on 2018/1/24.
 */
@ConfigurationProperties(prefix = "test")
@Component
public class TestProperties {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
