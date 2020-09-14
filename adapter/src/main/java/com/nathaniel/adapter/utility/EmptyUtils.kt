package com.nathaniel.adapter.utility

import android.annotation.TargetApi
import android.os.Build
import android.text.Editable
import android.text.TextUtils
import android.util.SparseArray
import android.util.SparseBooleanArray
import android.util.SparseIntArray
import android.util.SparseLongArray
import android.widget.EditText
import java.lang.reflect.Array

/**
 * EmptyUtils
 * 非空判断工具类
 * TODO 注意：
 * String 只判断了正常的非空
 * 没有判断 "null"、"nil"等
 * 在使用的时候需要根据自己的需求加以判断
 *
 * @author Nathaniel
 * nathanwriting@126.com
 * @version v1.0.0
 * @date 18-7-2 - 上午10:31
 */
object EmptyUtils {
    @JvmStatic
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun isObjectEmpty(`object`: Any?): Boolean {
        if (`object` == null) {
            return true
        }
        if (`object` is String && TextUtils.isEmpty(`object` as String?)) {
            return true
        }
        if (`object`.javaClass.isArray && Array.getLength(`object`) == 0) {
            return true
        }
        if (`object` is Collection<*> && `object`.isEmpty()) {
            return true
        }
        if (`object` is Map<*, *> && `object`.isEmpty()) {
            return true
        }
        if (`object` is SparseArray<*> && `object`.size() == 0) {
            return true
        }
        if (`object` is SparseBooleanArray && `object`.size() == 0) {
            return true
        }
        if (`object` is SparseIntArray && `object`.size() == 0) {
            return true
        }
        return if (`object` is Editable && `object`.length == 0) {
            true
        } else `object` is SparseLongArray && `object`.size() == 0
    }

    fun getLimitedEditor(editText: EditText, minLength: Int): Boolean {
        val string = getEditorText(editText, true)
        return !isObjectEmpty(string) && string!!.length >= minLength
    }

    /**
     * 后期放在StringUtils中
     *
     * @param editText   editText
     * @param trimEnable trimEnable
     * @return string
     */
    fun getEditorText(editText: EditText, trimEnable: Boolean): String? {
        var result = getEditorText(editText)
        if (!isObjectEmpty(result) && trimEnable) {
            result = result!!.trim { it <= ' ' }
        }
        return result
    }

    /**
     * 不对外开放
     *
     * @param editText editText
     * @return string
     */
    private fun getEditorText(editText: EditText): String? {
        return if (isObjectEmpty(editText.text)) null else editText.text.toString()
    }

    @Deprecated("")
    fun isObjectNotEmpty(`object`: Any?): Boolean {
        return !isObjectEmpty(`object`)
    }
}