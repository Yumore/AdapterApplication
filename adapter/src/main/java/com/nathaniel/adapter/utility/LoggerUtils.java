package com.nathaniel.adapter.utility;

import android.util.Log;

import androidx.annotation.NonNull;

/**
 * @author Nathaniel
 * @date 2018/5/29-10:17
 */
public final class LoggerUtils {
    private static final String TAG = LoggerUtils.class.getSimpleName();

    public static void logger(String message) {
        logger(TAG, message);
    }

    public static void logger(@NonNull String tag, String message) {
        logger(tag, Level.ERROR, message);
    }

    public static void logger(@NonNull String tag, @NonNull Level level, String message) {
        if (EmptyUtils.isEmpty(message)) {
            message = "logger message is empty in " + LoggerUtils.class.getSimpleName();
        }
        int length = message.length();
        int started = 0;
        int maxLength = 2000;
        int ending = maxLength;
        int passage = (message.length() + maxLength) / maxLength;
        for (int index = 0; index < passage; index++) {
            if (length > ending) {
                logged(tag, level, message.substring(started, ending));
                started = ending;
                ending = ending + maxLength;
            } else {
                logged(tag, level, message.substring(started, length));
                break;
            }
        }
    }

    private static void logged(@NonNull String tag, @NonNull Level level, @NonNull String message) {
        switch (level) {
            case DEBUG:
                Log.d(tag, message);
                break;
            case WARING:
                Log.w(tag, message);
                break;
            case INFO:
                Log.i(tag, message);
                break;
            case ERROR:
                Log.e(tag, message);
                break;
            case ASSERT:
            case VERBOSE:
            default:
                Log.v(tag, message);
                break;
        }
    }

    public enum Level {
        /**
         * 日志级别
         */
        INFO,
        DEBUG,
        WARING,
        ERROR,
        ASSERT,
        VERBOSE
    }
}
