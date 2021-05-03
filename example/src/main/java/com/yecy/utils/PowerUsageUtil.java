package com.yecy.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.util.TypedValue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PowerUsageUtil {

    private static final String TAG = PowerUsageUtil.class.getSimpleName();
    private static final int MIN_POWER_THRESHOLD_MILLI_AMP = 5;
    private static final int MAX_ITEMS_TO_LIST = 10;
    private static final int SECONDS_IN_HOUR = 60 * 60;
    private static PowerUsageUtil INSTANCE = null;
    private final int MIN_AVERAGE_POWER_THRESHOLD_MILLI_AMP = 10;
    private final int mStatsType = BatteryStats.STATS_SINCE_CHARGED;

    public static PowerUsageUtil getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PowerUsageUtil();
        }
        return INSTANCE;
    }


    /**
     * 更新电量信息
     */
    public List<String[]> getAppPowerUsageApi25(Context context) {
        Log.d(TAG, "getAppPowerUsageApi25: executed");
        List<String[]> result = new ArrayList<>();
        double totalSum = 0;
        BatteryStatsHelper mStatsHelper = new BatteryStatsHelper(context, true);
        Bundle nall = null;
        mStatsHelper.create(nall);
        mStatsHelper.clearStats();

        final PowerProfile powerProfile = mStatsHelper.getPowerProfile();
        final BatteryStats stats = mStatsHelper.getStats();
        final double averagePower = powerProfile.getAveragePower(PowerProfile.POWER_SCREEN_FULL);
        Log.d(TAG, "getAppPowerUsageApi25: average power=" + averagePower);
        TypedValue value = new TypedValue();

        if (averagePower >= MIN_AVERAGE_POWER_THRESHOLD_MILLI_AMP) {
            UserManager userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
            final List<UserHandle> profiles = userManager.getUserProfiles();
            mStatsHelper.refreshStats(BatteryStats.STATS_SINCE_CHARGED, profiles);
            final List<BatterySipper> usageList = mStatsHelper.getUsageList();
            //放电量
            final int dischargeAmount = (stats != null ? stats.getDischargeAmount(mStatsType) : 0);
            final int numSippers = usageList.size();
            Log.d(TAG, "getAppPowerUsageApi25: list size=" + numSippers);
            for (int i = 0; i < numSippers; i++) {
                final BatterySipper sipper = usageList.get(i);
                double totalPowerMah = getTotalPowerMah(sipper);
                if ((totalPowerMah * SECONDS_IN_HOUR) < MIN_POWER_THRESHOLD_MILLI_AMP) {
                    continue;
                }
                double totalPower = mStatsHelper.getTotalPower();
//                Log.i(TAG, "getAppPowerUsageApi25: totalPowerMah="+ totalPowerMah+ "， totalPower="+ totalPower+
//                        ", dischargeAmount="+ dischargeAmount);
                final double percentOfTotal =
                    ((totalPowerMah / totalPower) * dischargeAmount);
//                if (((int) (percentOfTotal + .5)) < 1) {
//                    continue;
//                }
//                if (sipper.drainType == BatterySipper.DrainType.OVERCOUNTED) {
//                    // Don't show over-counted unless it is at least 2/3 the size of
//                    // the largest real entry, and its percent of total is more significant
//                    if (totalPowerMah < ((mStatsHelper.getMaxRealPower()*2)/3)) {
//                        continue;
//                    }
//                    if (percentOfTotal < 10) {
//                        continue;
//                    }
//                    if ("user".equals(Build.TYPE)) {
//                        continue;
//                    }
//                }
//                if (sipper.drainType == BatterySipper.DrainType.UNACCOUNTED) {
//                    // Don't show over-counted unless it is at least 1/2 the size of
//                    // the largest real entry, and its percent of total is more significant
//                    if (totalPowerMah < (mStatsHelper.getMaxRealPower()/2)) {
//                        continue;
//                    }
//                    if (percentOfTotal < 5) {
//                        continue;
//                    }
//                    if ("user".equals(Build.TYPE)) {
//                        continue;
//                    }
//                }
//                final UserHandle userHandle = new UserHandle(UserHandle.getUserId(sipper.getUid()));
                final double percentOfMax = (totalPowerMah * 100)
                    / mStatsHelper.getMaxPower();
                sipper.percent = percentOfTotal;

                String[] temp = new String[3];
                temp[0] = sipper.packageWithHighestDrain;
                temp[1] = String.valueOf(percentOfTotal);
                temp[2] = String.valueOf(totalPowerMah);
                result.add(temp);
                totalSum += totalPowerMah;
                Log.d(TAG, "getAppPowerUsageApi25: package=" + sipper.packageWithHighestDrain + " power=" + percentOfTotal + ", percentoOfMax=" + percentOfMax);
            }
        }
        Log.d(TAG, "getAppPowerUsageApi25: total sum=" + totalSum);
        return result;
    }

    private double getTotalPowerMah(BatterySipper bs) {
        Class<?> class1 = bs.getClass();
        Field field = null;//变量名称
        try {
            field = class1.getDeclaredField("totalPowerMah");
            field.setAccessible(true);//开放权限
            double value = (double) field.get(bs);//treeToolbar类实例
            return value;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return 0;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取给个应用耗电信息
     *
     * @return List<String [ ]> 所有应用的耗电信息：
     * String[0]为包名；
     * String[1]为该包名对应应用的耗电量（mAh）
     */
    public List<String[]> getAppPowerUsageApi22(Context context) {
        Log.d(TAG, "getAppPowerUsageApi22: executed");
//        NetworkStatsFactory.javaReadNetworkStatsDetail()

        List<String[]> powerInfos = new ArrayList<>();

        List<BatterySipper> usageList = null;
        double totalPower = 0;
        int dischargeAmount = 0;

        Class<?> clazz;
        try {
            //创建实例
            //mStatsHelper = new BatteryStatsHelper(context, true);
            clazz = Class.forName("com.android.internal.os.BatteryStatsHelper");
            Constructor c = clazz.getConstructor(Context.class, boolean.class);//获取有参构造
            Object batteryStatsHelper = c.newInstance(context, true);

            //mStatsHelper.create(bundle); 初始化参数
            Method create = batteryStatsHelper.getClass().getMethod("create", Bundle.class);
            Object nall = null;
            create.invoke(batteryStatsHelper, nall);

            //mStatsHelper.clearStats(); 清除历史数据
            Method clearStats = batteryStatsHelper.getClass().getMethod("clearStats");
            clearStats.invoke(batteryStatsHelper);

            //设置User Profile
            UserManager userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
            final List<UserHandle> profiles = userManager.getUserProfiles();
            //mStatsHelper.refreshStats(BatteryStats.STATS_SINCE_CHARGED, profiles);
            Method refreshStats = batteryStatsHelper.getClass().getMethod("refreshStats", int.class, List.class);
            refreshStats.invoke(batteryStatsHelper, BatteryStats.STATS_SINCE_CHARGED, profiles);

            //获取应用耗电量数据列表
            Method getUsageList = batteryStatsHelper.getClass().getMethod("getUsageList");
            usageList = (List<BatterySipper>) getUsageList.invoke(batteryStatsHelper);
            //获取系统耗电量
//            mStatsHelper.getTotalPower()
            Method getTotalPower = batteryStatsHelper.getClass().getMethod("getTotalPower");
            totalPower = (double) getTotalPower.invoke(batteryStatsHelper);
//            LogUtils.log("总电量："+totalPower);

//            BatteryStats stats = mStatsHelper.getStats();
            Method getStats = batteryStatsHelper.getClass().getMethod("getStats");
            Object stats = getStats.invoke(batteryStatsHelper);
            //获取充电结束后的用电量
//            final int dischargeAmount = stats != null ? stats.getDischargeAmount(mStatsType) : 0;
            if (stats == null) {
                dischargeAmount = 0;
            } else {
                Method getDischargeAmount = stats.getClass().getMethod("getDischargeAmount", int.class);
                dischargeAmount = (int) getDischargeAmount.invoke(stats, 0);
            }
            Log.d(TAG, "getAppPowerUsageApi22: dischargeAmount:" + dischargeAmount);
            if (usageList != null && usageList.size() > 0) {
                for (int i = 0; i < usageList.size(); i++) {
                    BatterySipper bs = usageList.get(i);
//                    int uid=bs.getUid();
//                    String[] packages=bs.getPackages();
                    String packageWithHighestDrain = bs.packageWithHighestDrain;
                    double value = bs.value;
//                    double[] values=bs.values;
                    double percentOfTotal = (value / totalPower * dischargeAmount);
                    Log.d(TAG, "getAppPowerUsageApi22: packageWithHighestDrain:" + packageWithHighestDrain
                        + ",占比:" + percentOfTotal
                        + ",value:" + value
                    );
                    String[] info = new String[3];
                    info[0] = packageWithHighestDrain;
                    info[1] = String.valueOf(percentOfTotal);
                    info[2] = String.valueOf(value);
                    powerInfos.add(info);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return powerInfos;

    }
}
