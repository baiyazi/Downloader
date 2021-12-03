package com.weizu.filedownloader.listener;

/**
 * 监听接口
 * @author  梦否
 * @version 1.0
 * @since 1.0
 * @csdn https://blog.csdn.net/qq_26460841
 * @date 2021-11-16 09:57:12
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
}
