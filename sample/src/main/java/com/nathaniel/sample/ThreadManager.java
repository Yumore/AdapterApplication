package com.nathaniel.sample;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Nathaniel
 * @date 2019/12/7 - 17:05
 */
public class ThreadManager {
    private static ThreadManager threadManager;
    private final int corePoolSize;
    private final int maximumPoolSize;
    private final long keepAliveTime;
    private ThreadPoolExecutor threadPoolExecutor;

    private ThreadManager(int corePoolSize, int maximumPoolSize, long keepAliveTime) {
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
    }

    public static synchronized ThreadManager getInstance() {
        if (threadManager == null) {
            threadManager = new ThreadManager(4, 10, 2500);
        }
        return threadManager;
    }

    public void executor(Runnable runnable) {
        if (threadPoolExecutor == null || threadPoolExecutor.isShutdown()) {
            ThreadFactory threadFactory = Executors.defaultThreadFactory();
            threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), threadFactory);
        }
        threadPoolExecutor.execute(runnable);
    }
}
