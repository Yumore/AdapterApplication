package com.nathaniel.adapter.adapter;

import android.view.View;

/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.example.adapterapplication.adapter
 * @datetime 2020/4/19 - 16:58
 */
public interface OnBindingItemChildClickListener {
    boolean onItemChildClick(BaseBindingAdapter<?, ?> adapter, View view, int position);
}