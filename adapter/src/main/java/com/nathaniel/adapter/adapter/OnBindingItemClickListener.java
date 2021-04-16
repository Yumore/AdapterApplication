package com.nathaniel.adapter.adapter;

import android.view.View;

import androidx.viewbinding.ViewBinding;

/**
 * @author Nathaniel
 * @version V1.0.0
 * @datetime 2020/4/19 - 16:58
 */
public interface OnBindingItemClickListener {
    /**
     * item click listener for binding adapter
     *
     * @param adapter  BaseBindingAdapter
     * @param view     View
     * @param position position
     */
    void onItemClick(BaseBindingAdapter<?, ? extends ViewBinding> adapter, View view, int position);
}
