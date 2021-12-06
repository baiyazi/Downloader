package com.weizu.filedownloader2.utils;

import android.util.Log;

import java.io.IOException;

/**
 * 日志类，控制是否开启日志功能。
 * 为了在Logcat中方便过滤，所以统一将TAG设置为一个值。
 *
 * @author 梦否
 * @version 1.0.1
 * @since 1.0
 */
public class LogUtil {

    private static final String TAG = LogUtil.class.getSimpleName();

    // 是否开启日志开关，默认为true，即显示日志
    private static boolean showLog = true;

    public static void openLog() {
        showLog = true;
    }

    public static void closeLog() {
        showLog = false;
    }

    public static void e(String tag, String msg) {
        if (showLog) {
            Log.e(TAG, "class: " + tag + ". msg: " + msg);
        }
    }

    public static void e(String tag, String msg, IOException e) {
        if (showLog) {
            Log.e(TAG, "class: " + tag + ". msg: " + msg);
            e.printStackTrace();
        }
    }

    public static void d(String tag, String msg) {
        if (showLog) {
            Log.d(TAG, "class: " + tag + ". msg: " + msg);
        }
    }

    public static void i(String tag, String msg) {
        if (showLog) {
            Log.i(TAG, "class: " + tag + ". msg: " + msg);
        }
    }

    public static void w(String tag, String msg) {
        if (showLog) {
            Log.w(TAG, "class: " + tag + ". msg: " + msg);
        }
    }
}
