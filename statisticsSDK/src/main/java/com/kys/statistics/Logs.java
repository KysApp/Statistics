package com.kys.statistics;

import android.util.Log;

/**
 * Created by busiya on 2018/1/9.
 * 统计模块的日志信息打印
 */

public class Logs {

    public static Boolean enableLogs = true;

    public static void e(String tag, String message) {
        if (enableLogs) {
            Log.e(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (enableLogs) {
            Log.i(tag, message);
        }
    }

    public static void d(String tag, String message) {
        if (enableLogs) {
            Log.d(tag, message);
        }
    }

    public static void v(String tag, String message) {
        if (enableLogs) {
            Log.v(tag, message);
        }
    }

    public static void w(String tag, String message) {
        if (enableLogs) {
            Log.w(tag, message);
        }
    }
}
