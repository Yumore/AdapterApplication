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
 * @datetime 2020/4/5 - 2:22
 */
public class DefaultEmptyView extends BaseEmptyView {

    private ImageView commonEmptyImage;
    private TextView commonEmptyMessage;
    private TextView commonEmptyOption;

    public DefaultEmptyView(Context context) {
        super(context);
    }

    @Override
    protected void initialize() {
        commonEmptyImage = findViewById(R.id.common_empty_image);
        commonEmptyMessage = findViewById(R.id.common_empty_message);
        commonEmptyOption = findViewById(R.id.common_empty_option);
    }

    @Override
    protected int getEmptyLayoutId() {
        return R.layout.common_empty_layout;
    }


    @Override
    protected void setEmptyOptionText(CharSequence optionText, final OnEmptyListener onEmptyListener) {
        if (TextUtils.isEmpty(optionText)) {
            return;
        }
        commonEmptyOption.setText(optionText);
        commonEmptyOption.setVisibility(VISIBLE);
        commonEmptyOption.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onEmptyListener != null) {
                    onEmptyListener.onEmpty();
                }
            }
        });
    }

    @Override
    protected void setEmptyMessage(CharSequence emptyMessage) {
        if (commonEmptyMessage != null) {
            commonEmptyMessage.setText(emptyMessage);
        }
    }

    @Override
    protected void setEmptyImageResource(int drawableRes) {
        if (drawableRes != 0) {
            commonEmptyImage.setImageResource(drawableRes);
            commonEmptyImage.setVisibility(VISIBLE);
        }
    }
}
