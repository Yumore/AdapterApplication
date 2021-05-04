package com.nathaniel.statiistic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.nathaniel.statistics.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Nathaniel
 */
public class AboutActivity extends AppCompatActivity {

    public SharedPreferences defaultPreferences;
    public SharedPreferences.Editor editor;
    public SharedPreferences sharedPreferences;
    private Toolbar toolbar;
    private ListView listviewOpensource;
    private ListView listviewUpdatelog;
    private TextView textViewCurVersion;
    private RecyclerView recyclerview;
    private List<String> mdata;

    public static boolean isXIAOMI() {
        String device = Build.MANUFACTURER;
        System.out.println("Build.MANUFACTURER = " + device);
        if (device.equals("Xiaomi")) {
            System.out.println("this is a xiaomi device");
            return true;
        } else {
            return false;
        }
    }

    public static String myDevice() {
        return Build.MANUFACTURER;
    }

    protected void initData() {
        mdata = new ArrayList<>();
        for (int i = 'A'; i < 'z'; i++) {
            mdata.add(String.valueOf((char) i));
        }
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initData();
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        Button icon = (Button) findViewById(R.id.icon_about);
        icon.setOnClickListener(v -> {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo activeInfo = manager.getActiveNetworkInfo();
            Snackbar.make(getWindow().getDecorView(), myDevice() + ":" + activeInfo.getTypeName() + "", Snackbar.LENGTH_SHORT).show();
            if (defaultPreferences.getBoolean("log", false)) {
                AlertDialog.Builder alertDialogLog = new AlertDialog.Builder(AboutActivity.this);
                String logger = new FileManager().readLogFile(AboutActivity.this, "log");
                alertDialogLog.setMessage(logger);
                alertDialogLog.setMessage(logger);
                alertDialogLog.setPositiveButton(getString(R.string.ok), null);
                alertDialogLog.show();
                Log.d("qiang", "logtest2");
            }
        });
        /*
        listviewOpensource = (ListView) findViewById(R.id.opensourceproject);   //组织数据源
        ArrayAdapter<CharSequence> listviewOpensourceAA = ArrayAdapter.createFromResource(this, R.array.opensourceproject, R.layout.opensource_item);
        listviewOpensource.setAdapter(listviewOpensourceAA);

        listviewUpdatelog = (ListView) findViewById(R.id.updatelog);   //组织数据源
        ArrayAdapter<CharSequence> listviewUpdatelogAA = ArrayAdapter.createFromResource(this, R.array.updatelog, R.layout.opensource_item);
        listviewUpdatelog.setAdapter(listviewUpdatelogAA);

*/
        textViewCurVersion = (TextView) findViewById(R.id.curversion);
        textViewCurVersion.setText("当前版本:" + getAppVersion(getApplicationContext()));

        //recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
//        recyclerview.setLayoutManager(new GridLayoutManager(this, 3));
//        recyclerview.setLayoutManager(new LinearLayoutManager(this,
//                LinearLayoutManager.VERTICAL, false));
        //recyclerview.setLayoutManager(new LinearLayoutManager(this));
        //recyclerview.setItemAnimator(new DefaultItemAnimator()); //即使不设置,默认也是这个动画
        //recyclerview.setAdapter(new recyclerAdapter());

    }

    private String getAppVersion(Context context) {
        PackageManager manager;
        PackageInfo packageInfo = null;
        manager = context.getPackageManager();
        try {
            packageInfo = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert packageInfo != null;
        return packageInfo.versionName;
    }

    class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {
        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(AboutActivity.this).inflate(R.layout.recycler_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tv.setText(mdata.get(position));
        }

        @Override
        public int getItemCount() {
            return mdata.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            Button icon;
            TextView tv;

            MyViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.recyclerviewitem);
                icon = (Button) findViewById(R.id.icon);
            }
        }
    }

}
