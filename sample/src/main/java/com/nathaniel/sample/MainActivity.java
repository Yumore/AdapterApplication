package com.nathaniel.sample;

import android.content.Intent;

import com.nathaniel.baseui.AbstractActivity;
import com.nathaniel.sample.binding.DemoBindingActivity;
import com.nathaniel.sample.databinding.ActivityMainBinding;

import eu.faircode.netguard.surface.ActivityMain;

/**
 * @author nathaniel
 */
public class MainActivity extends AbstractActivity<ActivityMainBinding> {

    @Override
    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    public void loadData() {

    }

    @Override
    public void bindView() {
        binding.btnRecyclerView.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RecyclerViewActivity.class)));

        binding.btnListView.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ListViewActivity.class)));

        binding.btnNestedScrollView.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NestedScrollViewActivity.class)));

        binding.btnScrollView.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ScrollViewActivity.class)));

        binding.btnWebView.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, WebViewActivity.class)));

        binding.btnItemClick.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SampleActivity.class)));

        binding.btnViewBinding.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DemoBindingActivity.class)));

        binding.btnSameId.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, IncludeActivity.class)));

        binding.btnPackage.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, PackageActivity.class)));

        binding.btnNetGuard.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, ActivityMain.class)));
    }
}
