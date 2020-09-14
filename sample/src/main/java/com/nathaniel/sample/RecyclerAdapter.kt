package com.nathaniel.sample

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nathaniel.sample.RecyclerAdapter.ItemHolder

/**
 * @author nathaniel
 */
class RecyclerAdapter(private val mContext: Context) : RecyclerView.Adapter<ItemHolder>() {
    private var mCount = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.adapter_item, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.tvName.text = "item:$position"
    }

    override fun getItemCount(): Int {
        return mCount
    }

    fun setCount(count: Int) {
        mCount = count
        notifyDataSetChanged()
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView

        init {
            tvName = itemView.findViewById<View>(R.id.tv_name) as TextView
        }
    }
}