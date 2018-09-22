package com.coderpage.concurrency;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author lc. 2018-05-27 14:35
 * @since 0.6.0
 */
public class MineExecutors {

    private static volatile MineExecutors sInstance = null;

    private Handler mHandler;
    private ThreadPoolExecutor mIoExecutor;
    private ThreadPoolExecutor mAsyncExecutor;

    private MineExecutors() {
        mHandler = new Handler(Looper.getMainLooper());
        mIoExecutor = new DefaultWorkExecutor();
        mAsyncExecutor = AsyncTaskExecutor.executor();
    }

    private static MineExecutors instance() {
        if (sInstance == null) {
            synchronized (MineExecutors.class) {
                if (sInstance == null) {
                    sInstance = new MineExecutors();
                }
            }
        }
        return sInstance;
    }

    public static ThreadPoolExecutor ioExecutor() {
        return instance().mIoExecutor;
    }

    public static ThreadPoolExecutor asyncExecutor() {
        return instance().mAsyncExecutor;
    }

    public static void executeOnUiThread(Runnable runnable) {
        instance().mHandler.post(runnable);
    }
}
