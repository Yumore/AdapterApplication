package com.nathaniel.statiistic;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;
import static com.nathaniel.statiistic.StatisticsActivity.TAG;

/**
 * Created by small on 2016/11/10.
 */

public class LogManager {

    public SharedPreferences sharedPreferences;


    //写数据
    void writeLogFileAppend(Context context, String str) throws IOException {
        SharedPreferences pref_default = PreferenceManager.getDefaultSharedPreferences(context);
        if (pref_default.getBoolean("log", false)) {
            Time time = new Time();
            time.setToNow();
            String logstr = "\n---" + time.monthDay + "-" + time.hour + ":" + time.minute + ":" + time.second + "\n";
            try {
                FileOutputStream fout = context.openFileOutput("log", MODE_APPEND);
                byte[] bytes = (logstr + str).getBytes();
                fout.write(bytes);
                fout.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("qiang", "log写入成功");
        }
    }

    void writeLogFilePrivate(Context context) throws IOException {

        sharedPreferences = context.getSharedPreferences("data", MODE_PRIVATE);

        long remain_liuliang = sharedPreferences.getLong("remain_liuliang", 0);
        long all_liuliang = sharedPreferences.getLong("all_liuliang", 0);
        long curdayflow = sharedPreferences.getLong("curdayflow", 0);
        long lastmonthflow = sharedPreferences.getLong("lastmonthflow", 0);
        long curfreetimeflow = sharedPreferences.getLong("curfreetimeflow", 0);
        long curfreefront = sharedPreferences.getLong("curfreefront", 0);
        long curfreebehind = sharedPreferences.getLong("curfreebehind", 0);

        Time time = new Time();
        time.setToNow();
        String logstr = time.monthDay + ":" + time.hour + ":" + time.minute + ":" + time.second;
        logstr += "\nremain_liuliang:" + remain_liuliang;
        logstr += "\nall_liuliang:" + all_liuliang;
        logstr += "\ncurdayflow:" + curdayflow;
        logstr += "\nlastmonthflow:" + lastmonthflow;
        logstr += "\ncurfreetimeflow:" + curfreetimeflow;
        logstr += "\ncurfreefront:" + curfreefront;
        logstr += "\ncurfreebehind:" + curfreebehind;

        FileOutputStream fout = context.openFileOutput("log_refresh", MODE_PRIVATE);
        byte[] bytes = logstr.getBytes();
        fout.write(bytes);
        fout.close();
        Log.d(TAG, "log refresh");
    }

}
