package com.nathaniel.statiistic;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

/**
 * @author small
 * @date 2016/9/30
 */

public class AlarmTimingReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmTimingReceiver.class.getSimpleName();
    public NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "快速更新广播收到");
        String logstr = "AlarmReceiverTiming";
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        String notification_string;

        SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (activeInfo == null) {
            Log.d(TAG, "网络没有连接");
            return;
        }
        if (activeInfo.isConnected()) {
            if (Objects.equals(activeInfo.getTypeName(), "MOBILE")) {
                long curmonthflow = sharedPreferences.getLong("curmonthflow", 0);
                long remain_liuliang = sharedPreferences.getLong("remain_liuliang", 0);
                CalculateTodayFlow calculateTodayFlow = new CalculateTodayFlow();
                long curdayflow = ((sharedPreferences.getBoolean("isreboot", false)) ? calculateTodayFlow.calculate(context) : calculateTodayFlow.calculate(context) - sharedPreferences.getLong("firststartflow", 0));
                long allfreetimeflow = new Formatdata().getNumFromString(defaultPreferences.getString("freeflow", "0") + "M");
                long curmonthremainflow = defaultPreferences.getBoolean("free", false) ? (remain_liuliang - curmonthflow - curdayflow - allfreetimeflow) : (remain_liuliang - curmonthflow - curdayflow);
                //alert_notification
                if (defaultPreferences.getBoolean("alerts", true)) {
                    if (curmonthremainflow < (new Formatdata().getNumFromString(defaultPreferences.getString("alertsflow", "0") + "M"))) {
                        new NotificationManagers().showNotificationRough(context);
                    }
                }

                long cur_boot_mobiletx = TrafficStats.getMobileTxBytes();
                long cur_boot_mobilerx = TrafficStats.getMobileRxBytes();
                long thisbootflow = cur_boot_mobilerx + cur_boot_mobiletx;//4
                //long curdayflow = sharedPreferences.getLong("curdayflow", 0);
                //long onedaylastbootflow = sharedPreferences.getLong("onedaylastbootflow", 0);//5
                //long onebootlastdayflow = sharedPreferences.getLong("onebootlastdayflow", 0);//6

                long freetimeflowstart = sharedPreferences.getLong("freetimeflowstart", 0);

                Calendar calendar = Calendar.getInstance();
                int systemTime = calendar.get(Calendar.HOUR_OF_DAY);
                //Toast.makeText(context,systemTime,Toast.LENGTH_SHORT);
                if (systemTime > 22) {//从23点开始截止到次日7点
                    Log.d(TAG, "23free");
                    editor.putLong("curfreebehind", curdayflow - freetimeflowstart);
                    logstr += "\ncurfreebehind:" + (curdayflow - freetimeflowstart);
                } else if (systemTime < 6) {
                    Log.d(TAG, "07free");
                    editor.putLong("curfreefront", curdayflow);
                    logstr += "\ncurfreefront:" + (curdayflow);
                }
                editor.putLong("thisbootflow", thisbootflow);
                logstr += "\nthisbootflow:" + (thisbootflow);
                editor.putLong("curdayflow", curdayflow);
                logstr += "\ncurdayflow:" + (curdayflow);
                editor.putLong(calendar.get(Calendar.DAY_OF_MONTH) + "day", sharedPreferences.getLong("curdayflow", 0));
                logstr += "\n" + calendar.get(Calendar.DAY_OF_MONTH) + "day" + ":" + (sharedPreferences.getLong("curdayflow", 0));
                editor.commit();

                //log
                try {
                    new LogManager().writeLogFileAppend(context, logstr);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //log_refresh
                try {
                    new LogManager().writeLogFilePrivate(context);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new NotificationManagers().showNotificationPrecise(context, curdayflow);
                context.startService(new Intent(context, AlarmTimingService.class));
            }
//            Toast.makeText(context, "mobile:" + mobileInfo.isConnected() + "\n" + "wifi:" + wifiInfo.isConnected()      + "\n" + "active:" + activeInfo.getTypeName(), Toast.LENGTH_SHORT).show();
            //Log.d("qiang", "mobile:" + mobileInfo.isConnected() + ",wifi:" + wifiInfo.isConnected() + ",active:" + activeInfo.getTypeName());
        } else {
            Log.d(TAG, "网络没有连接,所以终止定时广播");
        }
    }


}
