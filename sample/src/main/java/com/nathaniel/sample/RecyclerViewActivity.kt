package com.nathaniel.sample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nathaniel.refresh.FooterView
import com.nathaniel.refresh.HeaderView
import com.nathaniel.refresh.RefreshLayout
import com.nathaniel.refresh.RefreshLayout.OnLoadMoreListener
import java.util.*

/**
 * @author nathaniel
 */
class RecyclerViewActivity : AppCompatActivity() {
    private var mRefreshLayout: RefreshLayout? = null
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: RecyclerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)
        mRefreshLayout = findViewById<View>(R.id.refresh_layout) as RefreshLayout
        mRecyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        mRecyclerView!!.layoutManager = LinearLayoutManager(this)
        mAdapter = RecyclerAdapter(this)
        mRecyclerView!!.adapter = mAdapter

        //设置头部(刷新)
        val headerView = HeaderView(this)
        val refreshTime = PreferencesUtils.getRefreshTime(RV_REFRESH_TIME)
        if (refreshTime > 0) {
            headerView.setRefreshTime(Date(refreshTime))
        }
        mRefreshLayout!!.setHeaderView(headerView)

        //设置尾部(加载更新)
        mRefreshLayout!!.setFooterView(FooterView(this))

        //设置刷新监听，触发刷新时回调
        mRefreshLayout!!.setOnRefreshListener(object : RefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                //延时3秒刷新完成，模拟网络加载的情况
                mRefreshLayout!!.postDelayed({
                    PreferencesUtils.writeRefreshTime(RV_REFRESH_TIME, Date().time)
                    //通知刷新完成
                    mRefreshLayout!!.finishRefresh(true)
                    //是否还有更多数据
                    mRefreshLayout!!.hasMore(true)
                    mAdapter!!.setCount(20)
                }, 3000)
            }
        })

        //设置上拉加载更多的监听，触发加载时回调。
        //RefreshLayout默认没有启用上拉加载更多的功能，如果设置了OnLoadMoreListener，则自动启用。
        mRefreshLayout!!.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                mRefreshLayout!!.postDelayed({ //通知加载完成
                    if (mAdapter!!.itemCount < 50) {
                        mAdapter!!.setCount(mAdapter!!.itemCount + 10)
                        mRefreshLayout!!.finishLoadMore(true, true)
                    } else {
                        mRefreshLayout!!.finishLoadMore(true, false)
                    }
                }, 3000)
            }
        })

//        // 启用下拉刷新功能。默认启用
//        mRefreshLayout.setRefreshEnable(true);
//
//        // 启用上拉加载更多功能。默认不启用，如果设置了OnLoadMoreListener，则自动启用。
//        mRefreshLayout.setLoadMoreEnable(true);
//
//        // 是否还有更多数据，只有为true是才能上拉加载更多，它会回调FooterView的onHasMore()方法。默认为true。
//        mRefreshLayout.hasMore(true);

        //自动触发下拉刷新。只有启用了下拉刷新功能时起作用。
        mRefreshLayout!!.autoRefresh()

//        //自动触发上拉加载更多。只有在启用了上拉加载更多功能并且有更多数据时起作用。
//        mRefreshLayout.autoLoadMore();
//
//        // 是否自动触发加载更多，在滑动到底部的时候，自动加载更多。只有在启用了上拉加载更多功能时起作用。
//        mRefreshLayout.setAutoLoadMore(true);
//
//        // 隐藏内容布局，显示空布局。
//        mRefreshLayout.showEmpty();
//
//        // 隐藏空布局，显示内容布局。
//        mRefreshLayout.hideEmpty();
    }

    companion object {
        private const val RV_REFRESH_TIME = "RV_Refresh_Time"
    }
}