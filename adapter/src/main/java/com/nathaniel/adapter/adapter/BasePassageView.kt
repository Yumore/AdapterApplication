package com.nathaniel.adapter.adapter

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.nathaniel.adapter.adapter.BasePassageView

/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.vipon.adapter
 * @datetime 2020/4/5 - 1:14
 */
abstract class BasePassageView : RelativeLayout {
    protected var contentLayout: View? = null
    protected var onPassageListener: OnPassageListener? = null
    var loadingStatus = 0
        set(loadingStatus) {
            field = loadingStatus
            when (loadingStatus) {
                STATUS_INITIAL -> setBeforeLoadingUi()
                STATUS_LOADING -> setOnLoadingUi()
                STATUS_SUCCESS -> setLoadSuccessUi()
                STATUS_FAILURE -> setLoadFailUi()
                STATUS_WITHOUT -> setWithoutUi()
                else -> {
                }
            }
        }

    constructor(context: Context?) : super(context) {
        setWillNotDraw(false)
        val layoutId = passageLayoutId
        require(layoutId != 0) { BasePassageView::class.java.simpleName + " : Must set content layout!" }
        contentLayout = inflate(context, layoutId, this)
        initialize()
        loadingStatus = STATUS_INITIAL
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    protected abstract val passageLayoutId: Int
    protected abstract fun initialize()
    protected abstract fun setBeforeLoadingUi()
    protected abstract fun setOnLoadingUi()
    protected abstract fun setLoadSuccessUi()
    protected abstract fun setLoadFailUi()
    protected abstract fun setWithoutUi()

    companion object {
        const val STATUS_INITIAL = 0x00000001
        const val STATUS_LOADING = 0x00000002
        const val STATUS_SUCCESS = 0x00000003
        const val STATUS_FAILURE = 0x00000004
        const val STATUS_WITHOUT = 0x00000005
    }
}