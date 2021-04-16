package com.nathaniel.sample.test;

import android.util.Log;

import com.nathaniel.startup.task.AppStartTask;

import java.util.List;

public class TestAppStartTaskOne extends AppStartTask {

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("Task:", "TestAppStartTaskOne执行耗时: " + (System.currentTimeMillis() - start));
    }

    @Override
    public List<Class<? extends AppStartTask>> getDependsTaskList() {
        return null;
    }

    @Override
    public boolean runningInMainThread() {
        return true;
    }
}
