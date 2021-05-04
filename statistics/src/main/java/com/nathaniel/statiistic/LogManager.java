package com.nathaniel.statiistic;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.Time;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import static android.content.Context.MODE_APPEND;
import static android.content.Context.MODE_PRIVATE;
import static com.nathaniel.statiistic.StatisticsActivity.TAG;

/**
 * @author small
 * @date 2016/11/10
 */

public class LogManager {

    public SharedPreferences sharedPreferences;


    //写数据
    void writeLogFileAppend(Context context, String str) throws IOException {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (defaultSharedPreferences.getBoolean("log", false)) {
            Calendar calendar = Calendar.getInstance();
            String logger = "\n---" + calendar.get(Calendar.MONDAY) + "-" + calendar.get(Calendar.HOUR) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND) + "\n";
            try {
                FileOutputStream fileOutputStream = context.openFileOutput("log", MODE_APPEND);
                byte[] bytes = (logger + str).getBytes();
                fileOutputStream.write(bytes);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("qiang", "log写入成功");
        }
    }

    void writeLogFilePrivate(Context context) throws IOException {

        sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);

        long remain_liuliang = sharedPreferences.getLong("remain_liuliang", 0);
        long all_liuliang = sharedPreferences.getLong("all_liuliang", 0);
        long curdayflow = sharedPreferences.getLong("curdayflow", 0);
        long lastmonthflow = sharedPreferences.getLong("lastmonthflow", 0);
        long curfreetimeflow = sharedPreferences.getLong("curfreetimeflow", 0);
        long curfreefront = sharedPreferences.getLong("curfreefront", 0);
        long curfreebehind = sharedPreferences.getLong("curfreebehind", 0);

        Time time = new Time();
        time.setToNow();
        String logger = time.monthDay + ":" + time.hour + ":" + time.minute + ":" + time.second;
        logger += "\nremain_liuliang:" + remain_liuliang;
        logger += "\nall_liuliang:" + all_liuliang;
        logger += "\ncurdayflow:" + curdayflow;
        logger += "\nlastmonthflow:" + lastmonthflow;
        logger += "\ncurfreetimeflow:" + curfreetimeflow;
        logger += "\ncurfreefront:" + curfreefront;
        logger += "\ncurfreebehind:" + curfreebehind;

        FileOutputStream fileOutputStream = context.openFileOutput("log_refresh", MODE_PRIVATE);
        byte[] bytes = logger.getBytes();
        fileOutputStream.write(bytes);
        fileOutputStream.close();
        Log.d(TAG, "log refresh");
    }

}
