package com.weizu.filedownloader.base;

import com.weizu.filedownloader.enums.FileSuffix;
import com.weizu.filedownloader.listener.IDownloadListener;

/**
 * 下载控制器的基类
 * @auth 梦否
 * @csdn https://blog.csdn.net/qq_26460841
 * @date 2021-11-16 09:56:37
 */
public abstract class BaseDownloadController {
    // 缓存在本地Cache文件夹下的子文件夹
    private String mCachePath = null;

    public void setCachePath(String cachePath){
        this.mCachePath = cachePath;
    }

    public String getCachePath() {
        return mCachePath;
    }

    /**
     * 开启下载
     * @param listener 下载监听接口
     */
    public abstract void download(IDownloadListener listener);
}
