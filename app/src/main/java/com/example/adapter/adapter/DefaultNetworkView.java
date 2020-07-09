package com.example.adapter.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adapter.R;


/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.vipon.adapter
 * @datetime 2020/4/5 - 12:06
 */
public class DefaultNetworkView extends BaseNetworkView {
    private ImageView commonNetworkImage;
    private TextView commonNetworkMessage;
    private TextView commonNetworkOption;

    public DefaultNetworkView(Context context) {
        super(context);
    }

    @Override
    protected void initialize() {
        commonNetworkImage = findViewById(R.id.common_network_image);
        commonNetworkMessage = findViewById(R.id.common_network_message);
        commonNetworkOption = findViewById(R.id.common_network_option);
    }

    @Override
    protected int getNetworkLayoutId() {
        return R.layout.common_network_layout;
    }

    @Override
    protected void setNetworkOptionText(CharSequence optionText, final OnNetworkListener onNetworkListener) {
        if (!TextUtils.isEmpty(optionText)) {
            commonNetworkOption.setText(optionText);
            commonNetworkOption.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onNetworkListener != null) {
                        onNetworkListener.onNetwork();
                    }
                }
            });
        }
    }

    @Override
    protected void setNetworKImageResource(int drawableRes) {
        if (drawableRes != 0) {
            commonNetworkImage.setImageResource(drawableRes);
            commonNetworkImage.setVisibility(VISIBLE);
        }
    }
}
