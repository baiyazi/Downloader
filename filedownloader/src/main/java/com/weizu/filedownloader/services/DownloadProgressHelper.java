package com.weizu.mylibrary2.services;

import java.util.HashMap;
import java.util.Map;

public class DownloadProgressHelper {

    // 根据线程数据来记录的已经下载文件大小
    private static Map<String, long[]> map;
    private static int mThreadNumber;

    static {
        map = new HashMap<>();
    }

    public static void setSize(int number) {
        DownloadProgressHelper.mThreadNumber = number;
    }

    public static int getSize() {
        return mThreadNumber;
    }

    /**
     * @param url 下载文件链接
     * @return 多个线程总共已经下载文件大小
     */
    public static long getDownloadProgressValueByFileURL(String url) throws Exception {
        if(mThreadNumber == 0) throw new Exception("必须先传入开启下载的线程个数");
        long[] orDefault = map.getOrDefault(url, new long[mThreadNumber]);
        long temp = 0L;
        assert orDefault != null;
        for (long l : orDefault) {
            temp += l;
        }
        return temp;
    }

    public static void increasementDownloadProgressValue(String url, int index, long value) throws Exception {
        if(mThreadNumber == 0) throw new Exception("必须先传入开启下载的线程个数");
        long[] orDefault = map.getOrDefault(url, new long[mThreadNumber]);
        assert orDefault != null;
        orDefault[index] += value;
        map.put(url, orDefault);
    }

    public static long getFileSegmentDownloadProgressValueByIndex(String url, int index) throws Exception {
        if(mThreadNumber == 0) throw new Exception("必须先传入开启下载的线程个数");
        long[] orDefault = map.getOrDefault(url, new long[mThreadNumber]);
        assert orDefault != null;
        return orDefault[index];
    }
}
