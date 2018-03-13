package com.kys.statistics.config;

/**
 * Created by busiya on 2018/3/5.
 * 网络的配置信息
 */

public class HttpConfig {
    public static final String HOST_RELEASE = "http://stat.bazirim.tv:9090";
    public static final String HOST_DEBUG = "http://120.205.22.100:9091/BDA";
//    public static final String HOST_DEBUG = "http://128.27.8.104:8083/BDA";
    public static final String ClickDo = "/BazirimApiController.do?doPBSequence";
    public static final String ExposureDo ="/BazirimApiController.do?doPBExposureSequence";
    public static final String CustomerDo = "/BazirimApiController.do?doPBCustomerSequence";
}
