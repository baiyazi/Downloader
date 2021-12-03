package com.weizu.filedownloader.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.weizu.filedownloader.utils.EncoderUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * SharedPreferences文件的辅助类
 * @auth 梦否
 * @csdn https://blog.csdn.net/qq_26460841
 * @date 2021-11-16 10:11:12
 */
public class SharedPreferencesHelper {
    private static final String TAG = "DownloadSpHelper";
    private static volatile SharedPreferences preferences;
    private static final String spFileName = "DownloadRecord";

    private SharedPreferencesHelper(){}

    public static void initSharedPreferences(Context c){
        if(preferences == null){
            synchronized (SharedPreferencesHelper.class){
                if(preferences == null){
                    preferences = c.getApplicationContext().
                            getSharedPreferences(spFileName, Context.MODE_PRIVATE);
                }
            }
        }
    }

    private static String getEncodeNameByURL(String url){
        return EncoderUtils.hashKeyFromUrl(url);
    }

    public synchronized static void storageDownloadPosition(String url, int index, long pos){
        SharedPreferences.Editor edit = preferences.edit();
        String newName = getEncodeNameByURL(url);
        Set<String> values = preferences.getStringSet(newName, new HashSet<>());
        values.add(index+"#"+pos);
        edit.putStringSet(newName, values);
        edit.apply();
    }

    public synchronized static long readDownloadPosition(String url, int index){
        String newName = getEncodeNameByURL(url);
        Set<String> values = preferences.getStringSet(newName, new HashSet<>());
        for (String value : values) {
            String[] split = value.split("#");
            if(Integer.parseInt(split[0]) == index) {
                return Long.parseLong(split[1]);
            }
        }
        return 0L;
    }

    public synchronized static void deleteSharedPreferenceRecord(String url){
        Log.e(TAG, "正在删除Sharedpreferences文件中的记录");
        SharedPreferences.Editor edit = preferences.edit();
        String newName = getEncodeNameByURL(url);
        edit.remove(newName);
        edit.apply();
    }
}
