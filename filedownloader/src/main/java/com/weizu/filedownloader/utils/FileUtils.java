package com.weizu.mylibrary2.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class FileUtils {

    /**
     * 判断应用缓存目录下是否存在这个cacheDir目录，没有就创建。
     * 同时，如果有SD卡，就优先存储在SD卡中。
     *
     * @param cacheDir 缓存目录
     * @return 缓存目录File对象
     */
    public static File buildPath(Context context, String cacheDir) {
        // 是否有SD卡
        boolean flag = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        // 如果有SD卡就存在外存，否则就位于这个应用的data/package name/cache目录下
        final String cachePath;
        if (flag) cachePath = context.getExternalCacheDir().getPath();
        else cachePath = context.getCacheDir().getPath();

        File directory = new File(cachePath + File.separator + cacheDir);
        // 目录不存在就创建
        if (!directory.exists()) directory.mkdirs();
        return directory;
    }
}
