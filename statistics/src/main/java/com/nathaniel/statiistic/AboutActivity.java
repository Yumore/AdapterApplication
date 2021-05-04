package com.nathaniel.statiistic;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.nathaniel.statistics.R;

import java.util.ArrayList;
import java.util.List;

public class AboutActivity extends AppCompatActivity {

    public SharedPreferences pref_default;
    public SharedPreferences.Editor editor;
    public SharedPreferences pref;
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
        mdata = new ArrayList<String>();
        for (int i = 'A'; i < 'z'; i++) {
            //mdata.add("" + (char) i);
            mdata.add("");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initData();
        pref_default = PreferenceManager.getDefaultSharedPreferences(this);
        editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        pref = getSharedPreferences("data", MODE_PRIVATE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button icon = (Button) findViewById(R.id.icon_about);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo activeInfo = manager.getActiveNetworkInfo();
                Snackbar.make(getWindow().getDecorView(), myDevice() + ":" + activeInfo.getTypeName() + "", Snackbar.LENGTH_SHORT).show();

                //Log.d("qiang","logtest1");
                if (pref_default.getBoolean("log", false)) {
                    //log
                    AlertDialog.Builder alertDialogLog = new AlertDialog.Builder(AboutActivity.this);
                    String logstr = new FileManager().readLogFile(AboutActivity.this, "log");
                    alertDialogLog.setMessage(logstr);
                    alertDialogLog.setMessage(logstr);
                    alertDialogLog.setPositiveButton(getString(R.string.ok), null);
                    alertDialogLog.show();

                    Log.d("qiang", "logtest2");
                }
                //getWindow().getDecorView()
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
        textViewCurVersion.setText("当前版本:" + getAPPVersion());

        //recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
//        recyclerview.setLayoutManager(new GridLayoutManager(this, 3));
//        recyclerview.setLayoutManager(new LinearLayoutManager(this,
//                LinearLayoutManager.VERTICAL, false));
        //recyclerview.setLayoutManager(new LinearLayoutManager(this));
        //recyclerview.setItemAnimator(new DefaultItemAnimator()); //即使不设置,默认也是这个动画
        //recyclerview.setAdapter(new recyclerAdapter());

    }

    private String getAPPVersion() {
        PackageManager manager;
        PackageInfo info = null;
        manager = this.getPackageManager();
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        assert info != null;
        return info.versionName;

    }

    class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.MyViewHolder> {
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                AboutActivity.this).inflate(R.layout.recycler_item, parent,
                false));
            return holder;

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
