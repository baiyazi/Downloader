package com.weizu.filedownloader2.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;


import androidx.annotation.Nullable;

import com.weizu.filedownloader2.config.DownloadOptions;
import com.weizu.filedownloader2.utils.LogUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * SharedPreferences文件的辅助类。在SP文件中存储每个文件的每个线程的下载位置，比如：
 * <code>
 * <set name="36626419f3ccf8bd2a68af1e1e0fd98c">
 *     <string>1#4592720</string>
 *     <string>2#4596720</string>
 * </set>
 * </code>
 * 其中name为下载文件的md5标识，存储内容为线程id#下载到了什么位置
 * <p> 需要注意的是，因为SP文件操作比较消耗性能，所以这里只有不需要每次都将记录写入都文件中，写入时机为：
 * <ul>
 *     <li>用户开始下载的时候，需要读取是否存在历史下载。</li>
 *     <li>用户点击暂停的时候，需要记录当前改文件的各个线程的下载位置。</li>
 *     <li>用户直接退出的时候，如何检测？并保存状态？</li>
 * </ul>
 * @author 梦否
 * @version 1.0
 * @since 1.0
 */
public class SharedPreferencesHelper {
    private static final String TAG = DownloadOptions.class.getSimpleName();
    private SharedPreferences mPreferences;
    private static final String spFileName = "DownloadRecord";
    private static volatile SharedPreferencesHelper mSharedPreferencesHelper;

    private SharedPreferencesHelper(SharedPreferences preferences){
        this.mPreferences = preferences;
    }

    public static SharedPreferencesHelper initSharedPreferences(Context c){
        if(mSharedPreferencesHelper == null){
            synchronized (SharedPreferencesHelper.class){
                if(mSharedPreferencesHelper == null){
                    mSharedPreferencesHelper = new SharedPreferencesHelper(
                            c.getApplicationContext().getSharedPreferences(spFileName, Context.MODE_PRIVATE)
                    );
                }
            }
        }
        return mSharedPreferencesHelper;
    }

    public static SharedPreferencesHelper getInstance(){
        return mSharedPreferencesHelper;
    }

    public synchronized void storageDownloadPosition(String md5, int index, long pos){
        SharedPreferences.Editor edit = mPreferences.edit();
        Set<String> values = mPreferences.getStringSet(md5, new HashSet<>());
        values.add(index+"#"+pos);
        edit.putStringSet(md5, values);
        edit.apply();
    }

    public synchronized long readDownloadPosition(String md5, int index){
        Set<String> values = mPreferences.getStringSet(md5, new HashSet<>());
        for (String value : values) {
            String[] split = value.split("#");
            if(Integer.parseInt(split[0]) == index) {
                return Long.parseLong(split[1]);
            }
        }
        return 0L;
    }

    public synchronized void deleteSharedPreferenceRecord(String md5){
        LogUtil.e(TAG, "正在删除Sharedpreferences文件中的记录");
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.remove(md5);
        edit.apply();
    }
}
