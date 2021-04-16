package com.nathaniel.adapter.utility;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * item decoration for recycler view
 *
 * @author Nathaniel
 */
public final class ItemDecoration extends RecyclerView.ItemDecoration {

    public static final int LINEAR_LAYOUT_MANAGER = 0;
    public static final int GRID_LAYOUT_MANAGER = 1;
    public static final int STAGGERED_GRID_LAYOUT = 2;
    public static final int FLEX_LAYOUT_MANAGER = 3;
    private final int headItemCount;
    @LayoutManager
    private final int layoutManager;
    private int leftRight;
    private int topBottom;
    private int space;
    private boolean includeEdge;
    private int spanCount;
    private GridLayoutManager gridLayoutManager;

    /**
     * GridLayoutManager or StaggeredGridLayoutManager spacing
     *
     * @param leftRight     leftRight
     * @param topBottom     topBottom
     * @param headItemCount headItemCount
     * @param layoutManager layoutManager
     */
    public ItemDecoration(int leftRight, int topBottom, int headItemCount, @LayoutManager int layoutManager) {
        this.leftRight = leftRight;
        this.topBottom = topBottom;
        this.headItemCount = headItemCount;
        this.layoutManager = layoutManager;
    }

    /**
     * GridLayoutManager or StaggeredGridLayoutManager spacing
     *
     * @param space         space
     * @param includeEdge   includeEdge
     * @param layoutManager layoutManager
     */
    public ItemDecoration(int space, boolean includeEdge, @LayoutManager int layoutManager) {
        this(space, 0, includeEdge, layoutManager);
    }

    /**
     * GridLayoutManager or StaggeredGridLayoutManager spacing
     *
     * @param space         space
     * @param headItemCount headItemCount
     * @param includeEdge   includeEdge
     * @param layoutManager layoutManager
     */
    public ItemDecoration(int space, int headItemCount, boolean includeEdge, @LayoutManager int layoutManager) {
        this.space = space;
        this.headItemCount = headItemCount;
        this.includeEdge = includeEdge;
        this.layoutManager = layoutManager;
    }

    /**
     * GridLayoutManager or StaggeredGridLayoutManager spacing
     *
     * @param space         space
     * @param headItemCount headItemCount
     * @param layoutManager layoutManager
     */
    public ItemDecoration(int space, int headItemCount, @LayoutManager int layoutManager) {
        this(space, headItemCount, true, layoutManager);
    }

    /**
     * LinearLayoutManager or GridLayoutManager or StaggeredGridLayoutManager spacing
     *
     * @param space         space
     * @param layoutManager layoutManager
     */
    public ItemDecoration(int space, @LayoutManager int layoutManager) {
        this(space, 0, true, layoutManager);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(canvas, parent, state);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        switch (layoutManager) {
            case LINEAR_LAYOUT_MANAGER:
                setLinearLayoutSpaceItemDecoration(outRect, view, parent, state);
                break;
            case GRID_LAYOUT_MANAGER:
                GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
                //列数
                if (EmptyUtils.isEmpty(gridLayoutManager)) {
                    return;
                }
                spanCount = gridLayoutManager.getSpanCount();
                setNGridLayoutSpaceItemDecoration(outRect, view, parent, state);
                break;
            case STAGGERED_GRID_LAYOUT:
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) parent.getLayoutManager();
                //列数
                if (EmptyUtils.isEmpty(staggeredGridLayoutManager)) {
                    return;
                }
                spanCount = staggeredGridLayoutManager.getSpanCount();
                setNGridLayoutSpaceItemDecoration(outRect, view, parent, state);
                break;
            case FLEX_LAYOUT_MANAGER:
                setFlexLayoutSpaceItemDecoration(outRect, view, parent, state);
            default:
                break;
        }
    }

    /**
     * LinearLayoutManager spacing
     *
     * @param outRect outRect
     * @param view    view
     * @param parent  parent
     * @param state   state
     */
    private void setLinearLayoutSpaceItemDecoration(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = space;
        } else {
            outRect.top = 0;
        }
    }

    /**
     * GridLayoutManager or StaggeredGridLayoutManager spacing
     *
     * @param outRect outRect
     * @param view    view
     * @param parent  parent
     * @param state   state
     */
    private void setNGridLayoutSpaceItemDecoration(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view) - headItemCount;
        if (headItemCount != 0 && position == -headItemCount) {
            return;
        }
        int column = position % spanCount;
        if (includeEdge) {
            outRect.left = space - column * space / spanCount;
            outRect.right = (column + 1) * space / spanCount;
            if (position < spanCount) {
                outRect.top = space;
            }
            outRect.bottom = space;
        } else {
            outRect.left = column * space / spanCount;
            outRect.right = space - (column + 1) * space / spanCount;
            if (position >= spanCount) {
                outRect.top = space;
            }
        }

    }

    public void setFlexLayoutSpaceItemDecoration(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = space;
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
    }

    /**
     * GridLayoutManager设置间距（此方法最左边和最右边间距为设置的一半）
     *
     * @param outRect outRect
     * @param view    view
     * @param parent  parent
     * @param state   state
     */
    private void setGridLayoutSpaceItemDecoration(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //判断总的数量是否可以整除
        gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
        if (EmptyUtils.isEmpty(gridLayoutManager)) {
            return;
        }
        int totalCount = gridLayoutManager.getItemCount();
        int surplusCount = totalCount % gridLayoutManager.getSpanCount();
        int childPosition = parent.getChildAdapterPosition(view);
        //竖直方向的
        if (gridLayoutManager.getOrientation() == RecyclerView.VERTICAL) {
            if (surplusCount == 0 && childPosition > totalCount - gridLayoutManager.getSpanCount() - 1) {
                //后面几项需要bottom
                outRect.bottom = topBottom;
            } else if (surplusCount != 0 && childPosition > totalCount - surplusCount - 1) {
                outRect.bottom = topBottom;
            }
            //被整除的需要右边
            if ((childPosition + 1 - headItemCount) % gridLayoutManager.getSpanCount() == 0) {
                //加了右边后最后一列的图就非宽度少一个右边距
                //outRect.right = leftRight;
            }
            outRect.top = topBottom;
            outRect.left = leftRight / 2;
            outRect.right = leftRight / 2;
        } else {
            if (surplusCount == 0 && childPosition > totalCount - gridLayoutManager.getSpanCount() - 1) {
                //后面几项需要右边
                outRect.right = leftRight;
            } else if (surplusCount != 0 && childPosition > totalCount - surplusCount - 1) {
                outRect.right = leftRight;
            }
            //被整除的需要下边
            if ((childPosition + 1) % gridLayoutManager.getSpanCount() == 0) {
                outRect.bottom = topBottom;
            }
            outRect.top = topBottom;
            outRect.left = leftRight;
        }
    }

    public GridLayoutManager getGridLayoutManager() {
        return gridLayoutManager;
    }

    @IntDef({
        LINEAR_LAYOUT_MANAGER,
        GRID_LAYOUT_MANAGER,
        STAGGERED_GRID_LAYOUT,
        FLEX_LAYOUT_MANAGER
    })

    @Retention(RetentionPolicy.SOURCE)
    public @interface LayoutManager {
        int type() default LINEAR_LAYOUT_MANAGER;
    }
}