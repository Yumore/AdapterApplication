package com.nathaniel.adapter.adapter

import android.content.Context
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import com.nathaniel.adapter.R

/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.vipon.adapter
 * @datetime 2020/4/5 - 12:06
 */
class DefaultNetworkView(context: Context?) : BaseNetworkView(context) {
    private var commonNetworkImage: ImageView? = null
    private var commonNetworkMessage: TextView? = null
    private var commonNetworkOption: TextView? = null
    override fun initialize() {
        commonNetworkImage = findViewById(R.id.common_network_image)
        commonNetworkMessage = findViewById(R.id.common_network_message)
        commonNetworkOption = findViewById(R.id.common_network_option)
    }

    override val networkLayoutId: Int
        protected get() = R.layout.common_network_layout

    override fun setNetworkOptionText(optionText: CharSequence?, onNetworkListener: OnNetworkListener?) {
        if (!TextUtils.isEmpty(optionText)) {
            commonNetworkOption!!.text = optionText
            commonNetworkOption!!.setOnClickListener { onNetworkListener?.onNetwork() }
        }
    }

    override fun setNetworKImageResource(drawableRes: Int) {
        if (drawableRes != 0) {
            commonNetworkImage!!.setImageResource(drawableRes)
            commonNetworkImage!!.visibility = VISIBLE
        }
    }
}