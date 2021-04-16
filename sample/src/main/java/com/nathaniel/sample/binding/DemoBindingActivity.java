package com.nathaniel.sample.binding;

import com.nathaniel.baseui.AbstractActivity;
import com.nathaniel.sample.databinding.ActivityBindingDemoBinding;

/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.hjq.demo.ui.binding
 * @datetime 2021/3/31 - 20:03
 */
public class DemoBindingActivity extends AbstractActivity<ActivityBindingDemoBinding> {
    @Override
    protected ActivityBindingDemoBinding getViewBinding() {
        return ActivityBindingDemoBinding.inflate(getLayoutInflater());
    }

    @Override
    public void loadData() {

    }

    @Override
    public void bindView() {
        binding.testTextTv.setText("页面设置的值（activity）");
    }
}
