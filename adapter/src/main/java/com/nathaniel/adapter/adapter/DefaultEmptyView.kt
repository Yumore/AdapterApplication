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
 * @datetime 2020/4/5 - 2:22
 */
open class DefaultEmptyView(context: Context?) : BaseEmptyView(context) {
    private var commonEmptyImage: ImageView? = null
    private var commonEmptyMessage: TextView? = null
    private var commonEmptyOption: TextView? = null
    override fun initialize() {
        commonEmptyImage = findViewById(R.id.common_empty_image)
        commonEmptyMessage = findViewById(R.id.common_empty_message)
        commonEmptyOption = findViewById(R.id.common_empty_option)
    }

    override val emptyLayoutId: Int
        get() = R.layout.common_empty_layout

    override fun setEmptyOptionText(optionText: CharSequence?, onEmptyListener: OnEmptyListener?) {
        if (TextUtils.isEmpty(optionText)) {
            return
        }
        commonEmptyOption!!.text = optionText
        commonEmptyOption!!.visibility = VISIBLE
        commonEmptyOption!!.setOnClickListener { onEmptyListener?.onEmpty() }
    }

    override fun setEmptyMessage(emptyMessage: CharSequence?) {
        if (commonEmptyMessage != null) {
            commonEmptyMessage!!.text = emptyMessage
        }
    }

    override fun setEmptyImageResource(drawableRes: Int) {
        if (drawableRes != 0) {
            commonEmptyImage!!.setImageResource(drawableRes)
            commonEmptyImage!!.visibility = VISIBLE
        }
    }
}