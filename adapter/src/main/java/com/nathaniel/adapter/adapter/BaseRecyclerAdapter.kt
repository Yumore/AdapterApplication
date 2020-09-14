package com.nathaniel.adapter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.nathaniel.adapter.utility.LoggerUtils

/**
 * @author nathaniel
 */
abstract class BaseRecyclerAdapter<T>
/**
 * init data and layout
 *
 * @param dataList dataList
 * @param layoutId layoutId
 */(@field:LayoutRes @param:LayoutRes private val layoutId: Int, private var dataList: MutableList<T>?) : RecyclerView.Adapter<BaseViewHolder>() {
    var onItemChildClickListener: OnItemChildClickListener? = null
    private var onItemClickListener: OnItemClickListener? = null
    var context: Context? = null
        private set
    private var headerLayout: LinearLayout? = null
    private var footerLayout: LinearLayout? = null
    private var emptyLayout: FrameLayout? = null
    private var passageView: BasePassageView? = null
    private var emptyView: BaseEmptyView? = null
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    /**
     * bind data to view
     *
     * @param viewHolder viewHolder
     * @param data       data
     */
    abstract fun bindDataToView(viewHolder: BaseViewHolder, data: T)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        context = parent.context
        val itemView: View
        val viewHolder: BaseViewHolder
        when (viewType) {
            EMPTY_VIEW -> viewHolder = BaseViewHolder(emptyLayout!!)
            HEADER_VIEW -> viewHolder = BaseViewHolder(headerLayout!!)
            FOOTER_VIEW -> viewHolder = BaseViewHolder(footerLayout!!)
            else -> {
                itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
                val baseViewHolder = BaseViewHolder(itemView)
                addViewListener(itemView, baseViewHolder)
                baseViewHolder.setAdapter(this)
                viewHolder = baseViewHolder
            }
        }
        return viewHolder
    }

    private fun addViewListener(itemView: View, viewHolder: BaseViewHolder) {
        itemView.setOnClickListener { view ->
            LoggerUtils.logger(TAG, "real position: " + (viewHolder.adapterPosition - headerCount) + ", view holder position: " + viewHolder.adapterPosition)
            onItemClickListener!!.onItemClick(this@BaseRecyclerAdapter, view, viewHolder.adapterPosition - headerCount)
        }
    }

    override fun onBindViewHolder(viewHolder: BaseViewHolder, position: Int) {
        LoggerUtils.logger(TAG, "adapter position = " + position + ", view holder position = " + (viewHolder.adapterPosition - headerCount) + ", list size = " + dataSize)
        if (isHeaderView(position) || isEmptyView(position) || isFooterView(position)) {
            LoggerUtils.logger(TAG, "item is header or footer or empty")
            return
        }
        if (viewHolder.adapterPosition - headerCount >= dataSize) {
            return
        }
        bindDataToView(viewHolder, dataList!![viewHolder.adapterPosition - headerCount])
    }

    override fun getItemViewType(position: Int): Int {
        return if (isEmptyView(position)) {
            EMPTY_VIEW
        } else if (isHeaderView(position)) {
            HEADER_VIEW
        } else if (isFooterView(position)) {
            FOOTER_VIEW
        } else {
            super.getItemViewType(position)
        }
    }

    private fun isFooterView(position: Int): Boolean {
        return position >= headerCount + dataSize + emptyCount && position < headerCount + dataSize + emptyCount + footerCount
    }

    private fun isHeaderView(position: Int): Boolean {
        return position < headerCount
    }

    private fun isEmptyView(position: Int): Boolean {
        return dataSize == 0 && emptyCount > 0 && position == headerCount + emptyCount - 1
    }

    override fun getItemCount(): Int {
        return headerCount + dataSize + emptyCount + footerCount
    }

    fun getDataList(): List<T>? {
        return dataList
    }

    fun setDataList(dataList: MutableList<T>?) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

    fun addDataList(dataList: List<T>?) {
        this.dataList!!.addAll(dataList!!)
        notifyDataSetChanged()
    }

    fun addHeaderView(headerView: View) {
        addHeaderView(headerView, 0, LinearLayout.INVISIBLE)
    }

    fun addHeaderView(headerView: View, position: Int) {
        addHeaderView(headerView, position, LinearLayout.VERTICAL)
    }

    /**
     * 添加header
     *
     * @param headerView  headerView
     * @param position    position
     * @param orientation orientation
     */
    fun addHeaderView(headerView: View, position: Int, orientation: Int) {
        var position = position
        if (headerLayout == null) {
            headerLayout = LinearLayout(headerView.context)
            if (orientation == LinearLayout.VERTICAL) {
                headerLayout!!.orientation = LinearLayout.VERTICAL
                headerLayout!!.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            } else {
                headerLayout!!.orientation = LinearLayout.HORIZONTAL
                headerView.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        }
        if (position < 0 || position > headerCount) {
            position = headerCount
        }
        if (position == headerCount && headerLayout!!.getChildAt(position) != null) {
            headerLayout!!.removeViewAt(position)
        }
        val parentViewGroup = headerView.parent as ViewGroup
        parentViewGroup?.removeView(headerView)
        headerLayout!!.addView(headerView, position)
        notifyItemInserted(position)
        notifyDataSetChanged()
    }

    fun addFooterView(footerView: View) {
        addFooterView(footerView, 0, LinearLayout.VERTICAL)
    }

    fun addFooterView(footerView: View, position: Int) {
        addFooterView(footerView, position, LinearLayout.VERTICAL)
    }

    fun removeFooterView(footerView: View?) {
        if (footerCount <= 0 || footerView == null) {
            return
        }
        footerLayout!!.removeView(footerView)
    }

    /**
     * 添加footer
     *
     * @param footerView  footerView
     * @param position    position
     * @param orientation orientation
     */
    fun addFooterView(footerView: View, position: Int, orientation: Int) {
        var position = position
        if (footerLayout == null) {
            footerLayout = LinearLayout(footerView.context)
            if (orientation == LinearLayout.VERTICAL) {
                footerLayout!!.orientation = LinearLayout.VERTICAL
                footerLayout!!.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            } else {
                footerLayout!!.orientation = LinearLayout.HORIZONTAL
                footerView.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
            }
        }
        if (position < 0 || position > headerCount + dataSize + emptyCount + footerCount) {
            position = headerCount + dataSize + emptyCount + footerCount
        }
        if (position == footerCount && footerLayout!!.getChildAt(position) != null) {
            footerLayout!!.removeViewAt(position)
        }
        val parentViewGroup = footerView.parent as ViewGroup
        parentViewGroup?.removeView(footerView)
        footerLayout!!.addView(footerView, position)
        notifyItemInserted(position)
        notifyDataSetChanged()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            val gridLayoutManager = layoutManager
            val spanSizeLookup = gridLayoutManager.spanSizeLookup
            gridLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    return if (isHeaderView(position) || isFooterView(position) || isEmptyView(position)) {
                        gridLayoutManager.spanCount
                    } else {
                        spanSizeLookup?.getSpanSize(position - headerCount) ?: 1
                    }
                }
            }
        } else {
            super.onAttachedToRecyclerView(recyclerView)
        }
    }

    override fun onViewAttachedToWindow(viewHolder: BaseViewHolder) {
        val position = viewHolder.layoutPosition
        if (isHeaderView(position) || isFooterView(position) || isEmptyView(position)) {
            val layoutParams = viewHolder.itemView.layoutParams
            if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                layoutParams.isFullSpan = true
            }
        } else {
            super.onViewAttachedToWindow(viewHolder)
        }
    }

    fun setEmptyMessage(context: Context, message: CharSequence?) {
        val emptyView = getEmptyView(context)
        emptyView.setEmptyMessage(message)
        setEmptyView(emptyView)
    }

    private fun getEmptyView(context: Context): BaseEmptyView {
        if (emptyView == null) {
            emptyView = DefaultEmptyView(context)
        }
        return emptyView!!
    }

    fun setEmptyView(emptyView: View) {
        if (emptyLayout == null) {
            emptyLayout = FrameLayout(emptyView.context)
            val frameLayoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            val viewLayoutParams = emptyView.layoutParams
            if (viewLayoutParams != null) {
                frameLayoutParams.width = viewLayoutParams.width
                frameLayoutParams.height = viewLayoutParams.height
            }
            emptyLayout!!.layoutParams = frameLayoutParams
        }
        emptyLayout!!.removeAllViews()
        emptyLayout!!.addView(emptyView)
        notifyDataSetChanged()
    }

    private fun getPassageView(context: Context): BasePassageView {
        if (passageView == null) {
            passageView = DefaultPassageView(context)
        }
        return passageView!!
    }

    fun setPassageEnable(context: Context, passageEnable: Boolean) {
        if (passageEnable) {
            if (getPassageView(context) != null) {
                removeFooterView(getPassageView(context))
            }
            getPassageView(context).loadingStatus = BasePassageView.STATUS_LOADING
            addFooterView(getPassageView(context))
        } else {
            removeFooterView(getPassageView(context))
        }
    }

    fun setWithoutMore(context: Context) {
        getPassageView(context).loadingStatus = BasePassageView.STATUS_WITHOUT
    }

    val headerCount: Int
        get() = if (headerLayout == null) 0 else headerLayout!!.childCount
    val footerCount: Int
        get() = if (footerLayout == null) 0 else footerLayout!!.childCount
    val dataSize: Int
        get() = if (dataList == null) 0 else dataList.size
    private val emptyCount: Int
        private get() = if (emptyLayout == null) 0 else emptyLayout!!.childCount

    companion object {
        private val TAG = BaseRecyclerAdapter::class.java.simpleName
        private const val HEADER_VIEW = 0x00000111
        private const val FOOTER_VIEW = 0x00000333
        private const val EMPTY_VIEW = 0x00000555
    }
}