package com.example.adapter.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.vipon.adapter
 * @datetime 2020/4/5 - 12:02
 */
public abstract class BaseNetworkView extends RelativeLayout {
    private View contentLayout;

    public BaseNetworkView(Context context) {
        super(context);
        int layoutId = getNetworkLayoutId();
        if (layoutId == 0) {
            throw new IllegalArgumentException(BaseEmptyView.class.getSimpleName() + " : Must set content layout!");
        }
        contentLayout = inflate(context, layoutId, this);
        initialize();
    }

    public BaseNetworkView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseNetworkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public BaseNetworkView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected abstract void initialize();

    protected abstract int getNetworkLayoutId();

    protected abstract void setNetworkOptionText(CharSequence optionText, OnNetworkListener onNetworkListener);

    protected abstract void setNetworKImageResource(int drawableRes);
}
