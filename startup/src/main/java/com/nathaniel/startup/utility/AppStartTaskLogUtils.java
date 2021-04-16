package com.nathaniel.startup.utility;

import android.util.Log;

import com.nathaniel.startup.dispatcher.AppStartTaskDispatcher;

/**
 * @author nathaniel
 */
public class AppStartTaskLogUtils {
    private static final String TAG = AppStartTaskLogUtils.class.getSimpleName();

    public static void showLog(String log) {
        if (AppStartTaskDispatcher.getInstance().isDebuggable()) {
            Log.e(TAG, log);
        }
    }
}
