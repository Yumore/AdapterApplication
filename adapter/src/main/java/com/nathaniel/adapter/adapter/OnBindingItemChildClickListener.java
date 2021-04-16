package com.nathaniel.adapter.adapter;

import android.view.View;

import androidx.viewbinding.ViewBinding;

/**
 * @author Nathaniel
 * @version V1.0.0
 * @datetime 2020/4/19 - 16:58
 */
public interface OnBindingItemChildClickListener {
    /**
     * item click listener
     *
     * @param adapter  adapter
     * @param view     view
     * @param position position
     * @return true/false
     */
    boolean onItemChildClick(BaseBindingAdapter<?, ? extends ViewBinding> adapter, View view, int position);
}
