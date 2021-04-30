package eu.faircode.netguard;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

public class ApplicationEx extends Application {
    private static final String TAG = ApplicationEx.class.getSimpleName();

    private Thread.UncaughtExceptionHandler exceptionHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Create version=" + Util.getSelfVersionName(this) + "/" + Util.getSelfVersionCode(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannels();
        }
        exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
                if (Util.ownFault(ApplicationEx.this, throwable) && Util.isPlayStoreInstall(ApplicationEx.this)) {
                    Log.e(TAG, throwable.toString() + "\n" + Log.getStackTraceString(throwable));
                    exceptionHandler.uncaughtException(thread, throwable);
                } else {
                    Log.w(TAG, throwable.toString() + "\n" + Log.getStackTraceString(throwable));
                    System.exit(1);
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannels() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel foreground = new NotificationChannel("foreground", getString(R.string.channel_foreground), NotificationManager.IMPORTANCE_MIN);
        foreground.setSound(null, Notification.AUDIO_ATTRIBUTES_DEFAULT);
        nm.createNotificationChannel(foreground);

        NotificationChannel notify = new NotificationChannel("notify", getString(R.string.channel_notify), NotificationManager.IMPORTANCE_DEFAULT);
        notify.setSound(null, Notification.AUDIO_ATTRIBUTES_DEFAULT);
        nm.createNotificationChannel(notify);

        NotificationChannel access = new NotificationChannel("access", getString(R.string.channel_access), NotificationManager.IMPORTANCE_DEFAULT);
        access.setSound(null, Notification.AUDIO_ATTRIBUTES_DEFAULT);
        nm.createNotificationChannel(access);
    }
}
