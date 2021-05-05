package com.nathaniel.sample;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.widget.Toast;

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
                    binding.packageTotalTv.setText(String.format("send total: %s, receive total: %s, time :%s",
                        DataUtils.getRealDataSize(TrafficStats.getTotalTxBytes()),
                        DataUtils.getRealDataSize(TrafficStats.getTotalRxBytes()),
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
        packageAdapter.setOnItemClickListener((adapter, view, position) -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Toast.makeText(getApplicationContext(), "版本过低没法打开", Toast.LENGTH_SHORT).show();
                return;
            }
            final AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
            if (mode == AppOpsManager.MODE_ALLOWED) {
                Toast.makeText(getApplicationContext(), "版本过低没法打开", Toast.LENGTH_SHORT).show();
                return;
            }
            // 打开“有权查看使用情况的应用”页面
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        });
    }
}
