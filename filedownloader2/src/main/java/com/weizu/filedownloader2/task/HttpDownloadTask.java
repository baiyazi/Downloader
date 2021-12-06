package com.weizu.filedownloader2.task;

import android.util.Log;

import com.weizu.filedownloader2.base.BaseHttpTask;
import com.weizu.filedownloader2.bean.DownloadEntity;
import com.weizu.filedownloader2.config.DownloadOptions;
import com.weizu.filedownloader2.listener.IDownloadListener;
import com.weizu.filedownloader2.services.DownloadProgressHelper;
import com.weizu.filedownloader2.utils.LogUtil;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class HttpDownloadTask extends BaseHttpTask implements IDownloadListener{
    private static final String TAG = HttpDownloadTask.class.getSimpleName();
    private IDownloadListener mListener;
    private int mHttpDownloadTaskIndex = -1;            // 下载关联的下标
    private DownloadEntity mDownloadEntity;
    private DownloadOptions mDownloadOptions;
    private List<long[]> mTaskBreakPoints;
    private DownloadProgressHelper mDownloadProgressHelper;
    private volatile boolean isPause = false;

    private HttpDownloadTask() {
    }

    public HttpDownloadTask(HttpFileInfoTask httpFileInfoTask, int taskIndex) {
        initHttpDownloadTask(httpFileInfoTask, taskIndex);
    }

    public HttpDownloadTask(HttpFileInfoTask httpFileInfoTask, int taskIndex, IDownloadListener listener) {
        initHttpDownloadTask(httpFileInfoTask, taskIndex);
        mListener = listener;
    }

    private void initHttpDownloadTask(HttpFileInfoTask httpFileInfoTask, int taskIndex){
        // 下载文件的任务分配类
        mHttpDownloadTaskIndex = taskIndex;
        mDownloadEntity = httpFileInfoTask.getDownloadEntity();
        mDownloadOptions = httpFileInfoTask.getDownloadOptions();
        mTaskBreakPoints = httpFileInfoTask.getTaskBreakPoints();
        mDownloadProgressHelper = DownloadProgressHelper.getInstance();
    }

    public void setDownloadListener(IDownloadListener listener) {
        mListener = listener;
    }


    public void pauseTask() {
        isPause = true;
    }

    public void restartDownloadTask(){
        isPause = false;
    }

    @Override
    protected boolean doRequest() {
        HttpURLConnection connection = null;
        try {
            // 获取当前Index对应的下载任务
            LogUtil.e(TAG, String.valueOf(mTaskBreakPoints == null));
            long[] currentDownloadTask = mTaskBreakPoints.get(mHttpDownloadTaskIndex);
            if(currentDownloadTask.length != 2) {
                onError("class: "+ TAG +". 任务分配出现错误");
                return false;
            }
            File localStorageFile = mDownloadEntity.getLocalStorageFile(mDownloadOptions.getDownloadDir());
            LogUtil.e(TAG, localStorageFile.getAbsolutePath());
            RandomAccessFile randomAccessFile = new RandomAccessFile(localStorageFile, "rwd");
            // 设置写入文件的开始位置
            randomAccessFile.seek(currentDownloadTask[0]);
            URL url = new URL(mDownloadEntity.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            mDownloadOptions.setConnectParameters(connection);
            LogUtil.e(TAG,  "bytes=" + currentDownloadTask[0] + "-" + currentDownloadTask[1]);
            connection.setRequestProperty("Range", "bytes=" + currentDownloadTask[0] + "-" + currentDownloadTask[1]);

            int responseCode = connection.getResponseCode();
            if(responseCode != HttpURLConnection.HTTP_PARTIAL){
                onError("class: "+ TAG +". 请求状态码出现错误！收到状态码" + responseCode);
            }

            InputStream inputStream = connection.getInputStream();

            if (connection.getResponseCode() == 206) {
                byte[] buffer = new byte[1024 * 1024 * 10];
                int len = -1;
                while ((len = inputStream.read(buffer)) != -1) {
                    if(isPause) {
                        onPause();
                        return true;
                    }
                    randomAccessFile.write(buffer, 0, len);
                    // todo 更新下载进度-内存
                    mDownloadProgressHelper.increasementDownloadProgressValue(mDownloadEntity.getMd5(), mHttpDownloadTaskIndex, len);
                    long downloadProgressValue = mDownloadProgressHelper.getDownloadProgressValueByFileMD5(mDownloadEntity.getMd5());
                    onProgress(downloadProgressValue, mDownloadEntity.getTotalSize());
                    if (downloadProgressValue == mDownloadEntity.getTotalSize()) {
                        onSuccess(localStorageFile.getAbsolutePath());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            onError(e.getMessage());
            return retry();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return true;
    }

    @Override
    public void onSuccess(String message) {
        if (mListener != null) {
            mListener.onSuccess(message);
        }
    }

    @Override
    public void onError(String msg) {
        if (mListener != null) {
            mListener.onError(msg);
        }
    }

    @Override
    public void onProgress(long currentPos, long totalLength) {
        if (mListener != null){
            mListener.onProgress(currentPos, totalLength);
        }
    }

    @Override
    public void onCancel() {
        if (mListener != null){
            mListener.onCancel();
        }
    }

    @Override
    public void onPause() {
        if (mListener != null){
            mListener.onPause();
        }
    }

    @Override
    public void onRestart() {
        if (mListener != null){
            mListener.onRestart();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HttpDownloadTask)) return false;
        HttpDownloadTask that = (HttpDownloadTask) o;
        return mHttpDownloadTaskIndex == that.mHttpDownloadTaskIndex &&
                isPause == that.isPause &&
                Objects.equals(mListener, that.mListener) &&
                Objects.equals(mDownloadEntity, that.mDownloadEntity) &&
                Objects.equals(mDownloadOptions, that.mDownloadOptions) &&
                Objects.equals(mTaskBreakPoints, that.mTaskBreakPoints) &&
                Objects.equals(mDownloadProgressHelper, that.mDownloadProgressHelper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mListener, mHttpDownloadTaskIndex, mDownloadEntity, mDownloadOptions, mTaskBreakPoints, mDownloadProgressHelper, isPause);
    }
}