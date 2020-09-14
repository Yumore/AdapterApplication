package com.nathaniel.sample

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nathaniel.adapter.adapter.BaseRecyclerAdapter
import com.nathaniel.adapter.adapter.OnItemChildClickListener
import com.nathaniel.adapter.adapter.OnItemClickListener
import com.nathaniel.adapter.utility.EmptyUtils.isObjectEmpty
import com.nathaniel.adapter.utility.LoggerUtils.logger
import com.nathaniel.sample.SampleActivity
import java.util.*

/**
 * @author nathaniel
 */
class SampleActivity : AppCompatActivity(), OnItemClickListener, OnItemChildClickListener, SwipeRefreshLayout.OnRefreshListener {
    private var dataList: MutableList<String?>? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var recyclerView: RecyclerView? = null
    private var sampleAdapter: SampleAdapter? = null
    private var passage = 1

    @SuppressLint("HandlerLeak")
    private val handler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            if (swipeRefreshLayout!!.isRefreshing) {
                swipeRefreshLayout!!.isRefreshing = false
            }
            if (msg.what == 0x102) {
                val stringList = msg.obj as List<String?>
                if (passage == 1 && !isObjectEmpty(dataList)) {
                    dataList!!.clear()
                }
                if (isObjectEmpty(stringList)) {
                    sampleAdapter!!.setEmptyMessage(this@SampleActivity, "data is empty")
                } else {
                    dataList!!.addAll(stringList)
                }
                loading = false
                sampleAdapter!!.notifyDataSetChanged()
            } else {
                super.handleMessage(msg)
            }
        }
    }
    private var withoutMore = false
    private var loading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        loadData()
        initView()
        bindView()
    }

    private fun loadData() {
        dataList = ArrayList()
        sampleAdapter = SampleAdapter(R.layout.item_recycler_list, dataList)
        requestData(passage)
    }

    private fun requestData(passage: Int) {
        Handler().postDelayed(LoadThread(passage), 2500)
    }

    private fun bindView() {
        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.adapter = sampleAdapter
        sampleAdapter!!.setOnItemClickListener(this)
        sampleAdapter!!.onItemChildClickListener = this
        swipeRefreshLayout!!.setOnRefreshListener(this)
        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val totalItemCount = recyclerView.adapter!!.itemCount
                val lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition()
                val visibleItemCount = recyclerView.childCount
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition == totalItemCount - 1 && visibleItemCount < lastVisibleItemPosition && !withoutMore && !loading) {
                    passage++
                    requestData(passage)
                    sampleAdapter!!.setPassageEnable(this@SampleActivity, true)
                    loading = true
                }
                logger(TAG, "visibleItemCount = $visibleItemCount, lastVisibleItemPosition = $lastVisibleItemPosition")
            }
        })
        swipeRefreshLayout!!.isRefreshing = true
    }

    private fun initView() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        recyclerView = findViewById(R.id.recyclerView)
    }

    override fun onItemClick(adapter: BaseRecyclerAdapter<*>?, view: View?, position: Int) {
        Toast.makeText(this, "Item: 我被点击了$position", Toast.LENGTH_SHORT).show()
    }

    override fun onItemChildClick(adapter: BaseRecyclerAdapter<*>?, view: View?, position: Int): Boolean {
        return when (view!!.id) {
            R.id.item_button_tv -> {
                Toast.makeText(this, "Button: 我被点击了$position", Toast.LENGTH_SHORT).show()
                true
            }
            else -> false
        }
    }

    override fun onRefresh() {
        passage = 1
        requestData(passage)
    }

    private inner class LoadThread(private val passage: Int) : Runnable {
        override fun run() {
            val datas: MutableList<String> = ArrayList()
            for (i in 0 until passage * 50) {
                datas.add(((passage - 1) * 50 + (i + 1)).toString() + "test data")
            }
            val message = handler.obtainMessage()
            message.what = 0x102
            message.obj = datas
            handler.sendMessage(message)
            if (passage == 10) {
                withoutMore = true
            }
        }
    }

    companion object {
        private val TAG = SampleActivity::class.java.simpleName
    }
}