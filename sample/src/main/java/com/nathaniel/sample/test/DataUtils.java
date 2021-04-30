package com.nathaniel.sample.test;

import android.annotation.SuppressLint;

/**
 * @author admin
 */
public class DataUtils {
    @SuppressLint("DefaultLocale")
    public static String getRealDataSize(long dataSize) {
        if (dataSize > 1024 * 1024 * 1024) {
            dataSize = dataSize / (1024 * 1024 * 1024);
            return String.format("%d GB", dataSize);
        } else if (dataSize > 1024 * 1024) {
            dataSize = dataSize / (1024 * 1024);
            return String.format("%d MB", dataSize);
        } else if (dataSize > 1024) {
            dataSize = dataSize / 1024;
            return String.format("%d KB", dataSize);
        }
        return String.format("%d B", dataSize);
    }
}
