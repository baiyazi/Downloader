package com.weizu.filedownloader2.task;

import com.weizu.filedownloader2.base.BaseHttpTask;
import com.weizu.filedownloader2.bean.DownloadEntity;
import com.weizu.filedownloader2.config.DownloadOptions;
import com.weizu.filedownloader2.listener.IFileInfoTaskCallBack;
import com.weizu.filedownloader2.services.DownloadProgressHelper;
import com.weizu.filedownloader2.services.SharedPreferencesHelper;
import com.weizu.filedownloader2.utils.EncoderUtil;
import com.weizu.filedownloader2.utils.FormatUtil;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 获取文件大小信息的线程类，用于将文件分段下载，多个线程共同下载一个文件
 * <p>在1.0中为DownloadThread
 *
 * @author 梦否
 * @version 1.0.1
 * @since 1.0
 */
public class HttpFileInfoTask extends BaseHttpTask implements IFileInfoTaskCallBack {
    private static final String TAG = HttpFileInfoTask.class.getSimpleName();
    private DownloadEntity mDownloadEntity;
    private IFileInfoTaskCallBack mListener;
    private DownloadOptions mDownloadOptions;
    private SharedPreferencesHelper mSharedPreferencesHelper;
    private DownloadProgressHelper mDownloadProgressHelper;
    private volatile boolean isPause = false;
    private List<long[]> taskBreakPoints;

    private HttpFileInfoTask() {
    }

    public HttpFileInfoTask(DownloadEntity downloadEntity, DownloadOptions downloadOptions) {
        mDownloadEntity = downloadEntity;
        mDownloadOptions = downloadOptions;
        try {
            getSharedPreferences();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDownloadProgressHelper = DownloadProgressHelper.getInstance();
        mDownloadProgressHelper.setThreadNumber(mDownloadOptions.getDefaultThreadNumber());
    }

    private void getSharedPreferences() throws Exception {
        if (SharedPreferencesHelper.getInstance() == null) {
            throw new Exception("class: " + TAG + ". SharedPreferencesHelper.initSharedPreferences()方法应该首先被初始化。");
        }
        mSharedPreferencesHelper = SharedPreferencesHelper.getInstance();
    }

    public HttpFileInfoTask(DownloadEntity downloadEntity, DownloadOptions downloadOptions, IFileInfoTaskCallBack listener) {
        mDownloadEntity = downloadEntity;
        mDownloadOptions = downloadOptions;
        mListener = listener;
        try {
            getSharedPreferences();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDownloadProgressHelper = DownloadProgressHelper.getInstance();
        mDownloadProgressHelper.setThreadNumber(mDownloadOptions.getDefaultThreadNumber());
    }

    public void setListener(IFileInfoTaskCallBack listener) {
        this.mListener = listener;
    }

    public DownloadEntity getDownloadEntity() {
        return mDownloadEntity;
    }

    public DownloadOptions getDownloadOptions() {
        return mDownloadOptions;
    }

    @Override
    protected boolean doRequest() {
        HttpURLConnection connection = null;
        try {
            if (isPause) {
                return true;
            }
            if (FormatUtil.checkNULL(mDownloadEntity.getUrl())) {
                throw new Exception("class: " + TAG + ". URL is invalid.");
            }

            URL url = new URL(FormatUtil.convertUrl(mDownloadEntity.getUrl()));
            connection = (HttpURLConnection) url.openConnection();
            mDownloadOptions.setConnectParameters(connection);
            connection.connect();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return false;
            }

            long fileSize = connection.getContentLength();
            InputStream inputStream = connection.getInputStream();
            mDownloadEntity.setMd5(EncoderUtil.getMD5(inputStream));
            mDownloadEntity.setTotalSize(fileSize);
            inputStream.close();
            // 分为如下几步进行
            /**
             * 1. 如果下载目录中存在同名文件，先比较大小，然后比较MD5值
             * 2. 如果MD5值也相等，就表示下载过了，不需要重复下载。
             * 3. 如果目录中没有，就找SharedPreferences中是否有MD5的下载记录
             * 4. 如果有记录，就读取记录，继续断点续传
             * 5. 如果没有记录，就新建下载任务进行下载，并记录下载位置
             */
            File localStorageFile = mDownloadEntity.getLocalStorageFile(mDownloadOptions.getDownloadDir());
            long localFileSize = localStorageFile.length();
            if (localFileSize == fileSize) {
                // 比较文件流的MD5值
                if (EncoderUtil.checkMD5(mDownloadEntity.getMd5(), localStorageFile))
                    onComplete("class: " + TAG + ". 文件已存在，目录为：" + localStorageFile.toString(), IFileInfoTaskCallBack.FILE_DOWNLOADED);
            } else {
                // 查找SP中是否有相关记录
                int defaultThreadNumber = mDownloadOptions.getDefaultThreadNumber();
                taskBreakPoints = new ArrayList<>();
                long stepSize = fileSize / defaultThreadNumber;
                long start, end;
                for (int i = 0; i < defaultThreadNumber; i++) {
                    if (isPause) {
                        return true;
                    }
                    start = i * stepSize;
                    if (i != defaultThreadNumber - 1) {
                        end = (i + 1) * stepSize - 1;
                    } else {
                        end = fileSize;
                    }
                    long position = mSharedPreferencesHelper.readDownloadPosition(mDownloadEntity.getMd5(), i);
                    if (position != 0L) { // 有记录，设置断点下载
                        taskBreakPoints.add(new long[]{position, end});
                        // 并且应该在内存中记录文件的进度
                        mDownloadProgressHelper.increasementDownloadProgressValue(mDownloadEntity.getMd5(), i, end - position);
                    } else { // 没有有记录，从理论位置开始
                        taskBreakPoints.add(new long[]{start, end});
                        // 并且应该在内存中记录文件的进度
                        mDownloadProgressHelper.increasementDownloadProgressValue(mDownloadEntity.getMd5(), i, 0L);
                    }
                }
                onComplete("class: " + TAG + ". 下载任务分配完成，一共分配给了" + mDownloadOptions.getDefaultThreadNumber() + "个线程处理。", IFileInfoTaskCallBack.FILE_INFO_REQUEST_SUCCESS);
            }
        } catch (Exception e) {
            onError(e);
            return retry();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return true;
    }

    /**
     * 获取分配的任务的起始点和结束点
     *
     * @return List<long [ ]>
     */
    public List<long[]> getTaskBreakPoints() {
        return taskBreakPoints;
    }

    public synchronized void pauseTask() {
        isPause = true;
    }

    public synchronized void restartTask() {
        isPause = false;
    }

    @Override
    public void onComplete(String msg, int code) {
        try {
            if (mListener != null)
                mListener.onComplete(msg, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancel() {
        if (mListener != null)
            mListener.onCancel();
    }

    @Override
    public void onError(Exception e) {
        if (mListener != null)
            mListener.onError(e);
    }
}
