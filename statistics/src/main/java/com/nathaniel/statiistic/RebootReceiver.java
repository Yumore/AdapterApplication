package com.nathaniel.statiistic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * @author small
 * @date 2016/9/30
 */

public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            intent = new Intent(context, AlarmManualService.class);
            //开启关闭Service1
            context.startService(intent);
            SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            //4
            long curdayflow = sharedPreferences.getLong("curdayflow", 0);
            editor.putBoolean("isreboot", true);
            editor.commit();
            new NotificationManagers().showNotificationPrecise(context, curdayflow);
        }
    }


}
