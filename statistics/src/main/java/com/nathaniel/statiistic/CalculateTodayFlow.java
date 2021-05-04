package com.nathaniel.statiistic;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.util.Log;

import static com.nathaniel.statiistic.StatisticsActivity.TAG;

/**
 * Created by small on 2016/9/30.
 */

public class CalculateTodayFlow {
    long calculate_file(Context context) {
        return 0;
    }

    long calculate(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        long result;//1 当日使用流量
        long cur_boot_mobiletx = TrafficStats.getMobileTxBytes();
        long cur_boot_mobilerx = TrafficStats.getMobileRxBytes();
        long thisbootflow = cur_boot_mobilerx + cur_boot_mobiletx;//4

        editor.putLong("thisbootflow", thisbootflow);
        //Log.d("qiang", "thisbootflow:" + thisbootflow);
        long onedaylastbootflow = sharedPreferences.getLong("onedaylastbootflow", 0);//5
        long onebootlastdayflow = sharedPreferences.getLong("onebootlastdayflow", 0);//6
        result = thisbootflow + onedaylastbootflow - onebootlastdayflow;

        editor.putLong("curdayflow", result);
        Log.d(TAG, "curdayflow:" + result);
        editor.commit();
        return result;
    }
}
