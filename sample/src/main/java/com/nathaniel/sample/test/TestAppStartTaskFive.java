package com.nathaniel.sample.test;

import android.util.Log;

import com.nathaniel.startup.task.AppStartTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author nathaniel
 */
public class TestAppStartTaskFive extends AppStartTask {

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("Task:", "TestAppStartTaskFive执行耗时: " + (System.currentTimeMillis() - start));
    }

    @Override
    public List<Class<? extends AppStartTask>> getDependsTaskList() {
        List<Class<? extends AppStartTask>> dependsTaskList = new ArrayList<>();
        dependsTaskList.add(TestAppStartTaskThree.class);
        dependsTaskList.add(TestAppStartTaskTwo.class);
        return dependsTaskList;
    }

    @Override
    public Executor runOnExecutor() {
        return super.runOnExecutor();
    }

    @Override
    public boolean runningInMainThread() {
        return false;
    }


}
