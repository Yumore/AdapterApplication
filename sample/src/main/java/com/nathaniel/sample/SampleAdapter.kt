package com.nathaniel.sample

import com.nathaniel.adapter.adapter.BaseRecyclerAdapter
import com.nathaniel.adapter.adapter.BaseViewHolder

/**
 * @author Nathaniel
 */
class SampleAdapter(layoutId: Int, dataList: List<String?>?) : BaseRecyclerAdapter<String?>(layoutId, dataList) {
    override fun bindDataToView(holder: BaseViewHolder, data: String) {
        holder.setText(R.id.item_text_tv, data)
        holder.addClickListener(R.id.item_button_tv)
    }
}