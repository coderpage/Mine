package com.coderpage.concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DefaultWorkExecutor extends ThreadPoolExecutor {
    private static final int CPU_SIZE = Runtime.getRuntime().availableProcessors();
    private static final int CORE_SIZE;
    private static final int MAX_SIZE;
    private static final long KEEP_ALIVE_TIME = 1L;

    public DefaultWorkExecutor() {
        this(CORE_SIZE, MAX_SIZE, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
    }

    static {
        // 线程池核心线程数为 cpu 核心数加一
        CORE_SIZE = CPU_SIZE + 1;
        // 线程池最大线程数为 cpu 核心数2被加一
        MAX_SIZE = CPU_SIZE * 2 + 1;
    }

    private DefaultWorkExecutor(int corePoolSize,
                                int maximumPoolSize,
                                long keepAliveTime,
                                TimeUnit unit,
                                BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

}
