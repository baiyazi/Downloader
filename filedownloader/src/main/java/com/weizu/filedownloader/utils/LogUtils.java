package com.weizu.mylibrary2.utils;

import android.util.Log;

import java.io.IOException;

public class LogUtils {

    private static boolean showLog = true;

    public static void openLog() {
        showLog = true;
    }

    public static void closeLog() {
        showLog = false;
    }

    public static void e(String tag, String msg){
        if(showLog){
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, IOException e){
        if(showLog){
            Log.e(tag, msg);
            e.printStackTrace();
        }
    }

    public static void d(String tag, String msg){
        if(showLog){
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg){
        if(showLog){
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg){
        if(showLog){
            Log.w(tag, msg);
        }
    }
}
