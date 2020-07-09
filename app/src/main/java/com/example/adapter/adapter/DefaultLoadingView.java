package com.example.adapter.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.adapter.R;


/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.vipon.adapter
 * @datetime 2020/4/5 - 16:14
 */
public class DefaultLoadingView extends BaseLoadingView {
    private ProgressBar loadingDialogProgress;
    private TextView loadingDialogMessage;

    public DefaultLoadingView(Context context) {
        super(context);
    }

    @Override
    protected void initialize() {
        loadingDialogProgress = findViewById(R.id.loading_dialog_progress);
        loadingDialogMessage = findViewById(R.id.loading_dialog_message);
    }

    @Override
    protected int getLoadingLayoutId() {
        return R.layout.common_loading_layout;
    }

    @Override
    protected void setLoadingMessage(CharSequence loadingMessage) {
        if (TextUtils.isEmpty(loadingMessage)) {
            return;
        }
        loadingDialogMessage.setText(loadingMessage);
    }
}
