package com.nathaniel.adapter.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nathaniel.adapter.R;


/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.vipon.adapter
 * @datetime 2020/4/5 - 1:32
 */
public class DefaultPassageView extends BasePassageView {
    private RelativeLayout commonPassageStatusRl;
    private ProgressBar commonPassageLoadingPb;
    private ImageView commonPassageStatusIv;
    private TextView commonPassageStatusTv;

    public DefaultPassageView(Context context) {
        super(context);
    }

    @Override
    protected int getPassageLayoutId() {
        return R.layout.common_defalut_passage;
    }

    @Override
    protected void initialize() {
        commonPassageStatusRl = findViewById(R.id.common_passage_status_rl);
        commonPassageLoadingPb = findViewById(R.id.common_passage_loading_pb);
        commonPassageStatusIv = findViewById(R.id.common_passage_status_iv);
        commonPassageStatusTv = findViewById(R.id.common_passage_status_tv);
    }

    @Override
    protected void setBeforeLoadingUi() {
        commonPassageStatusRl.setVisibility(GONE);
        commonPassageStatusTv.setText(R.string.passage_tips_initialize);
    }

    @Override
    protected void setOnLoadingUi() {
        commonPassageStatusRl.setVisibility(VISIBLE);
        commonPassageStatusTv.setVisibility(VISIBLE);
        commonPassageLoadingPb.setVisibility(VISIBLE);
        commonPassageStatusIv.setVisibility(GONE);
        commonPassageStatusTv.setText(R.string.passage_tips_loading);
    }

    @Override
    protected void setLoadSuccessUi() {
        commonPassageStatusRl.setVisibility(VISIBLE);
        commonPassageLoadingPb.setVisibility(GONE);
        commonPassageStatusIv.setVisibility(VISIBLE);
        commonPassageStatusIv.setImageResource(R.drawable.icon_passage_success);
        commonPassageStatusTv.setText(R.string.passage_tips_success);
    }

    @Override
    protected void setLoadFailUi() {
        commonPassageStatusRl.setVisibility(VISIBLE);
        commonPassageLoadingPb.setVisibility(GONE);
        commonPassageStatusIv.setVisibility(VISIBLE);
        commonPassageStatusIv.setImageResource(R.drawable.icon_passage_failure);
        commonPassageStatusTv.setText(R.string.passage_tips_failure);
    }

    @Override
    protected void setWithoutUi() {
        commonPassageStatusRl.setVisibility(GONE);
        commonPassageStatusTv.setText(R.string.passage_tips_without);
    }
}
