package com.weizu.filedownloader.config;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 获取线程池的大小
 * @author 梦否
 * @version 1.0
 * @since 1.0
 * @csdn https://blog.csdn.net/qq_26460841
 * @date 2021-11-16 10:44:57

 */
public class ThreadPoolExectorConfig {
    private int corePoolSize = Runtime.getRuntime().availableProcessors() + 1;
    private int maximumPoolSize = Runtime.getRuntime().availableProcessors() + 1;
    private ThreadPoolExecutor executor;


    public ThreadPoolExectorConfig(){
    }

    public ThreadPoolExectorConfig(int corePoolSize, int maximumPoolSize){
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public ThreadFactory getmThreadFactory() {
        return mThreadFactory;
    }

    private final ThreadFactory mThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread#" + mCount.getAndIncrement());
        }
    };

    public Executor getExecutor(){
        if(executor != null) return executor;
        executor = new ThreadPoolExecutor(corePoolSize,
                maximumPoolSize,
                10L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                mThreadFactory);
        return executor;
    }
}
