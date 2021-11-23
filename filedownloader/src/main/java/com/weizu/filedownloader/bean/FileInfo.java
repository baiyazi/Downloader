package com.weizu.mylibrary2.bean;

import android.content.Context;

import com.weizu.mylibrary2.enums.FileSuffix;
import com.weizu.mylibrary2.utils.EncoderUtils;
import com.weizu.mylibrary2.utils.FileUtils;
import com.weizu.mylibrary2.services.SharedPreferencesHelper;

import java.io.File;

/**
 * 文件的相关信息类
 * @auth 梦否
 * @csdn https://blog.csdn.net/qq_26460841
 * @date 2021-11-16 11:01:18
 */
public class FileInfo {
    private String url;                          // 文件链接
    private FileSuffix suffix;                   // 文件后缀
    private long totalSize;                      // 文件总大小
    private String newFileName;                  // 下载的文件自定义名字，不设置就默认为url的Hash值
    private Context context;

    public FileInfo(){}

    /**
     * 构造函数
     * @param context 上下文对象
     * @param url 待下载文件URL地址
     * @param suffix 文件后缀
     * @param totalSize 总大小
     */
    public FileInfo(Context context, String url, FileSuffix suffix, long totalSize){
        this.context = context;
        this.url = url;
        this.suffix = suffix;
        this.totalSize = totalSize;
        this.newFileName = EncoderUtils.hashKeyFromUrl(this.url);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getNewFileName() {
        return newFileName;
    }

    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public FileSuffix getSuffix() {
        return suffix;
    }

    public void setSuffix(FileSuffix suffix) {
        this.suffix = suffix;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    /**
     * 判断应用缓存目录下是否存在这个cacheDir目录，没有就创建。
     * 同时，如果有SD卡，就优先存储在SD卡中。
     */
    private File buildPath(String cacheDir) {
        return FileUtils.buildPath(context, cacheDir);
    }

    /**
     * 获取存储文件的File对象
     * @return File
     */
    public File getDownloadStorageFile(String cacheDir){
        File file = buildPath(cacheDir);
        String fileName = newFileName + "." + suffix.getValue();
        return new File(file, fileName);
    }
}



