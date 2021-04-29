package com.nathaniel.sample;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.nathaniel.adapter.utility.ItemDecoration;
import com.nathaniel.baseui.AbstractActivity;
import com.nathaniel.sample.databinding.ActivityPackageBinding;

import java.util.List;

/**
 * @author nathaniel
 * @version V1.0.0
 * @contact <a href="mailto:nathanwriting@126.com">contact me</a>
 * @package com.nathaniel.sample
 * @datetime 4/29/21 - 7:33 PM
 */
public class PackageActivity extends AbstractActivity<ActivityPackageBinding> {
    private PackageAdapter packageAdapter;
    private List<PackageEntity> packageEntityList;

    @Override
    protected ActivityPackageBinding getViewBinding() {
        return ActivityPackageBinding.inflate(getLayoutInflater());
    }

    @Override
    public void loadData() {
        packageEntityList = AppUtils.getPackageEntityList(PackageActivity.this);
        packageAdapter = new PackageAdapter(packageEntityList);
        ThreadManager.getInstance().executor(() -> {
            PackageManager packageManager = getPackageManager();
            String[] packageNames = null;
            int uid = 1000;
            while (uid <= 19999) {
                packageNames = packageManager.getPackagesForUid(uid);
                if (packageNames != null && packageNames.length > 0) {
                    for (String item : packageNames) {
                        try {
                            final PackageInfo packageInfo = packageManager.getPackageInfo(item, 0);
                            if (packageInfo == null) {
                                break;
                            }
                            CharSequence applicationLabel = packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageInfo.packageName, PackageManager.GET_META_DATA));
                            Log.d("TAG", "应用名称 = " + applicationLabel.toString() + " (" + packageInfo.packageName + ")");
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                uid++;
            }
        });
    }

    @Override
    public void bindView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        int padding = (int) getResources().getDimension(R.dimen.common_padding_small);
        binding.recyclerView.addItemDecoration(new ItemDecoration(padding, ItemDecoration.LINEAR_LAYOUT_MANAGER));
        binding.recyclerView.setAdapter(packageAdapter);
    }
}
