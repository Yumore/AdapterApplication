package com.nathaniel.adapter.adapter;

import android.view.View;

/**
 * @author Nathaniel
 * @version V1.0.0
 * @datetime 2020/4/19 - 16:58
 */
public interface OnItemChildClickListener {
    boolean onItemChildClick(BaseRecyclerAdapter<?> adapter, View view, int position);
}
