package com.nathaniel.statiistic;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Build;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.nathaniel.statistics.R;

import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

import static android.content.Context.AUDIO_SERVICE;
import static android.content.Context.MODE_PRIVATE;
import static android.media.AudioManager.RINGER_MODE_SILENT;
import static android.media.AudioManager.STREAM_RING;
import static com.nathaniel.statiistic.StatisticsActivity.TAG;

/**
 * @author small
 * @date 2016/9/30
 */

public class AlarmManualReceiver extends BroadcastReceiver implements SMSBroadcastReceiver.Interaction {
    public int mode;
    public AudioManager audio;
    public int volumn = 0;
    public NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        String logger = "AlarmReceiverManual";
        SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences sharedPreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (defaultPreferences.getBoolean("AutomaticCheck", false)) {
            //发送短信
            SendMessage sendmessage = new SendMessage();
            sendmessage.sendMessages(context);
            //静音
            sendmessage.silent(context);
            Log.d("qiang", "每日短信发送成功");

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
            SMSBroadcastReceiver smsBroadcastReceiver = new SMSBroadcastReceiver();
            context.getApplicationContext().registerReceiver(smsBroadcastReceiver, intentFilter);
            smsBroadcastReceiver.setInteractionListener(this);
        }
        Calendar calendar = Calendar.getInstance();

        int curday = calendar.get(Calendar.DAY_OF_MONTH);
        int curmonth = calendar.get(Calendar.MONTH);
        //重置
        if (Objects.equals(Integer.valueOf(defaultPreferences.getString("remonth", "")), curday)) {
            editor.putLong("lastmonthflow", sharedPreferences.getLong("curmonthflow", 0));//上个月使用
            logger += "\n" + "lastmonthflow" + ":" + sharedPreferences.getLong("curmonthflow", 0);
            editor.putLong("curmonthflow", 0);//
            logger += "\n" + "lastmonthflow" + ":" + sharedPreferences.getLong("curmonthflow", 0);
        }

        CalculateTodayFlow calculateTodayFlow = new CalculateTodayFlow();
        long todayflow = calculateTodayFlow.calculate(context);
        long curmonthflow = sharedPreferences.getLong("curmonthflow", 0);
        curmonthflow = curmonthflow + todayflow;//包含闲时的
        editor.putLong("curmonthflow", curmonthflow);
        logger += "\n" + "curmonthflow" + ":" + curmonthflow;
        editor.putLong("curfreebehind", 0);
        logger += "\n" + "curfreebehind" + ":" + 0;
        //启动longRunningService
        Intent i = new Intent(context, AlarmManualService.class);
        context.startService(i);

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();

        //long result;//1 当日使用流量
        long thisbootflow = sharedPreferences.getLong("thisbootflow", 0);//4
        //long curdayflow=sharedPreferences.getLong("curdayflow",0);
        long onedaylastbootflow = sharedPreferences.getLong("onedaylastbootflow", 0);//5
        long onebootlastdayflow = sharedPreferences.getLong("onebootlastdayflow", 0);//6

        if (activeInfo.isConnected()) {
            if (Objects.equals(activeInfo.getTypeName(), "MOBILE")) {
                long cur_boot_mobiletx = TrafficStats.getMobileTxBytes();
                long cur_boot_mobilerx = TrafficStats.getMobileRxBytes();
                thisbootflow = cur_boot_mobilerx + cur_boot_mobiletx;//4
            }
        }
        onebootlastdayflow = thisbootflow + onedaylastbootflow;
        editor.putLong("onebootlastdayflow", onebootlastdayflow);
        logger += "\n" + "onebootlastdayflow" + ":" + onebootlastdayflow;
        try {
            new LogManager().writeLogFileAppend(context, logger);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean flag = editor.commit();
        Log.d(TAG, "每日更新广播处理完毕manual" + flag);
    }

    @Override
    public void setTexts(Context context, String[] content) {
        //delay();
        try {
            Log.d("qiang", "delay");
            Thread.currentThread();
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        recovery(context);
        if (content[1] != null && content[0] != null) {
            SharedPreferences.Editor editor = context.getSharedPreferences("data", MODE_PRIVATE).edit();
            editor.putLong("curmonthflow", 0);
            editor.putLong("remain_liuliang", new Formatdata().getNumFromString(content[0]));
            editor.putLong("all_liuliang", new Formatdata().getNumFromString(content[1]));
            editor.putLong("curfreebehind", 0);
            editor.putLong("curfreefront", 0);
            editor.putBoolean("sent", true);
            editor.commit();


            CalculateTodayFlow calculateTodayFlow = new CalculateTodayFlow();
            long todayflow = calculateTodayFlow.calculate(context);
            new NotificationManagers().showNotificationPrecise(context, todayflow);
        } else {
            Toast.makeText(context, "查询失败-.-", Toast.LENGTH_LONG).show();
        }
    }

    public void recovery(Context context) {
        //Log.d("qiang", "volumn:" + volumn);
        if (Build.VERSION.SDK_INT >= 24) {
            audio = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        } else {
            audio = (AudioManager) context.getSystemService(AUDIO_SERVICE);
            audio.setRingerMode(mode);
            audio.setStreamVolume(mode, volumn, 0);
        }
    }

    public class SendMessage {
        void sendMessages(Context context) {
            SmsManager manager = SmsManager.getDefault();
            manager.sendTextMessage(context.getString(R.string.phone), null, context.getString(R.string.message_search), null, null);  //发送短信
        }

        void silent(Context context) {
            //静音--------
            if (Build.VERSION.SDK_INT >= 24) {
                audio = (AudioManager) context.getSystemService(AUDIO_SERVICE);
                audio.adjustVolume(AudioManager.ADJUST_LOWER, 0);
            } else {
                audio = (AudioManager) context.getSystemService(AUDIO_SERVICE);
                mode = audio.getRingerMode();
                volumn = audio.getStreamVolume(STREAM_RING);
                audio.setStreamVolume(STREAM_RING, 0, 0);
                audio.setRingerMode(RINGER_MODE_SILENT);
                //audio.setRingerMode(RINGER_MODE_SILENT);
                Log.d("qiang", "old_mode:" + volumn);
                Log.d("qiang", "old_volumn:" + volumn);
            }
        }
    }

}
