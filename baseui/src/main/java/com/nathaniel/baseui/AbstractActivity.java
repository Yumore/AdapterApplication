package com.nathaniel.baseui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.hjq.base.binding
 * @datetime 2021/3/31 - 19:50
 */
public abstract class AbstractActivity<VB extends ViewBinding> extends AppCompatActivity implements IViewBinding {
    protected VB binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewBinding();
        setContentView(binding.getRoot());
        loadData();
        bindView();
    }

    /**
     * 初始化ViewBinding对象
     *
     * @return VB
     */
    protected abstract VB getViewBinding();
}
