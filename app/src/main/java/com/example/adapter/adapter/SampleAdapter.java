package com.example.adapter.adapter;

import com.example.adapter.R;

import java.util.List;

/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.example.adapterapplication.adapter
 * @datetime 2020/4/19 - 17:26
 */
public class SampleAdapter extends BaseRecyclerAdapter<String> {
    public SampleAdapter(int layoutId, List<String> dataList) {
        super(layoutId, dataList);
    }

    @Override
    public void bindDataToView(BaseViewHolder holder, String data) {
        holder.setText(R.id.item_text_tv, data);

        holder.addClickListener(R.id.item_button_tv);
    }
}
