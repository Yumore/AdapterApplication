package com.nathaniel.adapter.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.vipon.adapter
 * @datetime 2020/4/5 - 2:13
 */
public abstract class BaseEmptyView extends RelativeLayout {
    protected View contentLayout;

    public BaseEmptyView(Context context) {
        super(context);
        int layoutId = getEmptyLayoutId();
        if (layoutId == 0) {
            throw new IllegalArgumentException(BaseEmptyView.class.getSimpleName() + " : Must set content layout!");
        }
        contentLayout = inflate(context, layoutId, this);
        initialize();
    }

    public BaseEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseEmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaseEmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected abstract void initialize();

    protected abstract int getEmptyLayoutId();

    protected abstract void setEmptyOptionText(CharSequence optionText, OnEmptyListener onEmptyListener);

    protected abstract void setEmptyMessage(CharSequence emptyMessage);

    protected abstract void setEmptyImageResource(int drawableRes);
}
