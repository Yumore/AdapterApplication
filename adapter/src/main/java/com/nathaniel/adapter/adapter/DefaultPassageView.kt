package com.nathaniel.adapter.adapter

import android.content.Context
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import com.nathaniel.adapter.R

/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.vipon.adapter
 * @datetime 2020/4/5 - 1:32
 */
class DefaultPassageView(context: Context?) : BasePassageView(context) {
    private var commonPassageStatusRl: RelativeLayout? = null
    private var commonPassageLoadingPb: ProgressBar? = null
    private var commonPassageStatusIv: ImageView? = null
    private var commonPassageStatusTv: TextView? = null
    override fun getPassageLayoutId(): Int {
        return R.layout.common_defalut_passage
    }

    override fun initialize() {
        commonPassageStatusRl = findViewById(R.id.common_passage_status_rl)
        commonPassageLoadingPb = findViewById(R.id.common_passage_loading_pb)
        commonPassageStatusIv = findViewById(R.id.common_passage_status_iv)
        commonPassageStatusTv = findViewById(R.id.common_passage_status_tv)
    }

    override fun setBeforeLoadingUi() {
        commonPassageStatusRl!!.visibility = GONE
        commonPassageStatusTv!!.setText(R.string.passage_tips_initialize)
    }

    override fun setOnLoadingUi() {
        commonPassageStatusRl!!.visibility = VISIBLE
        commonPassageStatusTv!!.visibility = VISIBLE
        commonPassageLoadingPb!!.visibility = VISIBLE
        commonPassageStatusIv!!.visibility = GONE
        commonPassageStatusTv!!.setText(R.string.passage_tips_loading)
    }

    override fun setLoadSuccessUi() {
        commonPassageStatusRl!!.visibility = VISIBLE
        commonPassageLoadingPb!!.visibility = GONE
        commonPassageStatusIv!!.visibility = VISIBLE
        commonPassageStatusIv!!.setImageResource(R.drawable.icon_passage_success)
        commonPassageStatusTv!!.setText(R.string.passage_tips_success)
    }

    override fun setLoadFailUi() {
        commonPassageStatusRl!!.visibility = VISIBLE
        commonPassageLoadingPb!!.visibility = GONE
        commonPassageStatusIv!!.visibility = VISIBLE
        commonPassageStatusIv!!.setImageResource(R.drawable.icon_passage_failure)
        commonPassageStatusTv!!.setText(R.string.passage_tips_failure)
    }

    override fun setWithoutUi() {
        commonPassageStatusRl!!.visibility = GONE
        commonPassageStatusTv!!.setText(R.string.passage_tips_without)
    }
}