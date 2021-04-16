package com.nathaniel.sample.binding;

import android.view.ViewGroup;

import com.nathaniel.baseui.AbstractFragment;
import com.nathaniel.sample.databinding.FragmentBindingDemo1Binding;

/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.hjq.demo.ui.binding
 * @datetime 2021/3/31 - 20:20
 */
public class Demo1BindingFragment extends AbstractFragment<FragmentBindingDemo1Binding> {

    @Override
    protected FragmentBindingDemo1Binding getViewBinding(ViewGroup container) {
        return FragmentBindingDemo1Binding.inflate(getLayoutInflater(), container, false);
    }

    @Override
    public void loadData() {

    }

    @Override
    public void bindView() {
        binding.demoTextTv.setText("fragment-设置的值");
        binding.itemStatusIncludeLayout.itemTextTv.setText("include 设置的值");
    }
}
