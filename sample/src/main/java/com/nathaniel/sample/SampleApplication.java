package com.nathaniel.sample;

import android.content.Context;

import com.nathaniel.sample.test.TestAppStartTaskFive;
import com.nathaniel.sample.test.TestAppStartTaskFour;
import com.nathaniel.sample.test.TestAppStartTaskThree;
import com.nathaniel.sample.test.TestAppStartTaskTwo;
import com.nathaniel.startup.dispatcher.AppStartTaskDispatcher;

import eu.faircode.netguard.ApplicationEx;

/**
 * @author nathaniel
 * @version V1.0.0
 * @contact <a href="mailto:nathanwriting@126.com">contact me</a>
 * @package com.nathaniel.sample
 * @datetime 4/16/21 - 10:44 AM
 */
public class SampleApplication extends ApplicationEx {

    @Override
    public void onCreate() {
        super.onCreate();
        addTasks(getApplicationContext());
    }

    private void addTasks(Context context) {
        if (MultiDexUtils.isMainProcess(context)) {
            return;
        }
        AppStartTaskDispatcher.getInstance()
                .setContext(context)
                .setDebuggable(true)
                .setAllTaskWaitTimeOut(1000)
                .addAppStartTask(new TestAppStartTaskTwo())
                .addAppStartTask(new TestAppStartTaskFour())
                .addAppStartTask(new TestAppStartTaskFive())
                .addAppStartTask(new TestAppStartTaskThree())
                // .addAppStartTask(new TestAppStartTaskOne())
                .start()
                .await();
    }
}
