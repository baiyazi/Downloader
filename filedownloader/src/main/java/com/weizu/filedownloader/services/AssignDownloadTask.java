package com.weizu.filedownloader.services;

import com.weizu.filedownloader.bean.FileDownloadPosition;
import com.weizu.filedownloader.bean.FileInfo;
import com.weizu.filedownloader.controller.DownloadController;
import com.weizu.filedownloader.enums.DownloadState;
import com.weizu.filedownloader.listener.IDownloadListener;
import com.weizu.filedownloader.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;

public class AssignDownloadTask {
    private String TAG = "AssignDownloadTaskThread";
    private FileInfo mFileInfo;
    private IDownloadListener listener;
    private DownloadController mDownloadController;

    public void setListener(IDownloadListener listener) {
        this.listener = listener;
    }

    public AssignDownloadTask(DownloadController controller, FileInfo fileInfo){
        this.mFileInfo = fileInfo;
        this.mDownloadController = controller;
    }

    public void setDownloadState(DownloadState downloadState) {
        mDownloadController.setDownloadState(downloadState);
    }

    public boolean getPaused(){
        return mDownloadController.getPaused();
    }

    public void assignmentAndStart(int connectionTimeout, String method, int maximumPoolSize, String cachePath, Executor executor) throws Exception{
        HttpURLConnection connection = null;
        File file = null;
        try {
            URL url1 = new URL(mFileInfo.getUrl());
            connection = (HttpURLConnection) url1.openConnection();
            connection.setConnectTimeout(connectionTimeout);
            connection.setRequestMethod(method);
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("accept", "*/*");
            connection.connect();

            // 获取文件总长度
            int totalLength = connection.getContentLength();
            file = mFileInfo.getDownloadStorageFile(cachePath);
            boolean equal = false;

            if((equal = totalLength == file.length())){
                if(listener != null) {
                    listener.onProgress(1, 1);
                    listener.onSuccess(file.getAbsolutePath());
                }
                mDownloadController.setDownloadState(DownloadState.DOWNLOADED);
                return;
            }

            mFileInfo.setTotalSize(totalLength);
            LogUtils.e(TAG, "文件总长度 ======> " + totalLength);

            // todo 分为多个线程下载
            long step = totalLength / maximumPoolSize;
            LogUtils.e(TAG, "每个线程下载的数据量大小为：" + step);

            DownloadProgressHelper.setSize(maximumPoolSize);
            for (int i = 0; i < maximumPoolSize; i++) {
                // 设置用户点击暂停响应
                if(this.mDownloadController.getPaused()){
                    mDownloadController.setDownloadState(DownloadState.PAUSE);
                    return;
                }

                DownloadThread downloadThread = null;
                FileDownloadPosition position = null;
                if(i != maximumPoolSize - 1) {
                    position = new FileDownloadPosition(AssignDownloadTask.this, mFileInfo.getUrl(), i * step, (i + 1) * step - 1, i);
                }else{
                    position = new FileDownloadPosition(AssignDownloadTask.this, mFileInfo.getUrl(), i * step, totalLength, i);
                }

                // 从SharedPreferences文件中读取已经下载的文件大小
                long size = SharedPreferencesHelper.readDownloadPosition(mFileInfo.getUrl(), i);
                // 设置大小到内存缓冲区
                DownloadProgressHelper.increasementDownloadProgressValue(mFileInfo.getUrl(), i, size);

                // todo 更新进度条
                if(null != listener)
                    listener.onProgress(DownloadProgressHelper.getDownloadProgressValueByFileURL(mFileInfo.getUrl()), totalLength);

                downloadThread = new DownloadThread(AssignDownloadTask.this, mFileInfo, cachePath, position);
                if(listener != null) downloadThread.setDownloadListener(listener);
                executor.execute(downloadThread);
            }
        }catch (IOException e){
            LogUtils.e(TAG, "Download bitmap failed.", e);
            if(listener != null) listener.onError(e.getLocalizedMessage());
        }finally {
            if(connection != null) connection.disconnect();
        }
    }
}
