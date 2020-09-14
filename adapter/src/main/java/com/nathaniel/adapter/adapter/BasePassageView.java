package com.nathaniel.adapter.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.vipon.adapter
 * @datetime 2020/4/5 - 1:14
 */
public abstract class BasePassageView extends RelativeLayout {
    public static final int STATUS_INITIAL = 0x00000001;
    public static final int STATUS_LOADING = 0x00000002;
    public static final int STATUS_SUCCESS = 0x00000003;
    public static final int STATUS_FAILURE = 0x00000004;
    public static final int STATUS_WITHOUT = 0x00000005;
    protected View contentLayout;
    protected OnPassageListener onPassageListener;
    private int loadingStatus;

    public BasePassageView(Context context) {
        super(context);
        setWillNotDraw(false);
        int layoutId = getPassageLayoutId();
        if (layoutId == 0) {
            throw new IllegalArgumentException(BasePassageView.class.getSimpleName() + " : Must set content layout!");
        }
        contentLayout = inflate(context, layoutId, this);
        initialize();
        setLoadingStatus(STATUS_INITIAL);
    }

    public BasePassageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BasePassageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BasePassageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public int getLoadingStatus() {
        return loadingStatus;
    }

    public void setLoadingStatus(int loadingStatus) {
        this.loadingStatus = loadingStatus;
        switch (loadingStatus) {
            case STATUS_INITIAL:
                setBeforeLoadingUi();
                break;
            case STATUS_LOADING:
                setOnLoadingUi();
                break;
            case STATUS_SUCCESS:
                setLoadSuccessUi();
                break;
            case STATUS_FAILURE:
                setLoadFailUi();
                break;
            case STATUS_WITHOUT:
                setWithoutUi();
                break;
            default:
                break;
        }
    }

    protected abstract int getPassageLayoutId();

    protected abstract void initialize();

    protected abstract void setBeforeLoadingUi();

    protected abstract void setOnLoadingUi();

    protected abstract void setLoadSuccessUi();

    protected abstract void setLoadFailUi();

    protected abstract void setWithoutUi();

    public void setOnPassageListener(OnPassageListener onPassageListener) {
        this.onPassageListener = onPassageListener;
    }
}
