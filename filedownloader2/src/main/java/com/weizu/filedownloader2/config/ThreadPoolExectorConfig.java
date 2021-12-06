package com.weizu.filedownloader2.config;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 获取线程池的大小
 * @author 梦否
 * @version 1.0.1
 * @since 1.0
 */
public class ThreadPoolExectorConfig {
    private volatile ThreadPoolExecutor mExecutor;
    private int mDefaultThreadNumber;

    private ThreadPoolExectorConfig(){
    }

    public ThreadPoolExectorConfig(int defaultThreadNumber){
        mDefaultThreadNumber = defaultThreadNumber;
    }

    public void setDefaultThreadNumber(int mDefaultThreadNumber) {
        this.mDefaultThreadNumber = mDefaultThreadNumber;
    }

    public ThreadFactory getThreadFactory() {
        return mThreadFactory;
    }

    private final ThreadFactory mThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread#" + mCount.getAndIncrement());
        }
    };

    public synchronized ThreadPoolExecutor getExecutor(){
        if(mExecutor != null) return mExecutor;
        mExecutor = new ThreadPoolExecutor(1,
                mDefaultThreadNumber,
                10L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                mThreadFactory);
        return mExecutor;
    }
}
