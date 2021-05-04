package com.nathaniel.sample;

import android.annotation.SuppressLint;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.nathaniel.baseui.AbstractActivity;
import com.nathaniel.datause.DataUsageManager;
import com.nathaniel.datause.Interval;
import com.nathaniel.datause.NetworkType;
import com.nathaniel.datause.Usage;
import com.nathaniel.sample.databinding.ActivitySummaryBinding;

/**
 * @author Nathaniel
 * @version V1.0.0
 * @contact <a href="mailto:nathanwriting@126.com">contact me</a>
 * @package com.nathaniel.sample
 * @datetime 2021/5/3 - 22:44
 */
public class SummaryActivity extends AbstractActivity<ActivitySummaryBinding> {
    @Override
    protected ActivitySummaryBinding getViewBinding() {
        return ActivitySummaryBinding.inflate(getLayoutInflater());
    }

    @Override
    public void loadData() {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void bindView() {
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) getSystemService(Context.NETWORK_STATS_SERVICE);
        @SuppressLint("ServiceCast")
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint({"HardwareIds", "MissingPermission"})
        DataUsageManager dataUsageManager = new DataUsageManager(networkStatsManager, telephonyManager.getSubscriberId());
        Usage usage = dataUsageManager.getUsage(Interval.INSTANCE.getToday(), NetworkType.MOBILE);
        binding.summaryTextTv.setText(new Gson().toJson(usage));
        dataUsageManager.getRealtimeUsage(NetworkType.WIFI).subscribe();
    }
}
