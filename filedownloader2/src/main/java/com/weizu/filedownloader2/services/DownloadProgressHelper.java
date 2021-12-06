package com.weizu.filedownloader2.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DownloadProgressHelper {
    private static final String TAG = DownloadProgressHelper.class.getSimpleName();
    // 根据线程数据来记录的已经下载文件大小 <file md5, long[mThreadNumber]>
    private volatile Map<String, long[]> map = new ConcurrentHashMap<>();
    private int mThreadNumber;

    public static DownloadProgressHelper getInstance(){
        return SingletonHolder.mDownloadProgressHelper;
    }

    private static class SingletonHolder{
        private static final DownloadProgressHelper mDownloadProgressHelper = new DownloadProgressHelper();
    }

    public void setThreadNumber(int number) {
        mThreadNumber = number;
    }

    /**
     * @param md5 下载文件链接
     * @return 多个线程总共已经下载文件大小
     */
    public synchronized long getDownloadProgressValueByFileMD5(String md5) throws Exception {
        if(mThreadNumber == 0) throw new Exception("class: "+ TAG +". 必须先传入开启下载的线程个数");
        long[] orDefault = map.getOrDefault(md5, new long[mThreadNumber]);
        long temp = 0L;
        assert orDefault != null;
        for (long l : orDefault) {
            temp += l;
        }
        return temp;
    }

    /**
     * 对应的线程index，的下载了多少
     * @param md5 待下载文件的md5值
     * @param index 线程下标
     * @param increment 下载增量
     * @throws Exception
     */
    public synchronized void increasementDownloadProgressValue(String md5, int index, long increment) throws Exception {
        if(mThreadNumber == 0) throw new Exception("class: "+ TAG +". 必须先传入开启下载的线程个数");
        long[] orDefault = map.getOrDefault(md5, new long[mThreadNumber]);
        assert orDefault != null;
        orDefault[index] += increment;
        map.put(md5, orDefault);
    }

    /**
     * 某个线程下载了多少
     * @param md5 待下载文件的md5值
     * @param index 线程下标
     * @return 该线程的下载量
     * @throws Exception
     */
    public synchronized long getFileSegmentDownloadProgressValueByIndex(String md5, int index) throws Exception {
        if(mThreadNumber == 0) throw new Exception("class: "+ TAG +". 必须先传入开启下载的线程个数");
        long[] orDefault = map.getOrDefault(md5, new long[mThreadNumber]);
        assert orDefault != null;
        return orDefault[index];
    }
}
