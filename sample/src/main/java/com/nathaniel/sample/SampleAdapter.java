package com.nathaniel.sample;

import com.nathaniel.adapter.adapter.BaseRecyclerAdapter;
import com.nathaniel.adapter.adapter.BaseViewHolder;

import java.util.List;

/**
 * @author Nathaniel
 */
public class SampleAdapter extends BaseRecyclerAdapter<String> {
    public SampleAdapter(int layoutId, List<String> dataList) {
        super(layoutId, dataList);
    }

    @Override
    public void bindDataToView(BaseViewHolder holder, String data) {
        holder.setText(com.nathaniel.sample.R.id.item_text_tv, data);

        holder.addClickListener(R.id.item_button_tv);
    }
}
