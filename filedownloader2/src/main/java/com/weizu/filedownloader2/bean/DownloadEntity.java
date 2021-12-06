package com.weizu.filedownloader2.bean;

import android.content.Context;

import com.weizu.filedownloader2.enums.FileSuffix;
import com.weizu.filedownloader2.services.SharedPreferencesHelper;
import com.weizu.filedownloader2.utils.EncoderUtil;
import com.weizu.filedownloader2.utils.FileUtil;

import java.io.File;
import java.util.Objects;

/**
 * 文件的相关信息类
 * @author 梦否
 * @version 1.0
 * @since 1.0
 */
public class DownloadEntity {
    private String url;                          // 文件链接
    private FileSuffix suffix;                   // 文件后缀
    private long totalSize;                      // 文件总大小
    private String newFileName;                  // 下载的文件自定义名字，不设置就默认为url的Hash值
    private String md5;                          // 文件流的md5值
    private Context context;
    private File parentPath;                     // 文件下载位置的父目录


    public void setContext(Context context) {
        this.context = context;
        SharedPreferencesHelper.initSharedPreferences(context);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Context getContext() {
        return context;
    }

    public FileSuffix getSuffix() {
        return suffix;
    }

    public void setSuffix(FileSuffix suffix) {
        this.suffix = suffix;
    }

    public DownloadEntity(){}

    public String getUrl() {
        return url;
    }

    public DownloadEntity(Context context, String url, FileSuffix suffix){
        this.url = url;
        this.suffix = suffix;
        this.setContext(context);
    }

    public String getNewFileName() {
        return newFileName == null ? getDefaultFileName() : newFileName;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getMd5() {
        return md5;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    private String getDefaultFileName() {
        return EncoderUtil.hashKeyFromUrl(this.url);
    }

    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }

    /**
     * 获取存储文件的File对象
     * @return File
     */
    public File getLocalStorageFile(String cacheDir){
        if(this.parentPath == null) {
            this.parentPath = buildPath(cacheDir);
        }
        String fileName = getNewFileName() + "." + suffix.getValue();
        return new File(this.parentPath, fileName);
    }

    /**
     * 判断应用缓存目录下是否存在这个cacheDir目录，没有就创建。
     * 同时，如果有SD卡，就优先存储在SD卡中。
     */
    private File buildPath(String cacheDir) {
        return FileUtil.buildPath(context, cacheDir);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DownloadEntity)) return false;
        DownloadEntity that = (DownloadEntity) o;
        return totalSize == that.totalSize &&
                Objects.equals(url, that.url) &&
                suffix == that.suffix &&
                Objects.equals(newFileName, that.newFileName) &&
                Objects.equals(md5, that.md5) &&
                Objects.equals(context, that.context) &&
                Objects.equals(parentPath, that.parentPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, suffix, totalSize, newFileName, md5, context, parentPath);
    }
}




