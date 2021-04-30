package com.nathaniel.sample;

import android.annotation.SuppressLint;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nathaniel.adapter.utility.ItemDecoration;
import com.nathaniel.baseui.AbstractActivity;
import com.nathaniel.sample.databinding.ActivityPackageBinding;
import com.nathaniel.sample.test.DataUtils;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author nathaniel
 * @version V1.0.0
 * @contact <a href="mailto:nathanwriting@126.com">contact me</a>
 * @package com.nathaniel.sample
 * @datetime 4/29/21 - 7:33 PM
 */
public class PackageActivity extends AbstractActivity<ActivityPackageBinding> {
    private static final int HANDLER_WHAT_REFRESH = 0x0101;
    private static final long DELAY_MILLIS = 1000L;
    private static final String TAG = PackageActivity.class.getSimpleName();
    /**
     * 刷新页面信息的handler
     */
    private Handler handler;
    private PackageAdapter packageAdapter;
    /**
     * 安装包信息
     */
    private List<PackageEntity> packageEntityList;

    @Override
    protected ActivityPackageBinding getViewBinding() {
        return ActivityPackageBinding.inflate(getLayoutInflater());
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void loadData() {
        packageEntityList = AppUtils.getPackageEntities(PackageActivity.this);
        packageAdapter = new PackageAdapter(packageEntityList);
        handler = new Handler(getMainLooper()) {
            @SuppressLint("DefaultLocale")
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == HANDLER_WHAT_REFRESH) {
                    String regex = "yyyy-MM-dd HH:mm:ss";
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(regex);
                    long[] totalData = AppUtils.getNetworkUsage();
                    // LoggerUtils.logger(TAG, String.format("send data: %d kb, receive data: %d kb", totalData[0], totalData[1]));
                    binding.packageTotalTv.setText(String.format("send total: %s, receive total: %s , send total from file: %s, receive total from file: %s, time :%s",
                            DataUtils.getRealDataSize(TrafficStats.getTotalTxBytes()),
                            DataUtils.getRealDataSize(TrafficStats.getTotalRxBytes()),
                            DataUtils.getRealDataSize(totalData[0]),
                            DataUtils.getRealDataSize(totalData[1]),
                            simpleDateFormat.format(System.currentTimeMillis())));
                    new Handler(getMainLooper()).postDelayed(() -> {
                        handler.sendEmptyMessage(HANDLER_WHAT_REFRESH);
                    }, DELAY_MILLIS);
                } else {
                    super.handleMessage(msg);
                }
            }
        };

    }

    @SuppressLint("DefaultLocale")
    @Override
    public void bindView() {
        handler.sendEmptyMessage(HANDLER_WHAT_REFRESH);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        int padding = (int) getResources().getDimension(R.dimen.common_padding_small);
        binding.recyclerView.addItemDecoration(new ItemDecoration(padding, ItemDecoration.LINEAR_LAYOUT_MANAGER));
        binding.recyclerView.setAdapter(packageAdapter);
    }
}
