package com.nathaniel.sample;

import android.graphics.drawable.Drawable;

/**
 * @author nathaniel
 * @version V1.0.0
 * @contact <a href="mailto:nathanwriting@126.com">contact me</a>
 * @package com.nathaniel.sample
 * @datetime 4/29/21 - 8:00 PM
 */
public class PackageEntity {
    private String packageName;
    private Drawable appIcon;
    private String appName;
    private int versionCode;
    private String versionName;
    private long rx;
    private long tx;
    private int pid;

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public long getRx() {
        return rx;
    }

    public void setRx(long rx) {
        this.rx = rx;
    }

    public long getTx() {
        return tx;
    }

    public void setTx(long tx) {
        this.tx = tx;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    @Override
    public String toString() {
        return "PackageEntity{" +
                "packageName='" + packageName + '\'' +
                ", appIcon=" + appIcon +
                ", appName='" + appName + '\'' +
                ", versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", rx=" + rx +
                ", tx=" + tx +
                ", pid=" + pid +
                '}';
    }
}
