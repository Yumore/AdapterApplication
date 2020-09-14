package com.nathaniel.sample

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.nathaniel.refresh.FooterView
import com.nathaniel.refresh.HeaderView
import com.nathaniel.refresh.RefreshLayout
import com.nathaniel.refresh.RefreshLayout.OnLoadMoreListener
import java.util.*

/**
 * @author nathaniel
 */
class ListViewActivity : AppCompatActivity() {
    private var mRefreshLayout: RefreshLayout? = null
    private var mListView: ListView? = null
    private var mAdapter: ArrayAdapter<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_view)
        mRefreshLayout = findViewById<View>(R.id.refresh_layout) as RefreshLayout
        mListView = findViewById<View>(R.id.list_view) as ListView
        mAdapter = ArrayAdapter(this, R.layout.adapter_item, R.id.tv_name)
        mListView!!.adapter = mAdapter

        //设置头部(刷新)
        val headerView = HeaderView(this)
        val refreshTime = PreferencesUtils.getRefreshTime(LV_REFRESH_TIME)
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
                    PreferencesUtils.writeRefreshTime(LV_REFRESH_TIME, Date().time)
                    //通知刷新完成
                    mRefreshLayout!!.finishRefresh(true)
                    //是否还有更多数据
                    mRefreshLayout!!.hasMore(true)
                    mAdapter!!.clear()
                    mAdapter!!.addAll(getData(20, 0))
                }, 3000)
            }
        })

        //设置上拉加载更多的监听，触发加载时回调。
        //RefreshLayout默认没有启用上拉加载更多的功能，如果设置了OnLoadMoreListener，则自动启用。
        mRefreshLayout!!.setOnLoadMoreListener(object : OnLoadMoreListener {
            override fun onLoadMore() {
                mRefreshLayout!!.postDelayed({
                    if (mAdapter!!.count < 50) {
                        mAdapter!!.addAll(getData(10, mAdapter!!.count))
                        //通知加载完成
                        mRefreshLayout!!.finishLoadMore(true, true)
                    } else {
                        //通知加载完成
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
//        // 是否自动触发加载更多。只有在启用了上拉加载更多功能时起作用。
//        mRefreshLayout.setAutoLoadMore(true);
//
//        // 隐藏内容布局，显示空布局
//        mRefreshLayout.showEmpty();
//
//        // 隐藏空布局，显示内容布局
//        mRefreshLayout.hideEmpty();
    }

    private fun getData(count: Int, position: Int): List<String> {
        val list: MutableList<String> = ArrayList()
        for (i in 0 until count) {
            list.add("ListView item:" + (i + position))
        }
        return list
    }

    companion object {
        private const val LV_REFRESH_TIME = "LV_Refresh_Time"
    }
}