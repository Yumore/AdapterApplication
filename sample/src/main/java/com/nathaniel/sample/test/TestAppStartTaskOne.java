package com.nathaniel.sample.test;

import com.nathaniel.adapter.utility.LoggerUtils;
import com.nathaniel.startup.task.AppStartTask;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestAppStartTaskOne extends AppStartTask {
    private static final String TAG = TestAppStartTaskOne.class.getSimpleName();

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        BufferedReader bufferedReader;
        String line;
        List<String[]> stringsList = new ArrayList<>();
        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/net/xt_qtaguid/stats"));
            while ((line = bufferedReader.readLine()) != null) {
                LoggerUtils.logger(TAG, String.format("source content is : %s", line.trim()));
                if (line.contains("idx")) {
                    continue;
                }
                String[] values = line.trim().split("\\s+");
                stringsList.add(values);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LoggerUtils.logger(TAG, "TestAppStartTaskOne执行耗时: " + (System.currentTimeMillis() - start));
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
