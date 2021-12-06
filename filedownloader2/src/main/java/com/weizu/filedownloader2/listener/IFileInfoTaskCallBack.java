package com.weizu.filedownloader2.listener;

/**
 * 请求文件信息的线程的监听回调接口
 */
public interface IFileInfoTaskCallBack {
    int FILE_DOWNLOADED = 0x11;
    int FILE_INFO_REQUEST_SUCCESS = 0x12;

    void onComplete(String msg, int code) throws Exception;
    void onCancel();
    void onError(Exception e);
}
