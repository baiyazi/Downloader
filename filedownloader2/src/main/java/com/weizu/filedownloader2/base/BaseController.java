package com.weizu.filedownloader2.base;

import com.weizu.filedownloader2.bean.DownloadEntity;
import com.weizu.filedownloader2.listener.IDownloadListener;
import com.weizu.filedownloader2.utils.LogUtil;

/**
 * 下载控制类的基类，复写下载类需要继承自这个类
 * @author 梦否
 * @version 1.0.1
 * @since 1.0.1
 */
public abstract class BaseController {
    private static final String TAG = BaseController.class.getName();

    public BaseController(){
        LogUtil.d(TAG, " method: BaseController()");
    }

    /**
     * 开启下载
     * @param listener 下载监听接口
     */
    public abstract void download(IDownloadListener listener);

    /**
     * 暂停下载任务
     * @param entity 下载任务
     */
    public abstract void pause(DownloadEntity entity);

    /**
     * 停止下载任务，即清除所有和当前下载任务相关的记录和文件
     * @param entity 下载任务
     */
    public abstract void stop(DownloadEntity entity);

    /**
     * 恢复下载任务
     * @param entity 下载任务
     */
    public abstract void resume(DownloadEntity entity);
}
