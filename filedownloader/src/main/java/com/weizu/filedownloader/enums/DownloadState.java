package com.weizu.filedownloader.enums;

/**
 * 文件下载状态，用来进行下载时候的重复判断：
 * - 如果当前文件已经在下载中，就没必要在重复响应下载任务
 * @author  梦否
 * @version 1.0
 * @since 1.0
 * @csdn https://blog.csdn.net/qq_26460841
 * @date 2021-11-17 15:32:01
 */
public enum DownloadState {
    BEFOREDOWNLOAD(0), // 下载前
    DOWNLOADING(1),    // 正在下载
    DOWNLOADED(2),     // 下载完成
    PAUSE(3);          // 暂停

    private int mValue;
    DownloadState(int value){
        this.mValue = value;
    }

    public int getValue(){
        return mValue;
    }
}
