package com.nathaniel.adapter.utility

import android.util.Log
import com.nathaniel.adapter.utility.EmptyUtils.isObjectEmpty

/**
 * @author Nathaniel
 * @date 2018/5/29-10:17
 */
object LoggerUtils {
    private val TAG = LoggerUtils::class.java.simpleName
    fun logger(message: String) {
        logger(TAG, message)
    }

    fun logger(tag: String, message: String) {
        logger(tag, Level.ERROR, message)
    }

    private fun logger(tag: String, level: Level, message: String) {
        var message = message
        if (isObjectEmpty(message)) {
            message = "logger message is empty in " + LoggerUtils::class.java.simpleName
        }
        val length = message.length
        var started = 0
        val maxLength = 2000
        var ending = maxLength
        val passage = (message.length + maxLength) / maxLength
        for (index in 0 until passage) {
            if (length > ending) {
                logged(tag, level, message.substring(started, ending))
                started = ending
                ending = ending + maxLength
            } else {
                logged(tag, level, message.substring(started, length))
                break
            }
        }
    }

    private fun logged(tag: String, level: Level, message: String) {
        when (level) {
            Level.DEBUG -> Log.d(tag, message)
            Level.WARING -> Log.w(tag, message)
            Level.INFO -> Log.i(tag, message)
            Level.ERROR -> Log.e(tag, message)
            Level.ASSERT, Level.VERBOSE -> Log.v(tag, message)
            else -> Log.v(tag, message)
        }
    }

    enum class Level {
        /**
         * 日志级别
         */
        INFO, DEBUG, WARING, ERROR, ASSERT, VERBOSE
    }
}