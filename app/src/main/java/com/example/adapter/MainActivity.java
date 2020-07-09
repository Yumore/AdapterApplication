package com.example.adapter;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.adapter.adapter.BaseRecyclerAdapter;
import com.example.adapter.adapter.OnItemChildClickListener;
import com.example.adapter.adapter.OnItemClickListener;
import com.example.adapter.adapter.SampleAdapter;
import com.example.adapter.utility.EmptyUtils;
import com.example.adapter.utility.LoggerUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnItemClickListener, OnItemChildClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private List<String> dataList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private SampleAdapter sampleAdapter;
    private int passage = 1;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            if (msg.what == 0x102) {
                List<String> stringList = (List<String>) msg.obj;
                if (passage == 1 && !EmptyUtils.isObjectEmpty(dataList)) {
                    dataList.clear();
                }
                if (EmptyUtils.isObjectEmpty(stringList)) {
                    sampleAdapter.setEmptyMessage(MainActivity.this, "data is empty");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadData();

        initView();

        bindView();
    }

    private void loadData() {
        dataList = new ArrayList<>();
        sampleAdapter = new SampleAdapter(R.layout.item_recycler_list, dataList);
        requestData(passage);
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
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                int totalItemCount = recyclerView.getAdapter().getItemCount();
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                int visibleItemCount = recyclerView.getChildCount();
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition == totalItemCount - 1 && visibleItemCount < lastVisibleItemPosition && !withoutMore && !loading) {
                    passage++;
                    requestData(passage);
                    sampleAdapter.setPassageEnable(MainActivity.this, true);
                    loading = true;
                }
                LoggerUtils.logger(TAG, "visibleItemCount = " + visibleItemCount + ", lastVisibleItemPosition = " + lastVisibleItemPosition);
            }
        });
        swipeRefreshLayout.setRefreshing(true);

    }

    private void initView() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
    }

    @Override
    public void onItemClick(BaseRecyclerAdapter adapter, View view, int position) {
        Toast.makeText(this, "Item: 我被点击了" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemChildClick(BaseRecyclerAdapter adapter, View view, int position) {
        switch (view.getId()) {
            case R.id.item_button_tv:
                Toast.makeText(this, "Button: 我被点击了" + position, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return false;
        }

    }

    @Override
    public void onRefresh() {
        passage = 1;
        requestData(passage);
    }

    private class LoadThread implements Runnable {
        private int passage;

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
