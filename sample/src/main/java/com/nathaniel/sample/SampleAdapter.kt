package com.nathaniel.sample

import com.nathaniel.adapter.adapter.BaseRecyclerAdapter
import com.nathaniel.adapter.adapter.BaseViewHolder

/**
 * @author Nathaniel
 */
class SampleAdapter(layoutId: Int, dataList: MutableList<String?>?) : BaseRecyclerAdapter<String?>(layoutId, dataList) {
    override fun bindDataToView(viewHolder: BaseViewHolder, data: String?) {
        viewHolder.setText(R.id.item_text_tv, data)
        viewHolder.addClickListener(R.id.item_button_tv)
    }
}