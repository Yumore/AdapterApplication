package com.nathaniel.sample.binding;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.nathaniel.adapter.adapter.BaseBindingAdapter;
import com.nathaniel.adapter.adapter.BaseViewBindingHolder;
import com.nathaniel.adapter.utility.LoggerUtils;
import com.nathaniel.sample.databinding.ItemRecyclerListBinding;

import java.util.List;

/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.hjq.demo.ui.binding
 * @datetime 2021/3/31 - 20:49
 */
public class SimpleBindingAdapter extends BaseBindingAdapter<String, ItemRecyclerListBinding> {

    private static final String TAG = SimpleBindingAdapter.class.getSimpleName();

    /**
     * init data and layout
     *
     * @param dataList dataList
     */
    public SimpleBindingAdapter(List<String> dataList) {
        super(dataList);
    }

    @Override
    public void bindDataToView(BaseViewBindingHolder<ItemRecyclerListBinding> viewHolder, ItemRecyclerListBinding binding, String data) {
        if (TextUtils.isEmpty(data)) {
            return;
        }
        LoggerUtils.logger(TAG, "data=" + data);
        binding.itemTextTv.setText(data);
    }


    @Override
    public ItemRecyclerListBinding getViewBinding(ViewGroup parent) {
        return ItemRecyclerListBinding.inflate(LayoutInflater.from(getContext()), parent, false);
    }
}
