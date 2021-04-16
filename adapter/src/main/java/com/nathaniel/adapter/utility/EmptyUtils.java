package com.nathaniel.adapter.utility;

import android.text.Editable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.widget.EditText;

import androidx.annotation.NonNull;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

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
public final class EmptyUtils {

    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof String && TextUtils.isEmpty((String) object)) {
            return true;
        }
        if (object.getClass().isArray() && Array.getLength(object) == 0) {
            return true;
        }
        if (object instanceof Collection && ((Collection<?>) object).isEmpty()) {
            return true;
        }
        if (object instanceof Map && ((Map<?, ?>) object).isEmpty()) {
            return true;
        }
        if (object instanceof SparseArray && ((SparseArray<?>) object).size() == 0) {
            return true;
        }
        if (object instanceof SparseBooleanArray && ((SparseBooleanArray) object).size() == 0) {
            return true;
        }
        if (object instanceof SparseIntArray && ((SparseIntArray) object).size() == 0) {
            return true;
        }
        if (object instanceof Editable && ((Editable) object).length() == 0) {
            return true;
        }
        return object instanceof SparseLongArray && ((SparseLongArray) object).size() == 0;
    }

    public static boolean getLimitedEditor(@NonNull EditText editText, int minLength) {
        String string = getEditorText(editText, true);
        return !isEmpty(string) && string.length() >= minLength;
    }

    /**
     * 后期放在StringUtils中
     *
     * @param editText   editText
     * @param trimEnable trimEnable
     * @return string
     */
    public static String getEditorText(EditText editText, boolean trimEnable) {
        String result = getEditorText(editText);
        if (!EmptyUtils.isEmpty(result) && trimEnable) {
            result = result.trim();
        }
        return result;
    }

    /**
     * 不对外开放
     *
     * @param editText editText
     * @return string
     */
    private static String getEditorText(EditText editText) {
        return isEmpty(editText.getText()) ? null : editText.getText().toString();
    }

    @Deprecated
    public static boolean isNotEmpty(Object object) {
        return !isEmpty(object);
    }
}