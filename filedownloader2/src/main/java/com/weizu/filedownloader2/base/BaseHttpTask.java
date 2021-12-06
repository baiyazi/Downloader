package com.weizu.filedownloader2.base;

import com.weizu.filedownloader2.utils.LogUtil;

public abstract class BaseHttpTask implements Runnable {
    private static final String TAG = BaseHttpTask.class.getSimpleName();
    private static final int MAX_REQUEST_COUNT = 3;
    private int mRequestCount = 0;

    /**
     * 实际的请求方法，需要其子类来实现这个方法。
     * @return 是否请求成功
     */
    protected abstract boolean doRequest();

    @Override
    public void run() {
        do {
            LogUtil.d(TAG , " method: run(). Request time: " + mRequestCount);
            // 请求失败，就继续尝试；否则就退出
            if (doRequest()) break;
        } while (mRequestCount < MAX_REQUEST_COUNT);
        mRequestCount = 0;
    }

    protected boolean retry() {
        mRequestCount++;
        return mRequestCount >= MAX_REQUEST_COUNT;
    }
}
