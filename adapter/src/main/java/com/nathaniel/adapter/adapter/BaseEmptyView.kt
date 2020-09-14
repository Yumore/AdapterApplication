package com.nathaniel.adapter.adapter

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.nathaniel.adapter.adapter.BaseEmptyView

/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.vipon.adapter
 * @datetime 2020/4/5 - 2:13
 */
abstract class BaseEmptyView : RelativeLayout {
    protected var contentLayout: View? = null

    constructor(context: Context?) : super(context) {
        val layoutId = emptyLayoutId
        require(layoutId != 0) { BaseEmptyView::class.java.simpleName + " : Must set content layout!" }
        contentLayout = inflate(context, layoutId, this)
        initialize()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}

    protected abstract fun initialize()
    protected abstract val emptyLayoutId: Int
    protected abstract fun setEmptyOptionText(optionText: CharSequence?, onEmptyListener: OnEmptyListener?)
    abstract fun setEmptyMessage(emptyMessage: CharSequence?)
    protected abstract fun setEmptyImageResource(drawableRes: Int)
}