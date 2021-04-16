package com.nathaniel.sample.binding;

import android.annotation.SuppressLint;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.nathaniel.adapter.utility.ItemDecoration;
import com.nathaniel.baseui.AbstractFragment;
import com.nathaniel.sample.R;
import com.nathaniel.sample.databinding.FragmentBindingDemo2Binding;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.hjq.demo.ui.binding
 * @datetime 2021/3/31 - 20:20
 */
public class Demo2BindingFragment extends AbstractFragment<FragmentBindingDemo2Binding> {
    private static final String TAG = Demo2BindingFragment.class.getSimpleName();
    private final List<String> stringList = new ArrayList<>();
    private SimpleAdapter simpleAdapter;

    @Override
    protected FragmentBindingDemo2Binding getViewBinding(ViewGroup container) {
        return FragmentBindingDemo2Binding.inflate(getLayoutInflater(), container, false);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void loadData() {
        for (int i = 0; i < 20; i++) {
            stringList.add(String.format("item %d", i));
        }
        simpleAdapter = new SimpleAdapter(R.layout.item_recycler_list, stringList);
    }

    @Override
    public void bindView() {
        binding.demoTextTv.setText("这是代码设置的值fragment-demo2");
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        int spaces = (int) getContext().getResources().getDimension(R.dimen.common_padding_micro);
        binding.recyclerView.addItemDecoration(new ItemDecoration(spaces, ItemDecoration.LINEAR_LAYOUT_MANAGER));
        binding.recyclerView.setAdapter(simpleAdapter);
        simpleAdapter.setOnItemClickListener((adapter, view, position) -> {
            Toast.makeText(getContext(), TAG + stringList.get(position) + " has been clicked", Toast.LENGTH_SHORT).show();
        });
    }
}
