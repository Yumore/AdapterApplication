package com.nathaniel.sample

import android.content.Context
import android.content.SharedPreferences

internal object PreferencesUtils {
    @Volatile
    private var mSharedPreferences: SharedPreferences? = null
    @Synchronized
    fun initSharedPreferences(context: Context): SharedPreferences? {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences("refresh_time", Context.MODE_PRIVATE)
        }
        return mSharedPreferences
    }

    /**
     * 对全局变量指定写入一个long值.
     *
     * @param key   KEY
     * @param value 值
     */
    fun writeRefreshTime(key: String?, value: Long) {
        val editor = mSharedPreferences!!.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getRefreshTime(key: String?): Long {
        return mSharedPreferences!!.getLong(key, 0)
    }
}