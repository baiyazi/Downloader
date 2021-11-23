package com.weizu.mylibrary2.services;

import android.util.Log;

import com.weizu.mylibrary2.bean.FileDownloadPosition;
import com.weizu.mylibrary2.bean.FileInfo;
import com.weizu.mylibrary2.enums.DownloadState;
import com.weizu.mylibrary2.listener.IDownloadListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 文件分段下载，多个线程共同下载一个文件，这里定义为这种情况下的单线程下载操作
 * @auth 梦否
 * @csdn https://blog.csdn.net/qq_26460841
 * @date 2021-11-16 15:05:23
 */
public class DownloadThread extends Thread{
    private static final String TAG = "DownloadThread";
    private long currentPosition;                 // 当前下载位置
    private IDownloadListener listener;           // 文件下载监听
    private FileInfo fileInfo;                    // 文件信息类
    private FileDownloadPosition position;        // 文件分段后的每段下载位置记录
    private String cachePath;                     // 本地存储目录的子目录名称cache/cachePath
    private AssignDownloadTask mTask;

    private DownloadThread(){}
    public DownloadThread(AssignDownloadTask task, FileInfo fileInfo, String cachePath, FileDownloadPosition position) {
        // 首先读取一下下载进度，得到当前的下载位置，因为不必重复下载
        this.currentPosition = position.getCurrentPosition();
        this.fileInfo = fileInfo;
        this.position = position;
        this.cachePath = cachePath;
        this.mTask = task;
    }

    public void setDownloadListener(IDownloadListener listener){
        this.listener = listener;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        URL url_c = null;
        InputStream inputStream = null;
        File downloadFile = null;
        try{
            RandomAccessFile randomAccessFile = new RandomAccessFile((downloadFile = this.fileInfo.getDownloadStorageFile(cachePath)), "rwd");
            // 设置写入文件的开始位置
            randomAccessFile.seek(this.position.getStartPosition() + this.currentPosition);
            url_c = new URL(this.fileInfo.getUrl());
            connection = (HttpURLConnection) url_c.openConnection();
            connection.setConnectTimeout(5 * 1000); // 5秒钟超时
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("Range", "bytes=" + (this.position.getStartPosition() + this.currentPosition) +"-" + this.position.getEndPosition());

            Log.e(TAG, Thread.currentThread().getName() + "请求数据范围：bytes=" + (this.position.getStartPosition() + this.currentPosition) + "-" + position.getEndPosition());
            inputStream = connection.getInputStream();

            if (connection.getResponseCode() == 206) {
                byte[] buffer = new byte[1024 * 1024 * 10];
                int len = -1;
                while ((len = inputStream.read(buffer)) != -1) {
                    // 响应暂停
                    if(this.mTask.getPaused()){
                        this.mTask.setDownloadState(DownloadState.PAUSE);
                        return;
                    }

                    randomAccessFile.write(buffer, 0, len);
                    // todo 【下载进度】
                    this.position.addCurrentPosition(len);
                    DownloadProgressHelper.increasementDownloadProgressValue(fileInfo.getUrl(),  position.getIndex(), len);
                    long downloadedTotalSize = DownloadProgressHelper.getDownloadProgressValueByFileURL(fileInfo.getUrl());
                    if(null != listener) listener.onProgress(downloadedTotalSize, fileInfo.getTotalSize());

                    if(downloadedTotalSize == fileInfo.getTotalSize()){
                        if(null != listener) listener.onSuccess(downloadFile.getAbsolutePath());
                    }
                }
            }
        }catch (IOException e){
            Log.e(TAG, "Download bitmap failed.", e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{
                if(inputStream != null) inputStream.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            if(connection != null) connection.disconnect();
        }
    }
}

