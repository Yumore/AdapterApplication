package com.yecy.settingapi;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yecy.utils.datausage.DataUsageUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class DataUsageActivity extends Activity {

    private static final String TAG = DataUsageActivity.class.getSimpleName();
    private final Context mContext = this;
    private final Handler mHandler = new Handler();
    private TextView mShow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_usage_activity);

        mShow = findViewById(R.id.sample_text);
    }


    public void forNetworkStatsManager(View v) {
        long start = SystemClock.elapsedRealtime();
        long end = System.currentTimeMillis();
        Map<Integer, DataUsageUtil.DataUsageWifi> mapMobile = DataUsageUtil.getInstance(this).forNetworkStatsManager(
            ConnectivityManager.TYPE_MOBILE, start, end);
        Map<Integer, DataUsageUtil.DataUsageWifi> mapWifi = DataUsageUtil.getInstance(this).forNetworkStatsManager(
            ConnectivityManager.TYPE_WIFI, start, end);
        if (mapMobile == null && mapWifi == null) {
            mShow.setText("应用使用流量获取失败");
            return;
        }
        mShow.setText("使用流量：mobile=" + mapMobile.size() + ", wifi=" + mapWifi.size() + "\n");
        Map<Integer, String[]> list = new HashMap<>();
        Set<Integer> keys = mapMobile.keySet();
        for (Integer key : keys) {
            DataUsageUtil.DataUsageWifi data
                = mapMobile.get(key);
            if (list.get(key) == null) {
                String[] tmp = new String[5];
                tmp[0] = String.valueOf(key);
                tmp[1] = String.valueOf(data.rxByte);
                tmp[2] = String.valueOf(data.txByte);
                list.put(key, tmp);
            } else {
                String[] tmp = list.get(key);
                tmp[1] = String.valueOf(data.rxByte);
                tmp[2] = String.valueOf(data.txByte);
                list.put(key, tmp);
            }

        }
        keys = mapWifi.keySet();
        for (Integer key : keys) {
            DataUsageUtil.DataUsageWifi data
                = mapWifi.get(key);
            if (list.get(key) == null) {
                String[] tmp = new String[5];
                tmp[0] = String.valueOf(key);
                tmp[3] = String.valueOf(data.rxByte);
                tmp[4] = String.valueOf(data.txByte);
                list.put(key, tmp);
            } else {
                String[] tmp = list.get(key);
                tmp[3] = String.valueOf(data.rxByte);
                tmp[4] = String.valueOf(data.txByte);
                list.put(key, tmp);
            }
        }
        keys = list.keySet();
        for (Integer key : keys) {
            String[] lst = list.get(key);
            mShow.append("packageName: " + lst[0] + "\n");
            mShow.append("Mobile Rx: " + lst[1] + "\n");
            mShow.append("Mobile Tx: " + lst[2] + "\n");
            mShow.append("Wifi Rx: " + lst[3] + "\n");
            mShow.append("Wifi Tx: " + lst[4] + "\n");
        }
    }


    public void forLoaderManager(View v) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long start = SystemClock.elapsedRealtime();
                long end = System.currentTimeMillis();
                final Map<Integer, DataUsageUtil.DataUsageWifi> mapMobile = DataUsageUtil.getInstance(mContext).forLoaderManager(
                    NetworkTemplate.buildTemplateMobileWildcard(), start, end);
                final Map<Integer, DataUsageUtil.DataUsageWifi> mapWifi = DataUsageUtil.getInstance(mContext).forLoaderManager(
                    NetworkTemplate.buildTemplateWifiWildcard(), start, end);
                if (mapMobile == null && mapWifi == null) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mShow.setText("应用使用流量获取失败");
                        }
                    });
                    return;
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mShow.setText("使用流量：mobile=" + mapMobile.size() + ", wifi=" + mapWifi.size() + "\n");
                    }
                });
                final Map<Integer, String[]> list = new HashMap<>();
                Set<Integer> keys = mapMobile.keySet();
                for (Integer key : keys) {
                    DataUsageUtil.DataUsageWifi data
                        = mapMobile.get(key);
                    if (list.get(key) == null) {
                        String[] tmp = new String[5];
                        tmp[0] = String.valueOf(key);
                        tmp[1] = String.valueOf(data.rxByte);
                        tmp[2] = String.valueOf(data.txByte);
                        list.put(key, tmp);
                    } else {
                        String[] tmp = list.get(key);
                        tmp[1] = String.valueOf(data.rxByte);
                        tmp[2] = String.valueOf(data.txByte);
                        list.put(key, tmp);
                    }
                }
                keys = mapWifi.keySet();
                for (Integer key : keys) {
                    DataUsageUtil.DataUsageWifi data
                        = mapWifi.get(key);
                    if (list.get(key) == null) {
                        String[] tmp = new String[5];
                        tmp[0] = String.valueOf(key);
                        tmp[3] = String.valueOf(data.rxByte);
                        tmp[4] = String.valueOf(data.txByte);
                        list.put(key, tmp);
                    } else {
                        String[] tmp = list.get(key);
                        tmp[3] = String.valueOf(data.rxByte);
                        tmp[4] = String.valueOf(data.txByte);
                        list.put(key, tmp);
                    }
                }
                keys = list.keySet();
                for (Integer key : keys) {
                    final String[] lst = list.get(key);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mShow.append("packageName: " + lst[0] + "\n");
                            mShow.append("Mobile Rx: " + lst[1] + "\n");
                            mShow.append("Mobile Tx: " + lst[2] + "\n");
                            mShow.append("Wifi Rx: " + lst[3] + "\n");
                            mShow.append("Wifi Tx: " + lst[4] + "\n");
                        }
                    });
                }
            }
        }).start();
    }
}
