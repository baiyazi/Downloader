package com.weizu.filedownloader2.utils;

import android.content.Context;
import android.os.Environment;

import com.weizu.filedownloader2.config.DownloadOptions;

import java.io.File;
import java.util.Objects;

/**
 * 文件处理类
 *
 * @author 梦否
 * @version 1.0
 * @since 1.0
 */
public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();

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
        // null 表示根目录
        LogUtil.e(TAG, flag ? "存在SD卡" : "不存在SD卡");
        if (flag) cachePath = findSDCardRoot(context.getExternalFilesDir(null)).toString();
        else cachePath = context.getCacheDir().getPath();

        File directory = new File(cachePath + File.separator + cacheDir);
        // 目录不存在就创建
        if (!directory.exists()) {
            boolean mkdirs = directory.mkdirs();
            LogUtil.e(TAG, "下载目录创建" + (mkdirs ? "成功" : "失败"));
        }
        return directory;
    }

    private static File findSDCardRoot(File externalFilesDir) {
        File parent;
        boolean equals = (Objects.requireNonNull(parent = externalFilesDir.getParentFile())).getName().equals("0");
        if (!equals) {
            return findSDCardRoot(parent);
        } else {
            return parent;
        }
    }
}
