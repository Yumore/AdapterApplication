package com.nathaniel.sample;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.nathaniel.adapter.adapter.BaseBindingAdapter;
import com.nathaniel.adapter.adapter.BaseViewBindingHolder;
import com.nathaniel.adapter.utility.EmptyUtils;
import com.nathaniel.adapter.utility.LoggerUtils;
import com.nathaniel.sample.databinding.ItemPackageRecyclerListBinding;
import com.nathaniel.sample.test.DataUtils;

import java.util.List;

/**
 * @author nathaniel
 * @version V1.0.0
 * @contact <a href="mailto:nathanwriting@126.com">contact me</a>
 * @package com.nathaniel.sample
 * @datetime 4/29/21 - 8:07 PM
 */
public class PackageAdapter extends BaseBindingAdapter<PackageEntity, ItemPackageRecyclerListBinding> {
    private static final String TAG = PackageAdapter.class.getSimpleName();

    /**
     * init data and layout
     *
     * @param dataList dataList
     */
    public PackageAdapter(List<PackageEntity> dataList) {
        super(dataList);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void bindDataToView(BaseViewBindingHolder<ItemPackageRecyclerListBinding> viewHolder, ItemPackageRecyclerListBinding binding, PackageEntity data) {
        if (EmptyUtils.isEmpty(data)) {
            return;
        }
        LoggerUtils.logger(TAG, LoggerUtils.Level.WARING, data.toString());
        binding.itemPackageImage.setImageDrawable(data.getAppIcon());
        binding.itemPackageName.setText(data.getPackageName());
        binding.itemPackageInfo.setText(String.format("versionName: %s, versionCode: %d", data.getVersionName(), data.getVersionCode()));
        binding.itemPackageData.setText(String.format("send data: %s, receive data: %s", DataUtils.getRealDataSize(data.getTx()), DataUtils.getRealDataSize(data.getRx())));
    }

    @Override
    public ItemPackageRecyclerListBinding getViewBinding(ViewGroup viewGroup) {
        return ItemPackageRecyclerListBinding.inflate(LayoutInflater.from(getContext()), viewGroup, false);
    }
}
