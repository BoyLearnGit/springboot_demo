package com.youedata.util;

/**
 * @author: lyl
 * @date: 2018/12/27 16:22.
 */
public class Config {

    private static int httpConnectTimeout=3000;
    private static int httpSocketTimeout=3000;
    private static int httpMaxPoolSize=100;
    private static int httpIdelTimeout=30;
    private static int httpMonitorInterval=1000;

    public static int getHttpConnectTimeout(){
        return httpConnectTimeout;
    }
    public static int getHttpSocketTimeout(){
        return httpSocketTimeout;
    }
    public static int getHttpMaxPoolSize(){
        return httpMaxPoolSize;
    }

    public static int getHttpIdelTimeout() {
        return httpIdelTimeout;
    }

    public static int getHttpMonitorInterval() {
        return httpMonitorInterval;
    }
}
