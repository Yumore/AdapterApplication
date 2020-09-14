package com.nathaniel.sample;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.nathaniel.refresh.HeaderView;
import com.nathaniel.refresh.RefreshLayout;

import java.util.Date;

/**
 * @author nathaniel
 */
public class NestedScrollViewActivity extends AppCompatActivity {

    private static final String NS_REFRESH_TIME = "NS_Refresh_Time";
    private RefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_scroll_view);

        mRefreshLayout = (RefreshLayout) findViewById(R.id.refresh_layout);

        //设置头部(刷新)
        HeaderView headerView = new HeaderView(this);
        long refreshTime = PreferencesUtils.getRefreshTime(NS_REFRESH_TIME);
        if (refreshTime > 0) {
            headerView.setRefreshTime(new Date(refreshTime));
        }
        mRefreshLayout.setHeaderView(headerView);

        //设置刷新监听，触发刷新时回调
        mRefreshLayout.setOnRefreshListener(new RefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //延时3秒刷新完成，模拟网络加载的情况
                mRefreshLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        PreferencesUtils.writeRefreshTime(NS_REFRESH_TIME, System.currentTimeMillis());
                        //通知刷新完成
                        mRefreshLayout.finishRefresh(true);
                    }
                }, 3000);
            }
        });

//        // 启用下拉刷新功能。默认启用
//        mRefreshLayout.setRefreshEnable(true);
//
//        //自动触发下拉刷新。只有启用了下拉刷新功能时起作用。
//        mRefreshLayout.autoRefresh();
//
//        // 隐藏内容布局，显示空布局
//        mRefreshLayout.showEmpty();
//
//        // 隐藏空布局，显示内容布局
//        mRefreshLayout.hideEmpty();
    }
}
