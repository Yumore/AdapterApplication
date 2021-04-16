package com.nathaniel.sample;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;

import androidx.multidex.MultiDex;

import java.io.File;

/**
 * @author nathaniel
 */
public class LoadMultiDexActivity extends Activity {
    private static final String TAG = "LoadMultiDexActivity";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Thread thread = new Thread() {
            @Override
            public void run() {
                loadMultiDex();
            }
        };
        thread.setName("multi_dex");
        thread.start();
        showLoadingDialog();
    }

    private void loadMultiDex() {
        Log.d(TAG, "MultiDex.install 开始: ");
        long startTime = System.currentTimeMillis();
        MultiDex.install(LoadMultiDexActivity.this);
        try {
            //模拟MultiDex耗时操作
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "MultiDex.install 结束，耗时: " + (System.currentTimeMillis() - startTime));
        afterMultiDex();
    }

    private void afterMultiDex() {
        deleteTempFile(this);
        //将这个进程杀死
        Log.d(TAG, "afterMultiDex: ");
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = null;
        }
        finish();
        Process.killProcess(Process.myPid());
    }

    private void deleteTempFile(Context context) {
        try {
            File file = new File(context.getCacheDir().getAbsolutePath(), "load_dex.tmp");
            if (file.exists()) {
                boolean flags = file.delete();
                Log.d(TAG, "deleteTempFile: " + flags);
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private void showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        progressDialog.setMessage("加载中 ...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            progressDialog = null;
        }
    }
}
