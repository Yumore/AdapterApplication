package com.nathaniel.statiistic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

/**
 * @author Nathaniel
 * @date 2016/11/25
 */

public class AlarmFreeReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmFreeReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "快速更新广播收到");
        SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (defaultPreferences.getBoolean("free", true)) {
            CalculateTodayFlow calculateTodayFlow = new CalculateTodayFlow();
            long curdayflow = calculateTodayFlow.calculate(context);
            long curfreetimeflow = sharedPreferences.getLong("curfreetimeflow", 0);
            editor.putLong("freetimeflowstart", curdayflow);
            editor.putLong("curmonthfreeflow", sharedPreferences.getLong("curmonthfreeflow", 0) + curfreetimeflow);
            boolean flag = editor.commit();
            Log.d(TAG, "flag = " + flag);
        }
    }
}
