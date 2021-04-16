package com.nathaniel.sample.binding;

import com.nathaniel.adapter.adapter.BaseRecyclerAdapter;
import com.nathaniel.adapter.adapter.BaseViewHolder;
import com.nathaniel.adapter.utility.EmptyUtils;
import com.nathaniel.sample.R;

import java.util.List;

/**
 * @author Nathaniel
 * @version V1.0.0
 * @package com.hjq.demo.ui.binding
 * @datetime 2021/3/31 - 20:49
 */
public class SimpleAdapter extends BaseRecyclerAdapter<String> {
    /**
     * init data and layout
     *
     * @param layoutId layoutId
     * @param dataList dataList
     */
    public SimpleAdapter(int layoutId, List<String> dataList) {
        super(layoutId, dataList);
    }


    @Override
    public void bindDataToView(BaseViewHolder viewHolder, String data) {
        if (EmptyUtils.isEmpty(data)) {
            return;
        }
        viewHolder.setText(R.id.item_text_tv, data);
    }
}
