package com.nathaniel.startup.dispatcher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;

import com.nathaniel.startup.runnable.AppStartTaskRunnable;
import com.nathaniel.startup.task.AppStartTask;
import com.nathaniel.startup.utility.AppStartTaskLogUtils;
import com.nathaniel.startup.utility.AppStartTaskSortUtils;
import com.nathaniel.startup.utility.ProcessUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nathaniel
 */
public class AppStartTaskDispatcher {
    /**
     * 所有任务需要等待的时间
     */
    private static final int WAITING_TIME = 10000;
    @SuppressLint("StaticFieldLeak")
    private static volatile AppStartTaskDispatcher sAppStartTaskDispatcher;
    /**
     * 存放每个Task  （key= Class < ? extends AppStartTask>）
     */
    private final HashMap<Class<? extends AppStartTask>, AppStartTask> mTaskHashMap;
    /**
     * 每个Task的孩子 （key= Class < ? extends AppStartTask>）
     */
    private final HashMap<Class<? extends AppStartTask>, List<Class<? extends AppStartTask>>> mTaskChildHashMap;
    /**
     * 通过Add添加进来的所有任务
     */
    private final List<AppStartTask> mStartTaskList;
    /**
     * 拓扑排序后的主线程的任务
     */
    private final List<AppStartTask> sortMainThreadTaskList;
    /**
     * 拓扑排序后的子线程的任务
     */
    private final List<AppStartTask> sortThreadPoolTaskList;
    /**
     * 需要等待的任务总数，用于CountDownLatch
     */
    private final AtomicInteger atomicInteger;
    private Context mContext;
    /**
     * 是否在主进程
     */
    private boolean isInMainProgress;
    /**
     * 拓扑排序后的所有任务
     */
    private List<AppStartTask> mSortTaskList;
    /**
     * 需要等待的任务总数，用于阻塞
     */
    private CountDownLatch mCountDownLatch;
    /**
     * 所有的任务开始时间，结束时间
     */
    private long startTime;
    /**
     * 所有阻塞任务的总超时时间
     */
    private long allTaskWaitTimeOut;
    private boolean debuggable;

    private AppStartTaskDispatcher() {
        mTaskHashMap = new HashMap<>();
        mTaskChildHashMap = new HashMap<>();
        mStartTaskList = new ArrayList<>();
        atomicInteger = new AtomicInteger();
        sortMainThreadTaskList = new ArrayList<>();
        sortThreadPoolTaskList = new ArrayList<>();
    }

    public static AppStartTaskDispatcher getInstance() {
        if (sAppStartTaskDispatcher == null) {
            synchronized (AppStartTaskDispatcher.class) {
                if (sAppStartTaskDispatcher == null) {
                    sAppStartTaskDispatcher = new AppStartTaskDispatcher();
                }
            }
        }
        return sAppStartTaskDispatcher;
    }

    public AppStartTaskDispatcher setAllTaskWaitTimeOut(long allTaskWaitTimeOut) {
        this.allTaskWaitTimeOut = allTaskWaitTimeOut;
        return this;
    }

    public boolean isDebuggable() {
        return debuggable;
    }

    public AppStartTaskDispatcher setDebuggable(boolean debuggable) {
        this.debuggable = debuggable;
        return this;
    }

    public AppStartTaskDispatcher setContext(Context context) {
        mContext = context;
        isInMainProgress = ProcessUtils.isMainProcess(mContext);
        return this;
    }

    public AppStartTaskDispatcher addAppStartTask(AppStartTask appStartTask) {
        if (appStartTask == null) {
            throw new RuntimeException("addAppStartTask() 传入的appStartTask为null");
        }
        mStartTaskList.add(appStartTask);
        if (ifNeedWait(appStartTask)) {
            atomicInteger.getAndIncrement();
        }
        return this;
    }

    public AppStartTaskDispatcher start() {
        if (mContext == null) {
            throw new RuntimeException("context为null，调用start()方法前必须调用setContext()方法");
        }
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new RuntimeException("start方法必须在主线程调用");
        }
        if (!isInMainProgress) {
            AppStartTaskLogUtils.showLog("当前进程非主进程");
            return this;
        }
        startTime = System.currentTimeMillis();
        //拓扑排序，拿到排好序之后的任务队列
        mSortTaskList = AppStartTaskSortUtils.getSortResult(mStartTaskList, mTaskHashMap, mTaskChildHashMap);
        initRealSortTask();
        printSortTask();
        mCountDownLatch = new CountDownLatch(atomicInteger.get());
        dispatchAppStartTask();
        return this;
    }

    /**
     * 分别处理主线程和子线程的任务
     */
    private void initRealSortTask() {
        for (AppStartTask appStartTask : mSortTaskList) {
            if (appStartTask.runningInMainThread()) {
                sortMainThreadTaskList.add(appStartTask);
            } else {
                sortThreadPoolTaskList.add(appStartTask);
            }
        }
    }

    /**
     * 输出排好序的Task
     */
    private void printSortTask() {
        StringBuilder sb = new StringBuilder();
        sb.append("当前所有任务排好的顺序为：");
        for (int i = 0; i < mSortTaskList.size(); i++) {
            String taskName = mSortTaskList.get(i).getClass().getSimpleName();
            if (i == 0) {
                sb.append(taskName);
            } else {
                sb.append("---＞");
                sb.append(taskName);
            }
        }
        AppStartTaskLogUtils.showLog(sb.toString());
    }

    /**
     * 发送任务
     */
    private void dispatchAppStartTask() {
        //再发送非主线程的任务
        for (AppStartTask appStartTask : sortThreadPoolTaskList) {
            if (!appStartTask.runningInMainThread()) {
                appStartTask.runOnExecutor().execute(new AppStartTaskRunnable(appStartTask, this));
            }
        }
        //再发送主线程的任务，防止主线程任务阻塞，导致子线程任务不能立刻执行
        for (AppStartTask appStartTask : sortMainThreadTaskList) {
            if (appStartTask.runningInMainThread()) {
                new AppStartTaskRunnable(appStartTask, this).run();
            }
        }
    }

    /**
     * 通知Children一个前置任务已完成
     */
    public void setNotifyChildren(AppStartTask appStartTask) {
        List<Class<? extends AppStartTask>> arrayList = mTaskChildHashMap.get(appStartTask.getClass());
        if (arrayList != null && arrayList.size() > 0) {
            for (Class<? extends AppStartTask> aclass : arrayList) {
                mTaskHashMap.get(aclass).notified();
            }
        }
    }

    /**
     * 标记已经完成的Task
     */
    public void markAppStartTaskFinish(AppStartTask appStartTask) {
        AppStartTaskLogUtils.showLog("任务完成了：" + appStartTask.getClass().getSimpleName());
        if (ifNeedWait(appStartTask)) {
            mCountDownLatch.countDown();
            atomicInteger.getAndDecrement();
        }
    }

    /**
     * 是否需要等待，主线程的任务本来就是阻塞的，所以不用管
     */
    private boolean ifNeedWait(AppStartTask task) {
        return !task.runningInMainThread() && task.waitEnable();
    }

    /**
     * 等待，阻塞主线程
     */
    public void await() {
        try {
            if (mCountDownLatch == null) {
                throw new RuntimeException("在调用await()之前，必须先调用start()");
            }
            if (allTaskWaitTimeOut == 0) {
                allTaskWaitTimeOut = WAITING_TIME;
            }
            mCountDownLatch.await(allTaskWaitTimeOut, TimeUnit.MILLISECONDS);
            long mFinishTime = System.currentTimeMillis() - startTime;
            AppStartTaskLogUtils.showLog("启动耗时：" + mFinishTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
