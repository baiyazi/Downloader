package com.weizu.filedownloader.controller;

import android.content.Context;

import com.weizu.filedownloader.base.BaseDownloadController;
import com.weizu.filedownloader.bean.FileInfo;
import com.weizu.filedownloader.config.ThreadPoolExectorConfig;
import com.weizu.filedownloader.enums.DownloadState;
import com.weizu.filedownloader.enums.FileSuffix;
import com.weizu.filedownloader.listener.IDownloadListener;
import com.weizu.filedownloader.services.AssignDownloadTask;
import com.weizu.filedownloader.services.SharedPreferencesHelper;

import java.util.concurrent.Executor;

public class DownloadController extends BaseDownloadController {
    private static final String TAG = "MultiThreadBreakpointDownloader";
    private String method = "GET";                                         // 请求方法
    private int connectionTimeout;                                         // 超时
    private String cacheDir = "WCache";                                    // 文件缓存目录
    private volatile boolean isPause = false;                              // 是否暂停
    private Executor executor;                                             // 线程池
    private int maximumPoolSize;                                           // 最大线程数
    private FileInfo fileInfo;                                             // 待下载文件信息
    private volatile DownloadState downloadState = DownloadState.BEFOREDOWNLOAD;    // 下载状态     //

    private DownloadController(){}

    public DownloadController(Context context){
        connectionTimeout = 500; // 500毫秒
        method = "GET";
        this.fileInfo = new FileInfo();
        this.fileInfo.setContext(context);
        this.initExecutor();
    }

    public DownloadController url(String url){
        this.fileInfo.setUrl(url);
        return this;
    }

    public DownloadController name(String newFileName){
        this.fileInfo.setNewFileName(newFileName);
        return this;
    }

    public DownloadController fileSuffix(FileSuffix fileSuffix){
        this.fileInfo.setSuffix(fileSuffix);
        return this;
    }

    public DownloadController cacheDir(String dir){
        this.cacheDir = dir;
        return this;
    }

    public DownloadController totalSize(long size){
        this.fileInfo.setTotalSize(size);
        return this;
    }

    public void setCacheDir(String cacheDir){
        this.cacheDir = cacheDir;
    }

    public String getCacheDir(){
        return cacheDir;
    }

    public void initExecutor(){
        ThreadPoolExectorConfig config = new ThreadPoolExectorConfig();
        executor = config.getExecutor();
        maximumPoolSize = config.getMaximumPoolSize();
    }

    // 多线程下需要加锁控制
    public synchronized void pauseDownload() {
        isPause = true;
        downloadState = DownloadState.PAUSE;
    }

    public synchronized boolean getPaused(){
        return isPause;
    }

    public synchronized void clearPause() {
        isPause = false;
        downloadState = DownloadState.BEFOREDOWNLOAD;
    }

    public DownloadController(Builder builder){
        this.fileInfo = new FileInfo();
        this.connectionTimeout = builder.connectionTimeout;
        this.fileInfo.setContext(builder.context);
        this.url(builder.url)
                .cacheDir(builder.cachePath)
                .fileSuffix(builder.suffix)
                .name(builder.newFileName)
                .initExecutor();
    }

    public static class Builder {
        private String url;
        private int connectionTimeout;
        private String cachePath = "imgs";
        private FileSuffix suffix;
        private final Context context;
        private String newFileName;

        public Builder(Context context){
            this.context = context;
        }

        public Builder url(String url){
            this.url = url;
            return this;
        }

        public Builder timeout(int ms){
            this.connectionTimeout = ms;
            return this;
        }

        public Builder name(String newFileName){
            this.newFileName = newFileName;
            return this;
        }

        public Builder suffix(FileSuffix suffix){
            this.suffix = suffix;
            return this;
        }

        public Builder cacheDirName(String cacheDirName){
            this.cachePath = cacheDirName;
            return this;
        }

        public DownloadController build(){
            return new DownloadController(this);
        }
    }

    public synchronized void setDownloadState(DownloadState downloadState) {
        this.downloadState = downloadState;
    }

    @Override
    public void download(IDownloadListener listener) {
        // 如果isPause值是暂停就不需要下载
        if(getPaused()) return;
        // 如果状态为下载中，就不需要在继续响应下载
        if(downloadState == DownloadState.DOWNLOADING) return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 初始化SharedPreferences文件
                downloadState = DownloadState.DOWNLOADING;
                SharedPreferencesHelper.initSharedPreferences(fileInfo.getContext());
                AssignDownloadTask task = new AssignDownloadTask(DownloadController.this, fileInfo);
                task.setListener(listener);
                try {
                    task.assignmentAndStart(connectionTimeout, method, maximumPoolSize, cacheDir, executor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
