package com.weizu.filedownloader2;

import com.weizu.filedownloader2.config.ThreadPoolExectorConfig;
import com.weizu.filedownloader2.listener.IDownloadListener;
import com.weizu.filedownloader2.listener.IFileInfoTaskCallBack;
import com.weizu.filedownloader2.services.SharedPreferencesHelper;
import com.weizu.filedownloader2.task.HttpDownloadTask;
import com.weizu.filedownloader2.task.HttpFileInfoTask;
import com.weizu.filedownloader2.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class DownloadTask {
    private static final String TAG = DownloadTask.class.getSimpleName();
    private volatile List<HttpFileInfoTask> mHttpFileInfoTasks;
    private volatile List<DownloadRequest> mDownloadRequests;
    private ThreadPoolExectorConfig mThreadPoolExectorConfig;
    private ExecutorService mHttpFileInfoFixedThreadPool = Executors.newFixedThreadPool(1);
    private ThreadPoolExecutor mHttpDownloadTaskExecutor;

    public DownloadTask() {
        mHttpFileInfoTasks = new ArrayList<>();
        mDownloadRequests = new ArrayList<>();
    }

    public void addTask(DownloadRequest downloadRequest, IDownloadListener listener) {
        HttpFileInfoTask fileInfoTask = new HttpFileInfoTask(downloadRequest.getDownloadEntity(), downloadRequest.getDownloadOptions());
        if(mThreadPoolExectorConfig == null){
            mThreadPoolExectorConfig = new ThreadPoolExectorConfig(downloadRequest.getDownloadOptions().getDefaultThreadNumber());
        }
        fileInfoTask.setListener(new IFileInfoTaskCallBack() {
            @Override
            public void onComplete(String msg, int code) throws Exception {
                LogUtil.d(TAG, msg);
                if(code == IFileInfoTaskCallBack.FILE_DOWNLOADED){
                    listener.onSuccess(downloadRequest.getDownloadEntity().getLocalStorageFile(downloadRequest.getDownloadOptions().getDownloadDir()).toString());
                    // 删除SP临时数据文件
                    SharedPreferencesHelper.getInstance().deleteSharedPreferenceRecord(downloadRequest.getDownloadEntity().getMd5());
                }else if(code  == IFileInfoTaskCallBack.FILE_INFO_REQUEST_SUCCESS){
                    // 请求完成后，就可以进行分多个线程下载
                    if(mHttpDownloadTaskExecutor == null) mHttpDownloadTaskExecutor = mThreadPoolExectorConfig.getExecutor();
                    for (int i = 0; i < downloadRequest.getDownloadOptions().getDefaultThreadNumber(); i++) {
                        HttpDownloadTask downloadTask = (downloadRequest.getHttpDownloadTasks()[i] =
                                new HttpDownloadTask(fileInfoTask, i, listener));
                        // 开启线程池下载
                        mHttpDownloadTaskExecutor.execute(downloadTask);
                    }
                }else{
                    throw new Exception("Error Code.");
                }
            }

            @Override
            public void onCancel() {
                listener.onCancel();
            }

            @Override
            public void onError(Exception e) {
                listener.onError(e.getMessage());
            }
        });
        mHttpFileInfoTasks.add(fileInfoTask);
        mDownloadRequests.add(downloadRequest);
    }

    /**
     * 开始下载，也就是加入到线程池中
     */
    public void startAllTask() {
        for (HttpFileInfoTask httpFileInfoTask : mHttpFileInfoTasks) {
            mHttpFileInfoFixedThreadPool.execute(httpFileInfoTask);
        }
    }

    /**
     * 通过DownloadRequest来开启下载任务
     * @param downloadRequest 下载请求
     * @throws Exception 异常
     */
    public void startTaskByRequest(DownloadRequest downloadRequest) throws Exception {
        int index = getIndexByDownloadRequest(downloadRequest);

        if( index == -1) {
            throw new Exception("DownloadTask ==> method: pauseTask() 中数组下标越界！");
        }

        HttpFileInfoTask fileInfoTask = mHttpFileInfoTasks.get(index);
        mHttpFileInfoFixedThreadPool.execute(fileInfoTask);
    }

    /**
     * 暂停任务逻辑：
     * 1. 暂停HttpInfoTask中的正在执行的线程；
     * 2. 暂停每个任务的HttpDownloadTask的每个线程；
     * 3. 保持每个文件中多个线程的下载状态到SharedPreferences文件中；
     */
    public void pauseAllTask() {
        // 或者直接使用
        // mHttpFileInfoFixedThreadPool.shutdownNow();
        // mHttpDownloadTaskExecutor.shutdownNow();

        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance();
        for (int i = 0; i < mDownloadRequests.size(); i++) {
            DownloadRequest downloadRequest = mDownloadRequests.get(i);
            HttpDownloadTask[] httpDownloadTasks = downloadRequest.getHttpDownloadTasks();
            HttpFileInfoTask fileInfoTask = mHttpFileInfoTasks.get(i);
            String md5 = downloadRequest.getDownloadEntity().getMd5();
            List<long[]> taskBreakPoints = fileInfoTask.getTaskBreakPoints();
            // 1. 暂停HttpInfoTask中的正在执行的线程
            int defaultThreadNumber = fileInfoTask.getDownloadOptions().getDefaultThreadNumber();
            for (int j = 0; j < defaultThreadNumber; j++) {
                // 2. 暂停每个任务的HttpDownloadTask的每个线程
                httpDownloadTasks[j].pauseTask();
                // 3. 保持每个文件中多个线程的下载状态到SharedPreferences文件中
                long[]  theoretic_startAndEnd = taskBreakPoints.get(j);
                long thread_downloaded_position = sharedPreferencesHelper.readDownloadPosition(md5, j);
                sharedPreferencesHelper.storageDownloadPosition(md5, j, (thread_downloaded_position + theoretic_startAndEnd[0]));
            }
        }
    }

    /**
     * 暂停任务逻辑：
     * 1. 暂停HttpInfoTask中的正在执行的线程；
     * 2. 暂停每个任务的HttpDownloadTask的每个线程；
     * 3. 保持每个文件中多个线程的下载状态到SharedPreferences文件中；
     */
    public void pauseTaskByRequest (DownloadRequest downloadRequest) throws Exception {
        int index = getIndexByDownloadRequest(downloadRequest);

        if( index == -1) {
            throw new Exception("DownloadTask ==> method: pauseTask() 中数组下标越界！");
        }

        HttpFileInfoTask fileInfoTask = mHttpFileInfoTasks.get(index);
        HttpDownloadTask[] httpDownloadTasks = mDownloadRequests.get(index).getHttpDownloadTasks();

        fileInfoTask.pauseTask();
        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance();
        int defaultThreadNumber = downloadRequest.getDownloadOptions().getDefaultThreadNumber();
        List<long[]> taskBreakPoints = fileInfoTask.getTaskBreakPoints();
        String md5 = downloadRequest.getDownloadEntity().getMd5();
        for (int i = 0; i < defaultThreadNumber; i++) {
            HttpDownloadTask httpDownloadTask = httpDownloadTasks[i];
            httpDownloadTask.pauseTask();

            long[]  theoretic_startAndEnd = taskBreakPoints.get(i);
            long thread_downloaded_position = sharedPreferencesHelper.readDownloadPosition(md5, i);
            sharedPreferencesHelper.storageDownloadPosition(md5, i, (thread_downloaded_position + theoretic_startAndEnd[0]));
        }
    }

    /**
     * 从传入的DownloadRequest来查找对应的加入到任务列表的下标
     * @param downloadRequest 下载请求
     * @return int 下标位置
     */
    private int getIndexByDownloadRequest(DownloadRequest downloadRequest) {
        // 找到这个downloadRequest对应的HttpFileInfoTask
        if(mDownloadRequests.size() == 0) return -1;
        int index = 0;
        for (; index < mDownloadRequests.size(); index++) {
            DownloadRequest mDownloadRequest = mDownloadRequests.get(index);
            if (mDownloadRequest.equals(downloadRequest)) {
                break;
            }
        }
        return index;
    }

    /**
     * 重新下载，其实也就是下载
     */
    public void restartAllTask () {
        startAllTask();
    }


    /**
     * 重新下载某个任务，通过DownloadRequest来标识
     * @param downloadRequest 下载请求
     * @throws Exception 异常
     */
    public void restartTaskByDownloadRequest(DownloadRequest downloadRequest) throws Exception {
        startTaskByRequest(downloadRequest);
    }

}
