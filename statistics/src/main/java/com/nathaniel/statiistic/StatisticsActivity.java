package com.nathaniel.statiistic;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.nathaniel.statistics.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;


/**
 * @author Nathaniel
 */
public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener, SMSBroadcastReceiver.Interaction {
    private static final int REQUEST_CODE = 1;
    static String TAG = "qiang";
    /**
     * 添加数据
     */

    static int LineChartEachMonthNums = 31;
    static int LineChartEachDayNums = 24;
    public AudioManager audio;
    public int mode;
    public CoordinatorLayout coordinatorLayout;
    public Button eachmonth;
    public Button eachday;
    public Toolbar toolbar;
    public TextView textView;
    //private static final int RECEIVE_SMS_REQUEST_CODE = 2;
    // public static int FLAG = 1;
    public FloatingActionButton fab;
    public SharedPreferences pref_default;
    public SharedPreferences.Editor editor;
    public SharedPreferences pref;
    //private final static String[] mLabels = {"ANT", "GNU", "OWL", "APE", "COD", "YAK", "RAM", "JAY"};
    int numberOfLines = 1;
    int maxNumberOfLines = 4;
    boolean isCubic = false;
    LineChartView lineChart;
    LineChartData chartData;
    //Snackbar.make(getWindow().getDecorView(),,Snackbar.LENGTH_SHORT).show();
    private int volumn = 0;
    private ProgressDialog progressDialog;

    /**
     * 检查手机是否是xiaomi
     *
     * @return true/false
     * @href http://dev.xiaomi.com/doc/p=254/index.html
     */
    public static boolean isMIUI() {
        String device = Build.MANUFACTURER;
        System.out.println("Build.MANUFACTURER = " + device);
        if (device.equals("Xiaomi")) {
            System.out.println("this is a xiaomi device");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 跳转到应用权限设置页面
     *
     * @param context 传入app 或者 activity
     *                context，通过context获取应用packegename，之后通过packegename跳转制定应用
     * @return 是否是miui
     */
    public static boolean gotoPermissionSettings(Context context) {
        boolean mark = isMIUI();
        if (mark) {
            // 只兼容miui v5/v6 的应用权限设置页面，否则的话跳转应用设置页面（权限设置上一级页面）
            try {
                Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                localIntent.putExtra("extra_pkgname", context.getPackageName());
                context.startActivity(localIntent);
            } catch (ActivityNotFoundException e) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
            }
        }
        return mark;
    }

    @Override
    protected void onResume() {
        super.onResume();
        long curmonthflow = pref.getLong("curmonthflow", 0);
        long all_liuliang = pref.getLong("all_liuliang", 0);
        long remain_liuliang = pref.getLong("remain_liuliang", 0);
        long curdayflow = (pref.getBoolean("reboot", false)) ? pref.getLong("curdayflow", 0) : 0;
        long lastmonthflow = pref.getLong("lastmonthflow", 0);
        long curfreetimeflow = pref.getLong("curfreetimeflow", 0);
        long curfreefront = pref.getLong("curfreefront", 0);
        long curfreebehind = pref.getLong("curfreebehind", 0);

        String textstr;
        //本月可用流量(含闲时)=总流量 本月还剩流量(含闲时)=总流量去掉不可用后剩余的 本月还剩流量(不含闲时)=本月还剩流量(含闲时)-闲时流量

        if (pref_default.getBoolean("free", false)) {
            long allfreetimeflow = new Formatdata().getNumFromString(pref_default.getString("freeflow", "0") + "M");
            textstr = "本月可用流量(含闲时)：" + new Formatdata().long2string(remain_liuliang)
                + "\n本月已用流量(含闲时)：" + new Formatdata().long2string(curmonthflow)
                + "\n本月还剩流量(含闲时)：" + ((pref.getBoolean("sent", false)) ? new Formatdata().long2string(remain_liuliang - curmonthflow - curdayflow) : "0K")
                + "\n本月可用闲时流量：" + new Formatdata().long2string(allfreetimeflow)
                + "\n今日闲时使用流量：" + new Formatdata().long2string(curfreetimeflow)
                + "\n本月还剩流量(不含闲时)：" + new Formatdata().long2string(remain_liuliang - curmonthflow - curdayflow - allfreetimeflow)
                + "\n今日使用流量(不含闲时)：" + new Formatdata().long2string(curdayflow - curfreebehind - curfreefront)
                + "\n上个月使用流量：" + new Formatdata().long2string(lastmonthflow);
        } else {
            textstr = "本月可用流量：" + new Formatdata().long2string(all_liuliang)
                + "\n本月已用流量：" + new Formatdata().long2string(curmonthflow)
                + "\n本月还剩流量：" + ((pref.getBoolean("sent", false)) ? new Formatdata().long2string(remain_liuliang - curmonthflow - curdayflow) : "0K")
                + "\n今日使用流量：" + new Formatdata().long2string(curdayflow)
                + "\n上个月使用流量：" + new Formatdata().long2string(lastmonthflow);
        }
        textView = (TextView) findViewById(R.id.main);
        textView.setText(textstr);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pref_default = PreferenceManager.getDefaultSharedPreferences(this);
        editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        pref = getSharedPreferences("data", MODE_PRIVATE);

        boolean isfirstrun = pref.getBoolean("isfirstrun", true);
        if (isfirstrun) {
            Calendar calendar = Calendar.getInstance();
            int curmonth = calendar.get(Calendar.MONTH);
            editor.putLong("firststartflow", new CalculateTodayFlow().calculate(this));//第一次开启前使用的流量
            editor.putInt("savemonth", curmonth);
            editor.putLong("thisbootflow", 0);//3
            editor.putLong("curdayflow", 0);//4
            editor.putLong("onedaylastbootflow", 0);//一日内上次开机使用的流量 5
            editor.putLong("onebootlastdayflow", 0);//一次开机前一天使用的流量 6
            editor.putLong("curmonthflow", 0);//7
            editor.putLong("lastmonthflow", 0);
            //editor.putLong("curfreetimeflow", 0);//当日闲时流量=curfreebehind+curfreefront
            editor.putLong("allfreetimeflow", 0);//闲时流量总量
            editor.putLong("curmonthfreeflow", 0);//当月使用闲时流量
            editor.putBoolean("isreboot", false);//是否重启过
            editor.commit();
            startService(new Intent(this, AlarmTimingService.class));
            startService(new Intent(this, AlarmFreeService.class));
            startService(new Intent(this, AlarmManualService.class));
            startActivity(new Intent(StatisticsActivity.this, SettingActivity.class));
            if (pref_default.getBoolean("isfirstrun", true)) {
                String tips_string = "请点击右下角按钮从运营商查询流量信息";
                AlertDialog.Builder tips = new AlertDialog.Builder(this);
                tips.setMessage(tips_string)
                    .setTitle("小提示")
                    .setPositiveButton("好的", (dialog, which) -> {
                        editor.putBoolean("isfirstrun", false);
                        editor.commit();

                    });
                tips.show();
            }
        }
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        lineChart = (LineChartView) findViewById(R.id.linechart);
        lineChart.setOnValueTouchListener(new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                //Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                //addLineToData();
                //resetViewport();
            }

            @Override
            public void onValueDeselected() {
            }
        });
        addLineChartDate(0);//本月
        /*
        <Button
            android:padding="10dp"
            android:id="@+id/eachday"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:background="@android:color/transparent"
            android:text="@string/eachday" />
        * */
        // eachday = (Button) findViewById(R.id.eachday);
        // eachday.setOnClickListener(this);
        eachmonth = (Button) findViewById(R.id.eachmonth);
        eachmonth.setOnClickListener(this);
    }

    private void addLineChartDate(int type) {
        Calendar calendar = Calendar.getInstance();
        int curday = calendar.get(Calendar.DAY_OF_MONTH);
        int numberOfPoints;
        ArrayList<AxisValue> axisValuesY = new ArrayList<AxisValue>();
        ArrayList<AxisValue> axisValuesX = new ArrayList<AxisValue>();
        List<Line> lines = new ArrayList<Line>();
        Axis axisY = new Axis();
        Axis axisX = new Axis();
        if (type == 0) {
            numberOfPoints = LineChartEachMonthNums;
        } else {
            numberOfPoints = LineChartEachDayNums;
        }
        for (int i = 0; i < numberOfLines; ++i) {
            List<PointValue> values = new ArrayList<PointValue>();
            if (type == 0) {
                values.clear();
                axisValuesY.clear();
                axisValuesX.clear();
                int j = 0;

                Log.d("MainActivity", "curday:" + curday);
                while (j < numberOfPoints) {
                    j++;
                    float yValue;
                    if (j == curday) {
                        long cur_boot_mobiletx = TrafficStats.getMobileTxBytes();
                        long cur_boot_mobilerx = TrafficStats.getMobileRxBytes();
                        long thisbootflow = cur_boot_mobilerx + cur_boot_mobiletx;
                        //yValue = (float)(new CalculateTodayFlow().calculate(this));
                        yValue = (new Formatdata().long2float(thisbootflow));
                        Log.d("MainActivity", "yValue:" + yValue);
                    } else {
                        yValue = new Formatdata().long2float(pref.getLong(j + "day", 0));
                    }
                    values.add(new PointValue(j, yValue));
                    Log.d(TAG, "pointvalue:" + (j) + "," + yValue);
                    //axisValuesY.add(new AxisValue(j * 10 * (i + 1)).setLabel(j + ""));//添加Y轴显示的刻度值
                    axisValuesX.add(new AxisValue(j).setLabel(j + "日"));//添加X轴显示的刻度值
                }
                //Calendar calendar = Calendar.getInstance();
                //int curday = calendar.get(Calendar.DAY_OF_MONTH);
                //values.set(curday,new PointValue(22,new Formatdata().longtofloat(pref.getLong("curdayflow",0))));
            } else {
                values.clear();
                axisValuesY.clear();
                axisValuesX.clear();
                for (int j = 0; j < numberOfPoints; ++j) {
                    values.add(new PointValue(j, j + 0.1f));
                    //axisValuesY.add(new AxisValue(j * 10 * (i + 1)).setLabel(j + ""));//添加Y轴显示的刻度值
                    axisValuesX.add(new AxisValue(j).setLabel(j + 1 + ":00"));//添加X轴显示的刻度值
                }
            }
            Line line = new Line(values);
            line.setColor(Color.parseColor("#b6ddfb"));//设置折线颜色
            line.setStrokeWidth(5);//设置折线宽度
            line.setFilled(false);//设置折线覆盖区域颜色
            //line.setCubic(isCubic);//节点之间的过渡
            line.setCubic(true);//曲线是否平滑，即是曲线还是折线
            line.setPointColor(Color.parseColor("#2196F3"));//设置节点颜色
            line.setPointRadius(5);//设置节点半径
            line.setHasLabels(true);//是否显示节点数据
            line.setHasLines(true);//是否显示折线
            line.setHasPoints(true);//是否显示节点
            line.setShape(ValueShape.CIRCLE);//节点图形样式 DIAMOND菱形、SQUARE方形、CIRCLE圆形
            line.setHasLabelsOnlyForSelected(false);//隐藏数据，触摸可以显示
            lines.add(line);
        }

        axisY.setValues(axisValuesY);
        axisX.setValues(axisValuesX);
        axisX.setLineColor(Color.BLACK);//无效果
        axisY.setLineColor(Color.BLACK);//无效果
        axisX.setTextColor(Color.BLACK);//设置X轴文字颜色
        axisY.setTextColor(Color.BLACK);//设置Y轴文字颜色
        axisX.setTextSize(10);//设置X轴文字大小
        axisX.setTypeface(Typeface.SERIF);//设置文字样式
        axisX.setHasTiltedLabels(false);//设置X轴文字向左旋转45度
        axisX.setHasLines(false);//是否显示X轴网格线
        axisY.setHasLines(false);
        axisX.setInside(false);//设置X轴文字在X轴内部

        chartData = new LineChartData(lines);
        chartData.setAxisYLeft(axisY);//将Y轴属性设置到左边
        chartData.setAxisXBottom(axisX);//将X轴属性设置到底部
        chartData.setBaseValue(20);//设置反向覆盖区域颜色
        chartData.setValueLabelBackgroundAuto(false);//设置数据背景是否跟随节点颜色
        chartData.setValueLabelBackgroundColor(Color.BLUE);//设置数据背景颜色
        chartData.setValueLabelBackgroundEnabled(false);//设置是否有数据背景
        chartData.setValueLabelsTextColor(Color.parseColor("#000000"));//设置数据文字颜色
        chartData.setValueLabelTextSize(10);//设置数据文字大小
        chartData.setValueLabelTypeface(Typeface.MONOSPACE);//设置数据文字样式

        lineChart.setLineChartData(chartData);//将数据添加到控件中
        lineChart.setInteractive(true);
        //lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setVisibility(View.VISIBLE);
        Viewport tempViewport = new Viewport(0, lineChart.getMaximumViewport().height() * 1.4f, 9, 0);//调整y轴,使图标上部有留白:
        lineChart.setCurrentViewport(tempViewport);//left：0//X轴为0   top:chart.getMaximumViewport()//Y轴的最大值right: 9//X轴显示9列 bottom：0//Y轴为0
    }

    /*
    private void prepareDataAnimation(int type) {
        if (type == 0) {
            for (int t = 0; t < chartData.getLines().get(0).getValues().size(); t++) {
                PointValue value = chartData.getLines().get(0).getValues().get(t);
                // Here I modify target only for Y values but it is OK to modify X targets as well.
                value.setTarget(value.getX(), new Formatdata().longtofloat(pref.getLong(t + 1 + "", 0)));//new Formatdata().longtofloat(pref.getLong(value.getX() + 1 + "", 0))
                Log.d(TAG, "value.x:" + value.getX() + ",value.y:" + value.getY());
            }
        } else if (type == 1) {
            for (PointValue value : chartData.getLines().get(1).getValues()) {
                // Here I modify target only for Y values but it is OK to modify X targets as well.
                value.setTarget(value.getX(), 0.1f);//                    value.setTarget(value.getX(), );//
            }
        }
        Viewport tempViewport = new Viewport(0, lineChart.getMaximumViewport().height() * 1.4f, 9, 0);//调整y轴,使图标上部有留白:
        lineChart.setCurrentViewport(tempViewport);//left：0//X轴为0   top:chart.getMaximumViewport()//Y轴的最大值right: 9//X轴显示9列 bottom：0//Y轴为0
    }
*/
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.eachmonth) {
            addLineChartDate(0);//本月
            //prepareDataAnimation(0);
            //lineChart.startDataAnimation();
                /*
            case R.id.eachday:
                AddLineChartDate(1);//本日
                //prepareDataAnimation(1);
                //lineChart.startDataAnimation();
                break;
            */
        } else if (id == R.id.fab) {
            ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo activeInfo = manager.getActiveNetworkInfo();
            if (activeInfo == null) {
                Log.d("qiang", "网络没有连接");
                Snackbar.make(coordinatorLayout, getString(R.string.toast_search), Snackbar.LENGTH_LONG).show();
                return;
            }
            if (activeInfo.isConnected()) {
                if (Objects.equals(activeInfo.getTypeName(), "MOBILE")) {
                    SendMessage sendmessage = new SendMessage();//发送短信
                    if (Build.VERSION.SDK_INT >= 23) {
                        int checkCallPhonePermission = ContextCompat.checkSelfPermission(StatisticsActivity.this, Manifest.permission.RECEIVE_SMS);
                        if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(StatisticsActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, REQUEST_CODE);
                        } else {
                            progressDialog = new ProgressDialog(StatisticsActivity.this);
                            progressDialog.setMessage(getString(R.string.inquery));
                            progressDialog.setCancelable(true);
                            progressDialog.show();
                            sendmessage.sendMessages();
                            //静音
                            sendmessage.silent();
                            Snackbar.make(v, getString(R.string.sent), Snackbar.LENGTH_LONG).setAction("", null).show();
                            Log.d("qiang", "发送成功");
                        }
                    } else {
                        progressDialog = new ProgressDialog(StatisticsActivity.this);
                        progressDialog.setMessage(getString(R.string.inquery));
                        progressDialog.setCancelable(true);
                        progressDialog.show();
                        sendmessage.sendMessages();
                        //静音
                        sendmessage.silent();
                        Snackbar.make(v, getString(R.string.sent), Snackbar.LENGTH_SHORT).setAction("", null).show();
                        Log.d("qiang", "发送成功");
                    }
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
                    SMSBroadcastReceiver dianLiangBR = new SMSBroadcastReceiver();
                    registerReceiver(dianLiangBR, intentFilter);
                    dianLiangBR.setInteractionListener(StatisticsActivity.this);
                } else {
                    Snackbar.make(v, getString(R.string.toast_search), Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(v, getString(R.string.toast_search), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "requestCode:" + requestCode);
        switch (requestCode) {
            case 1:
                Log.d("qiang", "grantResults:" + grantResults[0]);
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    progressDialog = new ProgressDialog(StatisticsActivity.this);
                    progressDialog.setMessage(getString(R.string.inquery));
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    SendMessage sendmessage = new SendMessage();
                    sendmessage.sendMessages();
                    //静音
                    sendmessage.silent();
                    Log.d("qiang", "发送成功");
                } else {
                    // Permission Denied
                    Snackbar.make(getWindow().getDecorView(), getString(R.string.close_sms_grant), Snackbar.LENGTH_SHORT).show();
                    Log.d("qiang", "接受短信权限已关闭");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingintent = new Intent(StatisticsActivity.this, SettingActivity.class);
            startActivity(settingintent);
        } else if (id == R.id.log) {
            final AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(this);
            if (pref_default.getBoolean("log", false)) {
                alertDialog2.setTitle(getString(R.string.log));
                String logstr = new FileManager().readLogFile(this, "log_refresh");
                alertDialog2.setMessage(logstr);
                alertDialog2.setPositiveButton(getString(R.string.ok), null);
                alertDialog2.show();
            } else {
                alertDialog2.setTitle(getString(R.string.log));
                alertDialog2.setMessage(getString(R.string.log_message));
                alertDialog2.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(StatisticsActivity.this, SettingActivity.class));
                    }
                });
                alertDialog2.setNegativeButton(getString(R.string.cancel), null);
                alertDialog2.show();
            }
        } else if (id == R.id.help) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle(getString(R.string.help));
            String helpmessage = getString(R.string.help_message);
            alertDialog.setMessage(helpmessage);
            alertDialog.setPositiveButton(getString(R.string.ok), null);
            alertDialog.show();
        } else if (id == R.id.about_activity) {
            startActivity(new Intent(StatisticsActivity.this, AboutActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTexts(Context context, String[] content) {
        //delay();
        try {
            Log.d("qiang", "delay");
            Thread.currentThread();
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        recovery();
        if (content[1] != null && content[0] != null) {
            progressDialog.dismiss();
            editor.putLong("curmonthflow", 0);
            editor.putLong("remain_liuliang", new Formatdata().getNumFromString(content[0]));
            editor.putLong("all_liuliang", new Formatdata().getNumFromString(content[1]));
            editor.putBoolean("sent", true);
            editor.commit();

            CalculateTodayFlow calculateTodayFlow = new CalculateTodayFlow();
            long todayflow = calculateTodayFlow.calculate(context);
            new NotificationManagers().showNotificationPrecise(context, todayflow);
        } else {
            Snackbar.make(coordinatorLayout, getString(R.string.inquery_failure), Snackbar.LENGTH_SHORT).show();
        }

        long remain_liuliang = pref.getLong("remain_liuliang", 0);
        long all_liuliang = pref.getLong("all_liuliang", 0);
        long curdayflow = pref.getLong("curdayflow", 0);
        long lastmonthflow = pref.getLong("lastmonthflow", 0);
        long curfreetimeflow = pref.getLong("curfreetimeflow", 0);
        long curfreefront = pref.getLong("curfreefront", 0);
        long curfreebehind = pref.getLong("curfreebehind", 0);

        String textstr;
        if (pref_default.getBoolean("free", false)) {
            long allfreetimeflow = new Formatdata().getNumFromString(pref_default.getString("freeflow", "0") + "M");
            textstr = "本月可用流量（含闲时）：" + new Formatdata().long2string(all_liuliang) + "\n本月可用闲时流量：" + new Formatdata().long2string(allfreetimeflow)
                + "\n本月已用流量：" + new Formatdata().long2string(all_liuliang - remain_liuliang)
                + "\n本月还剩流量：" + new Formatdata().long2string(remain_liuliang) + "\n上个月使用流量：" + new Formatdata().long2string(lastmonthflow)
                + "\n今日使用流量(不含闲时)：" + new Formatdata().long2string(curdayflow - curfreebehind - curfreefront) + "\n今日闲时使用流量：" + new Formatdata().long2string(curfreetimeflow);
        } else {
            textstr = "本月可用流量：" + new Formatdata().long2string(all_liuliang) + "\n本月已用流量：" + new Formatdata().long2string(all_liuliang - remain_liuliang)
                + "\n本月还剩流量：" + new Formatdata().long2string(remain_liuliang) + "\n上个月使用流量：" + new Formatdata().long2string(lastmonthflow)
                + "\n今日使用流量：" + new Formatdata().long2string(curdayflow);
        }
        textView.setText(textstr);
    }

    public boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.example.AlarmReceiver".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void recovery() {
        Log.d("qiang", "volumn:" + volumn);
        if (Build.VERSION.SDK_INT >= 24) {
            audio = (AudioManager) getSystemService(AUDIO_SERVICE);
        } else {
            audio = (AudioManager) getSystemService(AUDIO_SERVICE);
            audio.setRingerMode(mode);
            audio.setStreamVolume(mode, volumn, 0);
        }
    }

    public class SendMessage {
        void sendMessages() {
            SmsManager manager = SmsManager.getDefault();
            manager.sendTextMessage(getString(R.string.phone), null, getString(R.string.message_search), null, null);  //发送短信
            Log.d("qiang", "发送短信中");
        }

        void silent() {
            //静音--------
            if (Build.VERSION.SDK_INT >= 24) {
                audio = (AudioManager) getSystemService(AUDIO_SERVICE);
                audio.adjustVolume(AudioManager.ADJUST_LOWER, 0);
            } else {
                audio = (AudioManager) getSystemService(AUDIO_SERVICE);
                mode = audio.getRingerMode();
                volumn = audio.getStreamVolume(AudioManager.STREAM_RING);
                audio.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
                audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                //audio.setRingerMode(RINGER_MODE_SILENT);
                //Log.d("qiang", "old_mode:" + volumn);
                //Log.d("qiang", "old_volumn:" + volumn);
            }
        }
    }
}
/*
    private void resetViewport() {
        // Reset viewport height range to (0,100)
        final Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.bottom = 0;
        v.top = 100;
        v.left = 0;
        v.right = LineChartNums - 1;
        lineChart.setMaximumViewport(v);
        lineChart.setCurrentViewport(v);
    }

    private void toggleCubic() {
        isCubic = !isCubic;

        if (isCubic) {
            final Viewport v = new Viewport(lineChart.getMaximumViewport());
            v.bottom = -5;
            v.top = 105;
            lineChart.setMaximumViewport(v);
            lineChart.setCurrentViewportWithAnimation(v);
        } else {
            // If not cubic restore viewport to (0,100) range.
            final Viewport v = new Viewport(lineChart.getMaximumViewport());
            v.bottom = 0;
            v.top = 100;
            lineChart.setViewportAnimationListener(new ChartAnimationListener() {
                @Override
                public void onAnimationStarted() {
                    // TODO Auto-generated method stub
                }
                @Override
                public void onAnimationFinished() {
                    lineChart.setMaximumViewport(v);
                    lineChart.setViewportAnimationListener(null);
                }
            });
            lineChart.setCurrentViewportWithAnimation(v);
        }
    }
*/