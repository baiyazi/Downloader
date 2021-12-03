package com.weizu.filedownloader.bean;

import com.weizu.filedownloader.enums.DownloadState;
import com.weizu.filedownloader.services.AssignDownloadTask;
import com.weizu.filedownloader.services.SharedPreferencesHelper;

import java.io.File;

/**
 * 记录文件下载位置的类
 * @author 梦否
 * @version 1.0
 * @since 1.0
 * @csdn https://blog.csdn.net/qq_26460841
 * @date 2021-11-16 14:52:41
 */
public class FileDownloadPosition {
    private String url;                          // 文件链接
    private long startPosition, endPosition;     // 需要下载的起始位置和结束位置
    private long currentPosition;                // 当前下载到什么地方
    private final int index;                     // 第几个段
    private AssignDownloadTask mTask;

    public FileDownloadPosition(AssignDownloadTask task, String url, long startPosition, long endPosition, int index) {
        this.url = url;
        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.index = index;
        this.mTask = task;
    }

    public void addCurrentPosition(long increment){
        this.currentPosition += increment;
        SharedPreferencesHelper.storageDownloadPosition(url, index, this.currentPosition);
        if(this.currentPosition == endPosition - startPosition){
            // 删除SharedPreferences中保存的数据
            SharedPreferencesHelper.deleteSharedPreferenceRecord(url);
            this.mTask.setDownloadState(DownloadState.DOWNLOADED);
        }
    }

    public long getDownloadPositionByIndex(){
        long position = SharedPreferencesHelper.readDownloadPosition(url, index);
        this.currentPosition = position;
        return position;
    }

    public int getIndex() {
        return index;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
    }

    public long getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(long endPosition) {
        this.endPosition = endPosition;
    }

    public long getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(long currentPosition) {
        this.currentPosition = currentPosition;
    }
}
