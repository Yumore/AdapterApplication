package com.nathaniel.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataUsageUtil {

    private static final String TAG = DataUsageUtil.class.getSimpleName();
    private static final int LOADER_CHART_DATA = 2;
    private static final int LOADER_SUMMARY = 3;
    private static Context mContext;
    private static DataUsageUtil INSTANCE = null;
    INetworkStatsSession mSession;
    PackageManager pm = null;
    NetworkStats mStats = null;
    private final LoaderManager.LoaderCallbacks<NetworkStats> mSummaryCallbacks = new LoaderManager.LoaderCallbacks<NetworkStats>() {
        @Override
        public Loader<NetworkStats> onCreateLoader(int id, Bundle args) {
            Log.d(TAG, "onCreateLoader: id=" + id);
            return new SummaryForAllUidLoader(mContext, mSession, args);
        }

        @Override
        public void onLoadFinished(Loader<NetworkStats> loader, NetworkStats data) {
            Log.d(TAG, "onLoadFinished: loader=" + loader + ", data=" + data);
            mStats = data;
        }

        @Override
        public void onLoaderReset(Loader<NetworkStats> loader) {
            Log.d(TAG, "onLoaderReset: loader=" + loader);
        }

        private void updateEmptyVisible() {
            Log.d(TAG, "updateEmptyVisible: executed");
        }
    };


    public DataUsageUtil(Context context) {
        mContext = context;
    }

    public static DataUsageUtil getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new DataUsageUtil(context);
        }
        return INSTANCE;
    }

    public Map<Integer, DataUsageWifi> forLoaderManager(NetworkTemplate template, long start, long end) {
        INetworkStatsService mService
            = INetworkStatsService.Stub.asInterface(ServiceManager.getService(Context.NETWORK_STATS_SERVICE));
        try {
            mSession = mService.openSession();
            if (mSession == null) {
                Log.e(TAG, "getDataUsageWifi: the session is null");
                return null;
            }
            mStats = null;
            ((Activity) mContext).getLoaderManager().restartLoader(LOADER_SUMMARY,
                SummaryForAllUidLoader.buildArgs(template, start, end), mSummaryCallbacks);
            int time = 50;
            while (true) {
                if (mStats != null)
                    break;
                if (time-- < 0)
                    break;
                Thread.sleep(50);
            }
            if (mStats == null) {
                Log.e(TAG, "getDataUsageWifi: get the netword data usage failed");
                return null;
            }
            pm = mContext.getPackageManager();
            Map<Integer, DataUsageWifi> map = new HashMap<>();
            int size = mStats.size();
            NetworkStats.Entry entity = null;
            for (int i = 0; i < size; i++) {
                entity = mStats.getValues(i, entity);
                if (map.containsKey(entity.uid)) {
                    //uid相同，则将值相加
                    DataUsageWifi info = map.get(entity.uid);
                    info.rxByte += entity.rxBytes;
                    info.txByte += entity.txBytes;
                    map.put(entity.uid, info);
                } else {
                    String name = pm.getNameForUid(entity.uid);
                    map.put(entity.uid, new DataUsageWifi(entity.uid, name, entity.rxBytes, entity.txBytes));
//                    Log.d(TAG, "onLoadFinished: name="+ name);
//                    Log.d(TAG, "onLoadFinished: uid="+ entity.uid);
//                    Log.d(TAG, "onLoadFinished: set="+ entity.set);
//                    Log.d(TAG, "onLoadFinished: rxByte="+ entity.rxBytes);
//                    Log.d(TAG, "onLoadFinished: txByte="+ entity.txBytes);
                }
            }
            return map;
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public Map<Integer, DataUsageWifi> forNetworkStatsManager(int type, long start, long end) {
        Log.d(TAG, "getAllDataUsage: start=" + start + ", end=" + end);
        PackageManager pm = (PackageManager) mContext.getPackageManager();
        if (pm == null) {
            Log.e(TAG, "getAllDataUsage: get the telephone manager failed");
            return null;
        }
        TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (pm == null) {
            Log.e(TAG, "getAllDataUsage: get the telephone manager failed");
            return null;
        }
        NetworkStatsManager nsm = (NetworkStatsManager) mContext.getSystemService(Context.NETWORK_STATS_SERVICE);
        if (nsm == null) {
            Log.e(TAG, "getAllDataUsage: get the network stats manager failed");
            return null;
        }
        @SuppressLint("WrongConstant")
        List<ApplicationInfo> list = pm.getInstalledApplications(PackageManager.GET_ACTIVITIES);
        try {
            Map<Integer, DataUsageWifi> result = getDataUsageByUid(nsm, tm, type, start, end);
            if (result == null) {
                return null;
            }
            for (ApplicationInfo info : list) {
                DataUsageWifi data = result.get(info.uid);
                if (data == null) {
                    continue;
                }
                data.packageName = info.packageName;
            }
            return result;
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private Map<Integer, DataUsageWifi> getDataUsageByUid(NetworkStatsManager nsm, TelephonyManager tm,
                                                          int type, long start, long end) throws RemoteException {
        Log.d(TAG, "listDataUsageByUid: executed");
        // 获取subscriberId
        String subId = tm.getSubscriberId();
        if ((type == ConnectivityManager.TYPE_MOBILE) && (subId == null)) {
            Log.e(TAG, "listDataUsageByUid: get sub id fail");
            return null;
        }
        android.app.usage.NetworkStats summaryStats;
        android.app.usage.NetworkStats.Bucket summaryBucket
            = new android.app.usage.NetworkStats.Bucket();
        Map<Integer, DataUsageWifi> map = new HashMap<>();

        summaryStats = nsm.querySummary(type, subId, start, end);
        do {
            summaryStats.getNextBucket(summaryBucket);
            int summaryUid = summaryBucket.getUid();
            long summaryRx = summaryBucket.getRxBytes();
            long summaryTx = summaryBucket.getTxBytes();
            Log.i(TAG, "uid:" + summaryBucket.getUid() + " rx:" + summaryBucket.getRxBytes() +
                " tx:" + summaryBucket.getTxBytes());
            DataUsageWifi info = new DataUsageWifi(summaryUid, "", summaryRx, summaryTx);
            map.put(summaryUid, info);
        } while (summaryStats.hasNextBucket());
        return map;
    }

    public class DataUsageWifi {

        public long uid = 0;
        public String packageName = null;
        public long rxByte = 0;
        public long txByte = 0;

        public DataUsageWifi(long uid, String packageName, long rxByte, long txByte) {
            this.uid = uid;
            this.packageName = packageName;
            this.rxByte = rxByte;
            this.txByte = txByte;
        }
    }

}
