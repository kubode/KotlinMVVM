package com.teamlab.kotlin.mvvm.util

import android.util.Log

/**
 * 汎用ロガー
 *
 * Usage:
 * ```
 * logV({"test"}) // logged `V/ClassName: test`
 * ```
 */
object Logger {
    var level: LogLevel = LogLevel.V
}

enum class LogLevel(val priority: Int) {
    V(Log.VERBOSE),
    D(Log.DEBUG),
    I(Log.INFO),
    W(Log.WARN),
    E(Log.ERROR),
}

inline fun Any.log(level: LogLevel, msgCreator: () -> String, e: Throwable? = null) {
    if (level.ordinal >= Logger.level.ordinal) {
        val msg = msgCreator() + (e?.let { "\n" + Log.getStackTraceString(it) } ?: "")
        val tag = javaClass.name.substringAfterLast('.').substringBefore('$')
        Log.println(level.priority, tag, msg)
    }
}

inline fun Any.logV(msgCreator: () -> String, e: Throwable? = null) = log(LogLevel.V, msgCreator, e)
inline fun Any.logD(msgCreator: () -> String, e: Throwable? = null) = log(LogLevel.D, msgCreator, e)
inline fun Any.logI(msgCreator: () -> String, e: Throwable? = null) = log(LogLevel.I, msgCreator, e)
inline fun Any.logW(msgCreator: () -> String, e: Throwable? = null) = log(LogLevel.W, msgCreator, e)
inline fun Any.logE(msgCreator: () -> String, e: Throwable? = null) = log(LogLevel.E, msgCreator, e)
