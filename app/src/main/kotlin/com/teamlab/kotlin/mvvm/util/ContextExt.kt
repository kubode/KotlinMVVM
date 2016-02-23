package com.teamlab.kotlin.mvvm.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

private val handler = Handler(Looper.getMainLooper())

fun Context.showToast(text: CharSequence?) {
    handler.post { Toast.makeText(this, text, Toast.LENGTH_SHORT).show() }
}
