package com.nathaniel.sample;

import android.annotation.SuppressLint;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Build;
import android.os.RemoteException;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.nathaniel.adapter.utility.EmptyUtils;
import com.nathaniel.adapter.utility.LoggerUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nathaniel
 * @version V1.0.0
 * @contact <a href="mailto:nathanwriting@126.com">contact me</a>
 * @package com.nathaniel.sample
 * @datetime 4/29/21 - 7:57 PM
 */
public class AppUtils {

    private static final String TAG = AppUtils.class.getSimpleName();

    private static List<String> getAllInstalledApkInfo(Context context) {
        List<String> packageNames = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        @SuppressLint("QueryPermissionsNeeded")
        List<ResolveInfo> resolveInfoList = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfoList) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
//            if (!isSystemPackage(resolveInfo)) {
            packageNames.add(activityInfo.applicationInfo.packageName);
//            }
        }
        return packageNames;
    }

    private static boolean isSystemPackage(ResolveInfo resolveInfo) {
        return ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    private static Drawable getAppIconByPackageName(Context context, String packageName) {
        Drawable drawable;
        try {
            drawable = context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            drawable = ContextCompat.getDrawable(context, R.mipmap.ic_launcher);
        }
        return drawable;
    }

    private static String getAppNameByPackageName(Context context, String packageName) {
        String appName = "";
        ApplicationInfo applicationInfo;
        PackageManager packageManager = context.getPackageManager();
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            if (applicationInfo != null) {
                appName = (String) packageManager.getApplicationLabel(applicationInfo);
            }
        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
        }
        return appName;
    }

    private static String getVersionNameByPackageName(Context context, String packageName) {
        String versionName = "";
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            if (packageInfo != null) {
                versionName = packageInfo.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }


    private static int getVersionCodeByPackageName(Context context, String packageName) {
        int versionCode = 0;
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            if (packageInfo != null) {
                versionCode = packageInfo.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static List<PackageEntity> getPackageEntityList(Context context) {
        List<PackageEntity> packageEntities = new ArrayList<>();
        List<String> packageNames = getAllInstalledApkInfo(context);
        for (String packageName : packageNames) {
            PackageEntity packageEntity = new PackageEntity();
            packageEntity.setAppName(getAppNameByPackageName(context, packageName));
            packageEntity.setPackageName(packageName);
            packageEntity.setAppIcon(getAppIconByPackageName(context, packageName));
            packageEntity.setVersionCode(getVersionCodeByPackageName(context, packageName));
            packageEntity.setVersionName(getVersionNameByPackageName(context, packageName));
            packageEntities.add(packageEntity);
        }
        return packageEntities;
    }

    public static List<PackageEntity> getPackageEntities(Context context) {
        List<String[]> stringsList = getNetworkUsageAllUid();
        List<PackageEntity> packageEntities = new ArrayList<>();
        @SuppressLint("QueryPermissionsNeeded")
        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(0);
        for (PackageInfo packageInfo : installedPackages) {
            PackageEntity packageEntity = new PackageEntity();
            packageEntity.setAppName(packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString());
            packageEntity.setPackageName(packageInfo.packageName);
            packageEntity.setVersionName(packageInfo.versionName);
            packageEntity.setVersionCode(packageInfo.versionCode);
            packageEntity.setAppIcon(packageInfo.applicationInfo.loadIcon(context.getPackageManager()));
            packageEntity.setPid(packageInfo.applicationInfo.uid);

            if (EmptyUtils.isEmpty(stringsList)) {
                // NetworkManagementSocketTagger和TrafficStats.setThreadStatsUid()
                int uid = packageInfo.applicationInfo.uid;
                packageEntity.setRx(TrafficStats.getUidRxBytes(uid));
                packageEntity.setTx(TrafficStats.getUidTxBytes(uid));
            } else {
                long[] totalData = getNetworkUsageByUid(packageInfo.applicationInfo.uid);
                packageEntity.setRx(totalData[0]);
                packageEntity.setTx(totalData[1]);
//                for (String[] strings : stringsList) {
//                    if (Integer.parseInt(strings[3]) == packageInfo.applicationInfo.uid) {
//                        packageEntity.setRx(Integer.parseInt(strings[5]));
//                        packageEntity.setTx(Integer.parseInt(strings[7]));
//                        break;
//                    }
//                }
            }
//            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            packageEntities.add(packageEntity);
//            }
        }
        return packageEntities;
    }

    public static long[] getNetworkUsage() {
        BufferedReader bufferedReader;
        String line;
        String[] values;
        long[] totalBytes = new long[2];
        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/net/dev"));
            while ((line = bufferedReader.readLine()) != null) {
                // LoggerUtils.logger(TAG, String.format("source content is : %s", line.trim()));
                if (line.contains("eth") || line.contains("lo") || line.contains("wlan")) {
                    values = line.trim().split("\\s+");
                    totalBytes[0] += Long.parseLong(values[1]);
                    totalBytes[1] += Long.parseLong(values[9]);
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return totalBytes;
    }

    //
    public static long[] getNetworkUsageByUid(long uid) {
        BufferedReader bufferedReader;
        String line;
        String[] values;
        long[] totalBytes = new long[2];
        try {
            @SuppressLint("DefaultLocale")
            String filePath = String.format("/proc/%d/net/dev", uid);
            bufferedReader = new BufferedReader(new FileReader(filePath));
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("eth") || line.contains("lo") || line.contains("wlan")) {
                    values = line.trim().split("\\s+");
                    totalBytes[0] += Long.parseLong(values[1]);
                    totalBytes[1] += Long.parseLong(values[9]);
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return totalBytes;
    }

    public static List<String[]> getNetworkUsageAllUid() {
        BufferedReader bufferedReader;
        String line;
        List<String[]> stringsList = new ArrayList<>();
        try {
            String filePath = "/proc/net/xt_qtaguid/stats";
            if (!new File(filePath).exists()) {
                return stringsList;
            }
            bufferedReader = new BufferedReader(new FileReader(filePath));
            while ((line = bufferedReader.readLine()) != null) {
                LoggerUtils.logger(TAG, String.format("source content is : %s", line.trim()));
                if (line.contains("idx")) {
                    continue;
                }
                String[] values = line.trim().split("\\s+");
                stringsList.add(values);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringsList;
    }

    /**
     * 获取应用程序名称
     */
    public static synchronized String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized int getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static synchronized String getPackageName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取图标 bitmap
     *
     * @param context
     */
    public static synchronized Bitmap getBitmap(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        Drawable applicationIcon = packageManager.getApplicationIcon(applicationInfo);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) applicationIcon;
        return bitmapDrawable.getBitmap();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public long getAllRxBytesMobile(Context context) {
        final long startTime = 0;
        NetworkStats.Bucket bucket;
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);
        try {
            bucket = networkStatsManager.querySummaryForDevice(ConnectivityManager.TYPE_MOBILE,
                    getSubscriberId(context, ConnectivityManager.TYPE_MOBILE),
                    startTime,
                    System.currentTimeMillis());
        } catch (RemoteException e) {
            return -1;
        }
        return bucket.getRxBytes();
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    private String getSubscriberId(Context context, int networkType) {
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return telephonyManager.getSubscriberId();
        }
        return "";
    }

    public void listInstallPackages(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String[] packageNames = null;
        int uid = 1000;
        while (uid <= 19999) {
            packageNames = packageManager.getPackagesForUid(uid);
            if (packageNames != null && packageNames.length > 0) {
                for (String item : packageNames) {
                    try {
                        final PackageInfo packageInfo = packageManager.getPackageInfo(item, 0);
                        if (packageInfo == null) {
                            break;
                        }
                        CharSequence applicationLabel = packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageInfo.packageName, PackageManager.GET_META_DATA));
                        LoggerUtils.logger(TAG, LoggerUtils.Level.DEBUG, "应用名称 = " + applicationLabel.toString() + " (" + packageInfo.packageName + ")");
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            uid++;
        }
    }
}
