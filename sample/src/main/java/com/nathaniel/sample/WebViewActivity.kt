package com.nathaniel.sample

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.nathaniel.refresh.HeaderView
import com.nathaniel.refresh.RefreshLayout
import java.util.*

/**
 * @author nathaniel
 */
class WebViewActivity : AppCompatActivity() {
    private var mRefreshLayout: RefreshLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        mRefreshLayout = findViewById<View>(R.id.refresh_layout) as RefreshLayout
        val webView = findViewById<View>(R.id.web_view) as WebView
        webView.loadUrl("https://www.baidu.com")
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return super.shouldOverrideUrlLoading(view, url)
            }
        }

        //设置头部(刷新)
        val headerView = HeaderView(this)
        val refreshTime = PreferencesUtils.getRefreshTime(WV_REFRESH_TIME)
        if (refreshTime > 0) {
            headerView.setRefreshTime(Date(refreshTime))
        }
        mRefreshLayout!!.setHeaderView(headerView)

        //设置刷新监听，触发刷新时回调
        mRefreshLayout!!.setOnRefreshListener(object : RefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                //延时3秒刷新完成，模拟网络加载的情况
                mRefreshLayout!!.postDelayed({
                    PreferencesUtils.writeRefreshTime(WV_REFRESH_TIME, System.currentTimeMillis())
                    //通知刷新完成
                    mRefreshLayout!!.finishRefresh(true)
                }, 3000)
            }
        })

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

    companion object {
        private const val WV_REFRESH_TIME = "WV_Refresh_Time"
    }
}