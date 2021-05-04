//package com.yecy.settingapi;
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.TextView;
//
//import androidx.annotation.Nullable;
//
//import java.util.List;
//
//
//public class PowerUsageActivity extends Activity {
//
//    private static final String TAG = PowerUsageActivity.class.getSimpleName();
//    private final Context mContext = this;
//
//    private TextView mShow;
//
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.power_usage_activity);
//
//        mShow = findViewById(R.id.sample_text);
//    }
//
//
//    public void forAndroid5(View v) {
//        List<String[]> list = PowerUsageUtil.getInstance().getAppPowerUsageApi22(this);
//        if (list == null) {
//            mShow.setText("应用耗电量获取失败");
//            return;
//        }
//        mShow.setText("耗电量：" + "\n");
//        for (String[] lst : list) {
//            mShow.append("packageName: " + lst[0] + "\n");
//            mShow.append("百分比: " + lst[1] + "\n");
//            mShow.append("毫安数: " + lst[2] + "\n");
//        }
//    }
//
//
//    public void forAndroid7(View v) {
//        List<String[]> list = PowerUsageUtil.getInstance().getAppPowerUsageApi25(this);
//        if (list == null) {
//            mShow.setText("应用耗电量获取失败");
//            return;
//        }
//        mShow.setText("耗电量：" + "\n");
//        for (String[] lst : list) {
//            mShow.append("packageName: " + lst[0] + "\n");
//            mShow.append("百分比: " + lst[1] + "\n");
//            mShow.append("毫安数: " + lst[2] + "\n");
//        }
//    }
//}
