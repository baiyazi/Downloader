package com.weizu.filedownloader2.config;

import com.weizu.filedownloader2.base.BaseHttpTask;
import com.weizu.filedownloader2.utils.LogUtil;

import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.util.Objects;

/**
 * 下载配置类，配置一些默认的常用参数
 * @author 梦否
 * @version 1.0.1
 * @since 1.0.1
 */
public class DownloadOptions {
    private static final String TAG = DownloadOptions.class.getSimpleName();
    // 请求的方法
    private String METHOD = "GET";
    // 超时时间
    private int CONNECT_TIME_OUT = 5 * 1000;
    // 默认下载的线程数目
    private int DEFAULT_THREAD_NUMBER = 2;
    // 默认下载的文件位置，即：SD/WFileDownload
    private String DOWNLOAD_DIR = "WFileDownload";

    public void setDefaultThreadNumber(int defaultThreadNumber) {
        DEFAULT_THREAD_NUMBER = defaultThreadNumber;
    }


    public void setDownloadDir(String downloadDir) {
        DOWNLOAD_DIR = downloadDir;
    }

    public void setConnectTimeOut(int connectTimeOut) {
        CONNECT_TIME_OUT = connectTimeOut;
    }

    public int getConnectTimeOut() {
        return CONNECT_TIME_OUT;
    }

    public void setMethod(String method) {
        METHOD = method;
    }

    public String getMethod() {
        return METHOD;
    }

    public String getDownloadDir() {
        return DOWNLOAD_DIR;
    }

    public int getDefaultThreadNumber() {
        return DEFAULT_THREAD_NUMBER;
    }

    /**
     * 设置<code>HttpURLConnection</code>的默认请求参数
     * @param conn HttpURLConnection对象
     * @throws ProtocolException 请求协议异常
     */
    public void setConnectParameters(HttpURLConnection conn) throws ProtocolException {
        LogUtil.d(TAG, "method: setConnectParameters(HttpURLConnection).");
        conn.setConnectTimeout(CONNECT_TIME_OUT);
        conn.setRequestMethod(METHOD);
        StringBuilder accept = new StringBuilder();
        accept.append("image/gif, ")
                .append("image/jpeg, ")
                .append("image/pjpeg, ")
                .append("image/webp, ")
                .append("image/apng, ")
                .append("application/xml, ")
                .append("application/xaml+xml, ")
                .append("application/xhtml+xml, ")
                .append("application/x-shockwave-flash, ")
                .append("application/x-ms-xbap, ")
                .append("application/x-ms-application, ")
                .append("application/msword, ")
                .append("application/vnd.ms-excel, ")
                .append("application/vnd.ms-xpsdocument, ")
                .append("application/vnd.ms-powerpoint, ")
                .append("text/plain, ")
                .append("text/html, ")
                .append("*/*");
        conn.setRequestProperty("Accept", accept.toString());
        conn.setRequestProperty("Accept-Encoding", "identity");
        conn.setRequestProperty("Charset", "UTF-8");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        conn.setRequestProperty("Connection", "Keep-Alive");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DownloadOptions)) return false;
        DownloadOptions that = (DownloadOptions) o;
        return CONNECT_TIME_OUT == that.CONNECT_TIME_OUT &&
                DEFAULT_THREAD_NUMBER == that.DEFAULT_THREAD_NUMBER &&
                Objects.equals(METHOD, that.METHOD) &&
                Objects.equals(DOWNLOAD_DIR, that.DOWNLOAD_DIR);
    }

    @Override
    public int hashCode() {
        return Objects.hash(METHOD, CONNECT_TIME_OUT, DEFAULT_THREAD_NUMBER, DOWNLOAD_DIR);
    }
}
