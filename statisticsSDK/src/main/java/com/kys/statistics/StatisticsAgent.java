package com.kys.statistics;

import android.content.Context;
import android.text.TextUtils;

import com.kys.statistics.config.BaseConfig;
import com.kys.statistics.utils.GetDeviceIdUtils;
import com.kys.statistics.utils.HttpUtils;
import com.kys.statistics.utils.StatisticsDataUploadUtils;
import com.kys.statistics.utils.SystemUtils;

import java.net.URLEncoder;

/**
 * Created by busiya on 2018/1/9.
 * 统计模块的初始化类
 * 1、控制日志的打印
 * 2、控制数据上报的接口--测试地址或者正式地址
 * 3、设置公共参数数据
 */

public class StatisticsAgent {


    public static void init(Context context) {
        BaseConfig.NETWORK_TYPE = SystemUtils.GetNetworkType(context);
        BaseConfig.SYSTEM_VERSION = SystemUtils.getPhoneVersionName();
        BaseConfig.DEVICE_ID = new GetDeviceIdUtils(context).getDeviceUuid();
        BaseConfig.DEVICE = URLEncoder.encode(SystemUtils.getPhoneType());
        BaseConfig.APP_VERSION = SystemUtils.getAppVersion(context);//默认使用gradle中配置的版本号，用户可以通过setCommonData()方法重置客户端的版本号

        StatisticsDataUploadUtils.uploadData(context);
    }

    /**
     * 是否打印日志
     * 默认打印
     */
    public static void setEnableLogs(Boolean enable) {
        Logs.enableLogs = enable;
    }

    /**
     * 是否使用测试接口地址
     * 默认使用
     */
    public static void setDebugModel(Boolean isDebug) {
        HttpUtils.isDebug = isDebug;
    }

    /**
     * 设置公共参数数据
     *
     * @param appKey     平台配置的appkey
     * @param appVersion app的版本号
     * @param appChannel app的渠道
     */
    public static void setCommonData(String appKey,
                                     String appVersion,
                                     String appChannel
    ) {
        if (!TextUtils.isEmpty(appKey)) {
            BaseConfig.APP_KEY = appKey;
        }
        if (!TextUtils.isEmpty(appVersion)) {
            BaseConfig.APP_VERSION = appVersion;
        }
        if (!TextUtils.isEmpty(appChannel)) {
            BaseConfig.APP_CHANNEL = appChannel;
        }
    }

    /**
     * 设置用户数据
     * 用户登录和退出登录时，都需要调用该接口重置用户信息
     *
     * @param userId      用户id（可传""）
     * @param userIsLogin 用户是否登录
     */
    public static void setUserData(String userId, Boolean userIsLogin) {
        BaseConfig.USER_IS_LOGIN = userIsLogin;
        BaseConfig.USER_ID = userId;
    }
}

