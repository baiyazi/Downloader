package com.weizu.filedownloader2.listener;

/**
 * 监听接口
 * @author  梦否
 * @version 1.0
 * @since 1.0
 */
public interface IDownloadListener {
    /**
     * 下载成功回调
     * @param filePath 下载后的文件全路径
     */
    void onSuccess(String filePath);

    /**
     * 下载失败
     * @param msg 失败提示消息
     */
    void onError(String msg);

    /**
     * 下载监听
     * @param currentPos 当前下载位置
     * @param totalLength 总长度
     */
    void onProgress(long currentPos, long totalLength);

    /**
     * 任务取消
     */
    void onCancel();

    /**
     * 暂停任务
     */
    void onPause();

    /**
     * 从新开始任务
     */
    void onRestart();
}
