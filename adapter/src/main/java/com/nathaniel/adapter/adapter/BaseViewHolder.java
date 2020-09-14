package com.nathaniel.adapter.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedHashSet;

/**
 * if you wanna define a method to set other attributes to view
 * please make sure that you called {@link #getView(int viewId)}
 * how to use  {@link #setText(int viewId, CharSequence text)}
 *
 * @author nathaniel
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private LinkedHashSet<Integer> childClickIdSet;
    private BaseRecyclerAdapter baseRecyclerAdapter;
    private SparseArray<View> viewSparseArray;

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
        childClickIdSet = new LinkedHashSet<>();
    }

    public static BaseViewHolder getViewHolder(View itemView) {
        return new BaseViewHolder(itemView);
    }

    public static BaseViewHolder getViewHolder(ViewGroup parent, @LayoutRes int layoutId) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new BaseViewHolder(itemView);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T getView(@IdRes int viewId) {
        if (viewSparseArray == null) {
            viewSparseArray = new SparseArray<>();
        }

        View view = viewSparseArray.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            viewSparseArray.put(viewId, view);
        }
        return (T) view;
    }


    public void setText(@IdRes int viewId, CharSequence text) {
        if (getView(viewId) instanceof TextView) {
            TextView textView = getView(viewId);
            textView.setText(text);
        } else {
            throw new IllegalArgumentException("error view type");
        }
    }

    public void setText(@IdRes int viewId, int resId) {
        if (getView(viewId) instanceof TextView) {
            TextView textView = getView(viewId);
            textView.setText(resId);
        } else {
            throw new IllegalArgumentException("error view type");
        }
    }

    public void setTextColor(@IdRes int viewId, int color) {
        if (getView(viewId) instanceof TextView) {
            TextView textView = getView(viewId);
            textView.setTextColor(color);
        } else {
            throw new IllegalArgumentException("error view type");
        }
    }

    public void setTextColor(@IdRes int viewId, String color) {
        if (getView(viewId) instanceof TextView) {
            TextView textView = getView(viewId);
            textView.setTextColor(Color.parseColor(color));
        } else {
            throw new IllegalArgumentException("error view type");
        }
    }

    public void setImageResource(@IdRes int viewId, int resId) {
        if (getView(viewId) instanceof ImageView) {
            ImageView imageView = getView(viewId);
            imageView.setImageResource(resId);
        } else {
            throw new IllegalArgumentException("error view type");
        }
    }

    public void setBackgroundResource(@IdRes int viewId, int resId) {
        getView(viewId).setBackgroundResource(resId);
    }

    public void setBackground(@IdRes int viewId, Drawable drawable) {
        getView(viewId).setBackground(drawable);
    }

    public void setBackgroundColor(@IdRes int viewId, int color) {
        getView(viewId).setBackgroundColor(color);
    }

    public void setVisibility(@IdRes int viewId, int visibility) {
        getView(viewId).setVisibility(visibility);
    }

    public void setImageBitmap(@IdRes int viewId, Bitmap bitmap) {
        if (getView(viewId) instanceof ImageView) {
            ImageView imageView = getView(viewId);
            imageView.setImageBitmap(bitmap);
        } else {
            throw new IllegalArgumentException("error view type");
        }
    }

    public void addClickListener(@IdRes int viewId) {
        childClickIdSet.add(viewId);
        getView(viewId).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (baseRecyclerAdapter.getOnItemChildClickListener() != null) {
                    baseRecyclerAdapter.getOnItemChildClickListener().onItemChildClick(baseRecyclerAdapter, view, getAdapterPosition());
                }
            }
        });
    }

    public void setTextLinking(@IdRes int viewId) {
        TextView textView = getView(viewId);
        if (textView != null) {
            Linkify.addLinks(textView, Linkify.ALL);
        }
    }

    public void setTextTypeface(Typeface typeface, int... viewIds) {
        for (int viewId : viewIds) {
            TextView textView = getView(viewId);
            if (textView != null) {
                textView.setTypeface(typeface);
                textView.setPaintFlags(textView.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
            }
        }
    }


    public View getItemView() {
        return itemView;
    }

    public void setAdapter(@NonNull BaseRecyclerAdapter baseRecyclerAdapter) {
        this.baseRecyclerAdapter = baseRecyclerAdapter;
    }

    public void setAdapter(@IdRes int viewId, @NonNull BaseRecyclerAdapter baseRecyclerAdapter) {
        if (getView(viewId) instanceof RecyclerView) {
            RecyclerView recyclerView = getView(viewId);
            recyclerView.setAdapter(baseRecyclerAdapter);
        } else {
            throw new IllegalArgumentException("error view type");
        }
    }
}
