package com.teamlab.kotlin.mvvm.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

object Toaster {
    private val handler = Handler(Looper.getMainLooper())
    fun show(context: Context, text: CharSequence?) {
        handler.post { Toast.makeText(context, text, Toast.LENGTH_SHORT).show() }
    }
}
