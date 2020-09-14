package com.nathaniel.adapter.adapter

import android.content.Context
import android.text.TextUtils
import android.widget.ProgressBar
import android.widget.TextView
import com.nathaniel.adapter.R

/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.vipon.adapter
 * @datetime 2020/4/5 - 16:14
 */
class DefaultLoadingView(context: Context?) : BaseLoadingView(context) {
    private var loadingDialogProgress: ProgressBar? = null
    private var loadingDialogMessage: TextView? = null
    override fun initialize() {
        loadingDialogProgress = findViewById(R.id.loading_dialog_progress)
        loadingDialogMessage = findViewById(R.id.loading_dialog_message)
    }

    override val loadingLayoutId: Int
        protected get() = R.layout.common_loading_layout

    override fun setLoadingMessage(loadingMessage: CharSequence?) {
        if (TextUtils.isEmpty(loadingMessage)) {
            return
        }
        loadingDialogMessage!!.text = loadingMessage
    }
}