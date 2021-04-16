package com.nathaniel.startup.runnable;

import android.os.Process;

import com.nathaniel.startup.dispatcher.AppStartTaskDispatcher;
import com.nathaniel.startup.task.AppStartTask;


/**
 * @author nathaniel
 */
public class AppStartTaskRunnable implements Runnable {
    private final AppStartTask appStartTask;
    private final AppStartTaskDispatcher appStartTaskDispatcher;

    public AppStartTaskRunnable(AppStartTask appStartTask, AppStartTaskDispatcher appStartTaskDispatcher) {
        this.appStartTask = appStartTask;
        this.appStartTaskDispatcher = appStartTaskDispatcher;
    }

    @Override
    public void run() {
        Process.setThreadPriority(appStartTask.priority());
        appStartTask.waitToNotify();
        appStartTask.run();
        appStartTaskDispatcher.setNotifyChildren(appStartTask);
        appStartTaskDispatcher.markAppStartTaskFinish(appStartTask);
    }
}
