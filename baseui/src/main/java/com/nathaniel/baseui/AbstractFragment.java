package com.nathaniel.baseui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.hjq.base.binding
 * @datetime 2021/3/31 - 19:57
 */
public abstract class AbstractFragment<VB extends ViewBinding> extends Fragment implements IViewBinding {
    protected VB binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = getViewBinding(container);
        loadData();
        bindView();
        return binding.getRoot();
    }

    /**
     * 初始化ViewBinding
     *
     * @param container container
     * @return VB
     */
    protected abstract VB getViewBinding(ViewGroup container);
}
