package com.nathaniel.sample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nathaniel.adapter.adapter.BaseRecyclerAdapter;
import com.nathaniel.adapter.adapter.OnItemChildClickListener;
import com.nathaniel.adapter.adapter.OnItemClickListener;
import com.nathaniel.adapter.utility.EmptyUtils;
import com.nathaniel.adapter.utility.LoggerUtils;
import com.nathaniel.refresh.FooterView;
import com.nathaniel.refresh.HeaderView;
import com.nathaniel.refresh.RefreshLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author nathaniel
 */
public class SampleActivity extends AppCompatActivity implements OnItemClickListener, OnItemChildClickListener, RefreshLayout.OnLoadMoreListener, RefreshLayout.OnRefreshListener {
    private static final String RV_REFRESH_TIME = "RV_Refresh_Time";
    private static final String TAG = SampleActivity.class.getSimpleName();
    private List<String> dataList;
    private RecyclerView recyclerView;
    private SampleAdapter sampleAdapter;
    private int passage = 1;
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 0x102) {
                List<String> stringList = (List<String>) msg.obj;
                if (passage == 1 && !EmptyUtils.isObjectEmpty(dataList)) {
                    dataList.clear();
                }
                if (EmptyUtils.isObjectEmpty(stringList)) {
                    sampleAdapter.setEmptyMessage(SampleActivity.this, "data is empty");
                } else {
                    dataList.addAll(stringList);
                }
                loading = false;
                sampleAdapter.notifyDataSetChanged();
            } else {
                super.handleMessage(msg);
            }

        }
    };
    private boolean withoutMore, loading;
    private RefreshLayout refreshLayout;
    private HeaderView headerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        loadData();

        initView();

        bindView();
    }

    private void loadData() {
        dataList = new ArrayList<>();
        sampleAdapter = new SampleAdapter(R.layout.item_recycler_list, dataList);
        requestData(passage);

        //设置头部(刷新)
        headerView = new HeaderView(this);
        long refreshTime = PreferencesUtils.getRefreshTime(RV_REFRESH_TIME);
        if (refreshTime > 0) {
            headerView.setRefreshTime(new Date(refreshTime));
        }
    }

    private void requestData(int passage) {
        new Handler().postDelayed(new LoadThread(passage), 2500);
    }

    private void bindView() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(sampleAdapter);
        sampleAdapter.setOnItemClickListener(this);
        sampleAdapter.setOnItemChildClickListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                int visibleItemCount = recyclerView.getChildCount();
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition == totalItemCount - 1 && visibleItemCount < lastVisibleItemPosition && !withoutMore && !loading) {
                    passage++;
                    requestData(passage);
                    sampleAdapter.setPassageEnable(SampleActivity.this, true);
                    loading = true;
                }
                LoggerUtils.logger(TAG, "visibleItemCount = " + visibleItemCount + ", lastVisibleItemPosition = " + lastVisibleItemPosition);
            }
        });

        refreshLayout.setHeaderView(headerView);
        refreshLayout.setFooterView(new FooterView(this));

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadMoreListener(this);

        refreshLayout.autoRefresh();
    }

    private void initView() {
        refreshLayout = (RefreshLayout) findViewById(R.id.refresh_layout);
        recyclerView = findViewById(R.id.recyclerView);
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter<?> adapter, View view, int position) {
        Toast.makeText(this, "Item: 我被点击了" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemChildClick(BaseRecyclerAdapter<?> adapter, View view, int position) {
        if (view.getId() == R.id.item_button_tv) {
            Toast.makeText(this, "Button: 我被点击了" + position, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    @Override
    public void onLoadMore() {
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                //通知加载完成
                if (sampleAdapter.getItemCount() < 50) {
                    requestData(passage++);
                    refreshLayout.finishLoadMore(true, true);
                } else {
                    refreshLayout.finishLoadMore(true, false);
                }
            }
        }, 3000);
    }

    @Override
    public void onRefresh() {
        //延时3秒刷新完成，模拟网络加载的情况
        refreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                //通知刷新完成
                refreshLayout.finishRefresh(true);
                //是否还有更多数据
                refreshLayout.hasMore(true);
                passage = 1;
                requestData(passage);
            }
        }, 3000);
    }

    private class LoadThread implements Runnable {
        private final int passage;

        public LoadThread(int passage) {
            this.passage = passage;
        }

        @Override
        public void run() {
            List<String> datas = new ArrayList<>();
            for (int i = 0; i < passage * 50; i++) {
                datas.add(((passage - 1) * 50 + (i + 1)) + "test data");
            }
            Message message = handler.obtainMessage();
            message.what = 0x102;
            message.obj = datas;
            handler.sendMessage(message);

            if (passage == 10) {
                withoutMore = true;
            }
        }
    }
}
