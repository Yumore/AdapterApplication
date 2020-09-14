package com.nathaniel.adapter.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.vipon.adapter
 * @datetime 2020/4/5 - 16:07
 */
public abstract class BaseLoadingView extends RelativeLayout {
    private View contentLayout;

    public BaseLoadingView(Context context) {
        super(context);
        int layoutId = getLoadingLayoutId();
        if (layoutId == 0) {
            throw new IllegalArgumentException(BaseEmptyView.class.getSimpleName() + " : Must set content layout!");
        }
        contentLayout = inflate(context, layoutId, this);
        initialize();
    }

    public BaseLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaseLoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected abstract void initialize();

    protected abstract int getLoadingLayoutId();

    protected abstract void setLoadingMessage(CharSequence loadingMessage);
}
