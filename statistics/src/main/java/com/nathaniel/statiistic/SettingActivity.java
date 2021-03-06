package com.nathaniel.statiistic;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;
import com.nathaniel.statistics.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;


/**
 * Created by small on 2016/11/16.
 */

public class SettingActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    LinearLayout root;
    private EditTextPreference CheckEditTextPreference;
    private SwitchPreference ShowNotificationSwitchPreference;
    private SwitchPreference LogSwitchPreference;
    private SwitchPreference AutomaticCheckSwitchPreference;
    private ListPreference RemonthListPreference;
    private SwitchPreference freeSwitchPreference;
    private EditTextPreference freeEditTextPreference;
    private EditTextPreference alertsflowEditTextPreference;
    //private EditTextPreference blankEditTextPreference;
    private SwitchPreference alertSwitchPreference;

    private void initPreferences() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        CheckEditTextPreference = (EditTextPreference) findPreference("check");
        ShowNotificationSwitchPreference = (SwitchPreference) findPreference("ShowNotification");
        AutomaticCheckSwitchPreference = (SwitchPreference) findPreference("AutomaticCheck");
        LogSwitchPreference = (SwitchPreference) findPreference("log");
        RemonthListPreference = (ListPreference) findPreference("remonth");

        freeSwitchPreference = (SwitchPreference) findPreference("free");
        freeEditTextPreference = (EditTextPreference) findPreference("freeflow");
        freeEditTextPreference.setSummary(defaultSharedPreferences.getString("freeflow", "0"));
        if (!defaultSharedPreferences.getBoolean("free", false)) {
            ((PreferenceCategory) findPreference("checkcategory")).removePreference(findPreference("freeflow"));
        }
        alertSwitchPreference = (SwitchPreference) findPreference("alert");
        alertsflowEditTextPreference = (EditTextPreference) findPreference("alertsflow");
        alertsflowEditTextPreference.setSummary(defaultSharedPreferences.getString("alertsflow", "0"));
        if (!defaultSharedPreferences.getBoolean("alerts", false)) {
            ((PreferenceCategory) findPreference("alertscategory")).removePreference(findPreference("alertsflow"));
        }
        // blankEditTextPreference=(EditTextPreference)findPreference("blank");
        //CheckEditTextPreference.setSummary(defaultSharedPreferences.getString("check", "0"));
        RemonthListPreference.setSummary(defaultSharedPreferences.getString("remonth", "0"));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        root = (LinearLayout) findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.activity_toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         Preference preference) {
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SharedPreferences pref_default = PreferenceManager.getDefaultSharedPreferences(this);
        switch (key) {
            case "log":
                Snackbar.make(root, "更改已保存", Snackbar.LENGTH_SHORT)
                    .show();
                Log.d("qiang", "log change");
                try {
                    writeFile(this, "log", "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "AutomaticCheck":
                Snackbar.make(root, "更改已保存", Snackbar.LENGTH_SHORT)
                    .show();
                if (pref_default.getBoolean("ShowNotification", true)) {
                    Intent intent2 = new Intent(this, AlarmManualService.class);
                    startService(intent2);
                }
                break;
            case "alerts":
                Snackbar.make(root, "更改已保存", Snackbar.LENGTH_SHORT)
                    .show();
                if (pref_default.getBoolean("alerts", false)) {

                    ((PreferenceCategory) findPreference("alertscategory")).addPreference(alertsflowEditTextPreference);
                    alertsflowEditTextPreference.setSelectable(true);
                    alertsflowEditTextPreference.setSummary(pref_default.getString("alertsflow", "0"));
                } else {
                    ((PreferenceCategory) findPreference("alertscategory")).removePreference(findPreference("alertsflow"));
                }
                break;
            case "alertsflow":
                Snackbar.make(root, "更改已保存", Snackbar.LENGTH_SHORT)
                    .show();
                alertsflowEditTextPreference.setSummary(pref_default.getString("alertsflow", "0"));
                break;
            case "ShowNotification":
                Snackbar.make(root, "更改已保存", Snackbar.LENGTH_SHORT)
                    .show();
                if (!pref_default.getBoolean("ShowNotification", true)) {
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Notification.Builder builder = new Notification.Builder(this);
                    builder.setSmallIcon(R.mipmap.ic_album_black_24dp)
                        .setContentTitle("流量计")
                        .setAutoCancel(true)
                        .setOngoing(true)
                        .setContentText("");
                    if (Build.VERSION.SDK_INT < 16) {
                        notificationManager.notify(0, builder.getNotification());
                    } else {
                        notificationManager.notify(0, builder.build());
                    }
                    notificationManager.cancel(0);
                } else {
                    SharedPreferences pref = this.getSharedPreferences("data", MODE_PRIVATE);
                    long remain_liuliang = pref.getLong("remain_liuliang", 0);
                    long all_liuliang = pref.getLong("all_liuliang", 0);
                    long curmonthflow = pref.getLong("curmonthflow", 0);
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    String notification_string;
                    long curdayflow = pref.getLong("curdayflow", 0);//4

                    if (Objects.equals(remain_liuliang, "") | Objects.equals(all_liuliang, "")) {
                        notification_string = "无流量数据，请启动应用查询";
                    } else {
                        notification_string = "本月流量还剩 " + new Formatdata().long2string(remain_liuliang - curmonthflow - curdayflow) + " 今日已用" + new Formatdata().long2string(curdayflow);
                    }
                    Notification.Builder builder = new Notification.Builder(this);
                    builder.setSmallIcon(R.mipmap.ic_album_black_24dp)
                        .setContentTitle("流量计")
                        .setAutoCancel(true)
                        .setOngoing(true)
                        .setContentText(notification_string);
                    if (Build.VERSION.SDK_INT < 16) {
                        notificationManager.notify(0, builder.getNotification());
                    } else {
                        notificationManager.notify(0, builder.build());
                    }
                }
                break;
            case "free":
                Snackbar.make(root, "更改已保存", Snackbar.LENGTH_SHORT)
                    .show();
                if (pref_default.getBoolean("free", false)) {
                    startService(new Intent(this, AlarmFreeService.class));
                    ((PreferenceCategory) findPreference("checkcategory")).addPreference(freeEditTextPreference);
                    freeEditTextPreference.setSelectable(true);
                    freeEditTextPreference.setSummary(pref_default.getString("freeflow", "0"));
                } else {
                    ((PreferenceCategory) findPreference("checkcategory")).removePreference(findPreference("freeflow"));
                }
                break;
            case "freeflow":
                Snackbar.make(root, "更改已保存", Snackbar.LENGTH_SHORT)
                    .show();
                freeEditTextPreference.setSummary(pref_default.getString("freeflow", "0"));
                break;
            case "remonth":
                RemonthListPreference.setSummary(pref_default.getString("remonth", "0"));
                break;
            default:
                break;
        }
    }

    //写数据
    void writeFile(Context context, String fileName, String writestr) throws IOException {
        try {/*
            BufferedWriter fout = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName, true)));
            /*** 追加文件：使用FileOutputStream，在构造FileOutputStream时，把第二个参数设为true

            fout.write(writestr);
            */

            FileOutputStream fout = context.openFileOutput(fileName, MODE_PRIVATE);
            byte[] bytes = writestr.getBytes();
            fout.write(bytes);

            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
