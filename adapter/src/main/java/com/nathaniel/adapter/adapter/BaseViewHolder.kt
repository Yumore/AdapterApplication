package com.nathaniel.adapter.adapter

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.util.Linkify
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * if you wanna define a method to set other attributes to view
 * please make sure that you called [.getView]
 * how to use  [.setText]
 *
 * @author nathaniel
 */
class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val childClickIdSet: LinkedHashSet<Int>
    private var baseRecyclerAdapter: BaseRecyclerAdapter<*>? = null
    private var viewSparseArray: SparseArray<View?>? = null
    fun <T : View?> getView(@IdRes viewId: Int): T? {
        if (viewSparseArray == null) {
            viewSparseArray = SparseArray()
        }
        var view = viewSparseArray!![viewId]
        if (view == null) {
            view = itemView.findViewById(viewId)
            viewSparseArray!!.put(viewId, view)
        }
        return view as T?
    }

    fun setText(@IdRes viewId: Int, text: CharSequence?) {
        if (getView<View>(viewId) is TextView) {
            val textView = getView<TextView>(viewId)!!
            textView.text = text
        } else {
            throw IllegalArgumentException("error view type")
        }
    }

    fun setText(@IdRes viewId: Int, resId: Int) {
        if (getView<View>(viewId) is TextView) {
            val textView = getView<TextView>(viewId)!!
            textView.setText(resId)
        } else {
            throw IllegalArgumentException("error view type")
        }
    }

    fun setTextColor(@IdRes viewId: Int, color: Int) {
        if (getView<View>(viewId) is TextView) {
            val textView = getView<TextView>(viewId)!!
            textView.setTextColor(color)
        } else {
            throw IllegalArgumentException("error view type")
        }
    }

    fun setTextColor(@IdRes viewId: Int, color: String?) {
        if (getView<View>(viewId) is TextView) {
            val textView = getView<TextView>(viewId)!!
            textView.setTextColor(Color.parseColor(color))
        } else {
            throw IllegalArgumentException("error view type")
        }
    }

    fun setImageResource(@IdRes viewId: Int, resId: Int) {
        if (getView<View>(viewId) is ImageView) {
            val imageView = getView<ImageView>(viewId)!!
            imageView.setImageResource(resId)
        } else {
            throw IllegalArgumentException("error view type")
        }
    }

    fun setBackgroundResource(@IdRes viewId: Int, resId: Int) {
        getView<View>(viewId)!!.setBackgroundResource(resId)
    }

    fun setBackground(@IdRes viewId: Int, drawable: Drawable?) {
        getView<View>(viewId)!!.background = drawable
    }

    fun setBackgroundColor(@IdRes viewId: Int, color: Int) {
        getView<View>(viewId)!!.setBackgroundColor(color)
    }

    fun setVisibility(@IdRes viewId: Int, visibility: Int) {
        getView<View>(viewId)!!.visibility = visibility
    }

    fun setImageBitmap(@IdRes viewId: Int, bitmap: Bitmap?) {
        if (getView<View>(viewId) is ImageView) {
            val imageView = getView<ImageView>(viewId)!!
            imageView.setImageBitmap(bitmap)
        } else {
            throw IllegalArgumentException("error view type")
        }
    }

    fun addClickListener(@IdRes viewId: Int) {
        childClickIdSet.add(viewId)
        getView<View>(viewId)!!.setOnClickListener { view ->
            if (baseRecyclerAdapter!!.onItemChildClickListener != null) {
                baseRecyclerAdapter!!.onItemChildClickListener?.onItemChildClick(baseRecyclerAdapter, view, adapterPosition)
            }
        }
    }

    fun setTextLinking(@IdRes viewId: Int) {
        val textView = getView<TextView>(viewId)
        if (textView != null) {
            Linkify.addLinks(textView, Linkify.ALL)
        }
    }

    fun setTextTypeface(typeface: Typeface?, vararg viewIds: Int) {
        for (viewId in viewIds) {
            val textView = getView<TextView>(viewId)
            if (textView != null) {
                textView.typeface = typeface
                textView.paintFlags = textView.paintFlags or Paint.SUBPIXEL_TEXT_FLAG
            }
        }
    }

    val itemView: View
        get() = itemView

    fun setAdapter(baseRecyclerAdapter: BaseRecyclerAdapter<*>) {
        this.baseRecyclerAdapter = baseRecyclerAdapter
    }

    fun setAdapter(@IdRes viewId: Int, baseRecyclerAdapter: BaseRecyclerAdapter<*>) {
        if (getView<View>(viewId) is RecyclerView) {
            val recyclerView = getView<RecyclerView>(viewId)!!
            recyclerView.adapter = baseRecyclerAdapter
        } else {
            throw IllegalArgumentException("error view type")
        }
    }

    companion object {
        fun getViewHolder(itemView: View): BaseViewHolder {
            return BaseViewHolder(itemView)
        }

        fun getViewHolder(parent: ViewGroup, @LayoutRes layoutId: Int): BaseViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
            return BaseViewHolder(itemView)
        }
    }

    init {
        childClickIdSet = LinkedHashSet()
    }
}