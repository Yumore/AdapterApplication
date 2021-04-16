package com.nathaniel.adapter.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

public class BaseViewBindingHolder<VB extends ViewBinding> extends RecyclerView.ViewHolder {
    private VB binding;
    private BaseBindingAdapter<?, VB> baseRecyclerAdapter;

    public BaseViewBindingHolder(@NonNull VB binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public BaseViewBindingHolder(@NonNull View itemView) {
        super(itemView);
    }

    public VB getBinding() {
        return binding;
    }

    public void addClickListener(View view) {
        view.setOnClickListener(view1 -> {
            if (baseRecyclerAdapter.getOnItemChildClickListener() != null) {
                baseRecyclerAdapter.getOnItemChildClickListener().onItemChildClick(baseRecyclerAdapter, view1, getAdapterPosition());
            }
        });
    }


    public View getItemView() {
        return itemView;
    }

    public void setAdapter(@NonNull BaseBindingAdapter<?, VB> baseRecyclerAdapter) {
        this.baseRecyclerAdapter = baseRecyclerAdapter;
    }

    public void setAdapter(View view, @NonNull BaseBindingAdapter<?, VB> baseRecyclerAdapter) {
        if (view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setAdapter(baseRecyclerAdapter);
        } else {
            throw new IllegalArgumentException("error view type");
        }
    }
}
