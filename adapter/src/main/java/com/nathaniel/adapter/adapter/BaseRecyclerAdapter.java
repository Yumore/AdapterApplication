package com.nathaniel.adapter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.nathaniel.adapter.utility.LoggerUtils;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * @author nathaniel
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = BaseRecyclerAdapter.class.getSimpleName();
    private static final int HEADER_VIEW = 0x00000111;
    private static final int FOOTER_VIEW = 0x00000333;
    private static final int EMPTY_VIEW = 0x00000555;
    @LayoutRes
    private final int layoutId;
    private List<T> dataList;
    private OnItemChildClickListener onItemChildClickListener;
    private OnItemClickListener onItemClickListener;
    private Context context;
    private LinearLayout headerLayout;
    private LinearLayout footerLayout;
    private FrameLayout emptyLayout;
    private BasePassageView passageView;
    private BaseEmptyView emptyView;

    /**
     * init data and layout
     *
     * @param dataList dataList
     * @param layoutId layoutId
     */
    public BaseRecyclerAdapter(@LayoutRes int layoutId, List<T> dataList) {
        this.layoutId = layoutId;
        this.dataList = dataList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * bind data to view
     *
     * @param viewHolder viewHolder
     * @param data       data
     */
    public abstract void bindDataToView(BaseViewHolder viewHolder, T data);

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView;
        BaseViewHolder viewHolder;
        switch (viewType) {
            case EMPTY_VIEW:
                viewHolder = new BaseViewHolder(emptyLayout);
                break;
            case HEADER_VIEW:
                viewHolder = new BaseViewHolder(headerLayout);
                break;
            case FOOTER_VIEW:
                viewHolder = new BaseViewHolder(footerLayout);
                break;
            default:
                itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
                BaseViewHolder baseViewHolder = new BaseViewHolder(itemView);
                addViewListener(itemView, baseViewHolder);
                baseViewHolder.setAdapter(this);
                viewHolder = baseViewHolder;
                break;
        }
        return viewHolder;
    }

    private void addViewListener(View itemView, final BaseViewHolder viewHolder) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoggerUtils.logger(TAG, "real position: " + (viewHolder.getAdapterPosition() - getHeaderCount()) + ", view holder position: " + viewHolder.getAdapterPosition());
                onItemClickListener.onItemClick(BaseRecyclerAdapter.this, view, viewHolder.getAdapterPosition() - getHeaderCount());
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder viewHolder, int position) {
        LoggerUtils.logger(TAG, "adapter position = " + position + ", view holder position = " + (viewHolder.getAdapterPosition() - getHeaderCount()) + ", list size = " + getDataSize());
        if (isHeaderView(position) || isEmptyView(position) || isFooterView(position)) {
            LoggerUtils.logger(TAG, "item is header or footer or empty");
            return;
        }
        if (viewHolder.getAdapterPosition() - getHeaderCount() >= getDataSize()) {
            return;
        }
        bindDataToView(viewHolder, dataList.get(viewHolder.getAdapterPosition() - getHeaderCount()));
    }

    @Override
    public int getItemViewType(int position) {
        if (isEmptyView(position)) {
            return EMPTY_VIEW;
        } else if (isHeaderView(position)) {
            return HEADER_VIEW;
        } else if (isFooterView(position)) {
            return FOOTER_VIEW;
        } else {
            return super.getItemViewType(position);
        }
    }

    private boolean isFooterView(int position) {
        return position >= getHeaderCount() + getDataSize() + getEmptyCount() && position < getHeaderCount() + getDataSize() + getEmptyCount() + getFooterCount();
    }

    private boolean isHeaderView(int position) {
        return position < getHeaderCount();
    }

    private boolean isEmptyView(int position) {
        return getDataSize() == 0 && getEmptyCount() > 0 && position == getHeaderCount() + getEmptyCount() - 1;
    }

    @Override
    public int getItemCount() {
        return getHeaderCount() + getDataSize() + getEmptyCount() + getFooterCount();
    }

    public List<T> getDataList() {
        return dataList;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void addDataList(List<T> dataList) {
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    public Context getContext() {
        return context;
    }

    public void addHeaderView(View headerView) {
        addHeaderView(headerView, 0, LinearLayout.INVISIBLE);
    }

    public void addHeaderView(View headerView, int position) {
        addHeaderView(headerView, position, LinearLayout.VERTICAL);
    }

    /**
     * 添加header
     *
     * @param headerView  headerView
     * @param position    position
     * @param orientation orientation
     */
    public void addHeaderView(@NonNull View headerView, int position, int orientation) {
        if (headerLayout == null) {
            headerLayout = new LinearLayout(headerView.getContext());
            if (orientation == LinearLayout.VERTICAL) {
                headerLayout.setOrientation(LinearLayout.VERTICAL);
                headerLayout.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            } else {
                headerLayout.setOrientation(LinearLayout.HORIZONTAL);
                headerView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
            }
        }
        if (position < 0 || position > getHeaderCount()) {
            position = getHeaderCount();
        }
        if (position == getHeaderCount() && headerLayout.getChildAt(position) != null) {
            headerLayout.removeViewAt(position);
        }
        ViewGroup parentViewGroup = (ViewGroup) headerView.getParent();
        if (parentViewGroup != null) {
            parentViewGroup.removeView(headerView);
        }
        headerLayout.addView(headerView, position);
        notifyItemInserted(position);
        notifyDataSetChanged();
    }

    public void addFooterView(View footerView) {
        addFooterView(footerView, 0, LinearLayout.VERTICAL);
    }

    public void addFooterView(View footerView, int position) {
        addFooterView(footerView, position, LinearLayout.VERTICAL);
    }

    public void removeFooterView(View footerView) {
        if (getFooterCount() <= 0 || footerView == null) {
            return;
        }
        footerLayout.removeView(footerView);
    }

    /**
     * 添加footer
     *
     * @param footerView  footerView
     * @param position    position
     * @param orientation orientation
     */
    public void addFooterView(@NonNull View footerView, int position, int orientation) {
        if (footerLayout == null) {
            footerLayout = new LinearLayout(footerView.getContext());
            if (orientation == LinearLayout.VERTICAL) {
                footerLayout.setOrientation(LinearLayout.VERTICAL);
                footerLayout.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
            } else {
                footerLayout.setOrientation(LinearLayout.HORIZONTAL);
                footerView.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
            }
        }
        if (position < 0 || position > getHeaderCount() + getDataSize() + getEmptyCount() + getFooterCount()) {
            position = getHeaderCount() + getDataSize() + getEmptyCount() + getFooterCount();
        }
        if (position == getFooterCount() && footerLayout.getChildAt(position) != null) {
            footerLayout.removeViewAt(position);
        }
        ViewGroup parentViewGroup = (ViewGroup) footerView.getParent();
        if (parentViewGroup != null) {
            parentViewGroup.removeView(footerView);
        }
        footerLayout.addView(footerView, position);
        notifyItemInserted(position);
        notifyDataSetChanged();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            final GridLayoutManager.SpanSizeLookup spanSizeLookup = gridLayoutManager.getSpanSizeLookup();
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isHeaderView(position) || isFooterView(position) || isEmptyView(position)) {
                        return gridLayoutManager.getSpanCount();
                    } else {
                        if (spanSizeLookup != null) {
                            return spanSizeLookup.getSpanSize(position - getHeaderCount());
                        }
                        return 1;
                    }
                }
            });
        } else {
            super.onAttachedToRecyclerView(recyclerView);
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BaseViewHolder viewHolder) {
        int position = viewHolder.getLayoutPosition();
        if (isHeaderView(position) || isFooterView(position) || isEmptyView(position)) {
            ViewGroup.LayoutParams layoutParams = viewHolder.getItemView().getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams layoutParams1 = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                layoutParams1.setFullSpan(true);
            }
        } else {
            super.onViewAttachedToWindow(viewHolder);
        }
    }


    public void setEmptyMessage(Context context, CharSequence message) {
        BaseEmptyView emptyView = getEmptyView(context);
        emptyView.setEmptyMessage(message);
        setEmptyView(emptyView);
    }

    private BaseEmptyView getEmptyView(Context context) {
        if (emptyView == null) {
            emptyView = new DefaultEmptyView(context);
        }
        return emptyView;
    }

    public void setEmptyView(View emptyView) {
        if (emptyLayout == null) {
            emptyLayout = new FrameLayout(emptyView.getContext());
            final FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            final ViewGroup.LayoutParams viewLayoutParams = emptyView.getLayoutParams();
            if (viewLayoutParams != null) {
                frameLayoutParams.width = viewLayoutParams.width;
                frameLayoutParams.height = viewLayoutParams.height;
            }
            emptyLayout.setLayoutParams(frameLayoutParams);
        }
        emptyLayout.removeAllViews();
        emptyLayout.addView(emptyView);
        notifyDataSetChanged();
    }

    private BasePassageView getPassageView(@NonNull Context context) {
        if (passageView == null) {
            passageView = new DefaultPassageView(context);
        }
        return passageView;
    }

    public void setPassageEnable(@NonNull Context context, boolean passageEnable) {
        if (passageEnable) {
            if (getPassageView(context) != null) {
                removeFooterView(getPassageView(context));
            }
            getPassageView(context).setLoadingStatus(BasePassageView.STATUS_LOADING);
            addFooterView(getPassageView(context));
        } else {
            removeFooterView(getPassageView(context));
        }
    }

    public void setWithoutMore(@NonNull Context context) {
        getPassageView(context).setLoadingStatus(BasePassageView.STATUS_WITHOUT);
    }

    public OnItemChildClickListener getOnItemChildClickListener() {
        return onItemChildClickListener;
    }

    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        this.onItemChildClickListener = onItemChildClickListener;
    }

    public int getHeaderCount() {
        return headerLayout == null ? 0 : headerLayout.getChildCount();
    }

    public int getFooterCount() {
        return footerLayout == null ? 0 : footerLayout.getChildCount();
    }

    public int getDataSize() {
        return dataList == null ? 0 : dataList.size();
    }

    private int getEmptyCount() {
        return emptyLayout == null ? 0 : emptyLayout.getChildCount();
    }
}