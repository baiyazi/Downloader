package com.weizu.filedownloader2;

import android.content.Context;

import com.weizu.filedownloader2.bean.DownloadEntity;
import com.weizu.filedownloader2.config.DownloadOptions;
import com.weizu.filedownloader2.enums.FileSuffix;
import com.weizu.filedownloader2.task.HttpDownloadTask;

import java.util.Arrays;
import java.util.Objects;

/**
 * 一个下载任务就是一个请求
 */
public class DownloadRequest {
    // 下载的文件的信息
    private DownloadEntity downloadEntity;
    // 下载的选项配置
    private DownloadOptions downloadOptions;
    // 真正下载的线程
    private HttpDownloadTask[] httpDownloadTasks;

    private DownloadRequest() {
    }

    public DownloadRequest(Context context) {
        downloadEntity = new DownloadEntity();
        downloadOptions = new DownloadOptions();
        downloadEntity.setContext(context);
        initHttpDownloadTasks(downloadOptions.getDefaultThreadNumber());
    }

    public void initHttpDownloadTasks(int threadNumber) {
        httpDownloadTasks = new HttpDownloadTask[threadNumber];
    }

    public DownloadRequest(Builder builder) {
        this(builder.context);
        // downloadEntity
        downloadEntity.setUrl(builder.url);
        downloadEntity.setSuffix(builder.suffix);
        downloadEntity.setNewFileName(builder.newFileName);
        // downloadOptions
        if (builder.METHOD != null) downloadOptions.setMethod(builder.METHOD);
        if (builder.CONNECT_TIME_OUT != 0)
            downloadOptions.setConnectTimeOut(builder.CONNECT_TIME_OUT);
        if (builder.DEFAULT_THREAD_NUMBER != 0) {
            downloadOptions.setDefaultThreadNumber(builder.DEFAULT_THREAD_NUMBER);
            initHttpDownloadTasks(builder.DEFAULT_THREAD_NUMBER);
        }
        if (builder.DOWNLOAD_DIR != null) downloadOptions.setDownloadDir(builder.DOWNLOAD_DIR);
    }


    public HttpDownloadTask[] getHttpDownloadTasks() {
        return httpDownloadTasks;
    }

    public DownloadEntity getDownloadEntity() {
        return downloadEntity;
    }

    public DownloadOptions getDownloadOptions() {
        return downloadOptions;
    }

    public static class Builder {
        private String url;
        private FileSuffix suffix;
        private String newFileName;
        private final Context context;
        private String METHOD;
        private int CONNECT_TIME_OUT;
        private int DEFAULT_THREAD_NUMBER;
        private String DOWNLOAD_DIR;


        public Builder(Context context) {
            this.context = context;
        }

        public DownloadRequest build(){
            return new DownloadRequest(this);
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder suffix(FileSuffix suffix) {
            this.suffix = suffix;
            return this;
        }

        public Builder newFileName(String newFileName) {
            this.newFileName = newFileName;
            return this;
        }

        public Builder method(String method) {
            this.METHOD = method;
            return this;
        }

        public Builder connectTimeOut(int connectTimeOut) {
            this.CONNECT_TIME_OUT = connectTimeOut;
            return this;
        }

        public Builder defaultThreadNumber(int defaultThreadNumber) {
            this.DEFAULT_THREAD_NUMBER = defaultThreadNumber;
            return this;
        }

        public Builder downloadDir(String downloadDir) {
            this.DOWNLOAD_DIR = downloadDir;
            return this;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DownloadRequest that = (DownloadRequest) o;
        return Objects.equals(downloadEntity, that.downloadEntity) &&
                Objects.equals(downloadOptions, that.downloadOptions) &&
                Arrays.equals(httpDownloadTasks, that.httpDownloadTasks);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(downloadEntity, downloadOptions);
        result = 31 * result + Arrays.hashCode(httpDownloadTasks);
        return result;
    }
}
