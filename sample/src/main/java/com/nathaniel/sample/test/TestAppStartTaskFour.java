package com.nathaniel.sample.test;

import android.util.Log;

import com.nathaniel.startup.task.AppStartTask;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nathaniel
 */
public class TestAppStartTaskFour extends AppStartTask {

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("Task:", "TestAppStartTaskFour执行耗时: " + (System.currentTimeMillis() - start));
    }

    @Override
    public List<Class<? extends AppStartTask>> getDependsTaskList() {
        List<Class<? extends AppStartTask>> dependsTaskList = new ArrayList<>();
        dependsTaskList.add(TestAppStartTaskTwo.class);
        dependsTaskList.add(TestAppStartTaskThree.class);
        return dependsTaskList;
    }

    @Override
    public boolean runningInMainThread() {
        return false;
    }

}
