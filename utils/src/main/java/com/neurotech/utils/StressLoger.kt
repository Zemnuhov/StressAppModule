package com.neurotech.utils

import android.util.Log

object StressLogger {
    private const val TAG = "StressApp"

    private val lineNumber: Int
        get() = Thread.currentThread().stackTrace[4].lineNumber

    fun Any.log(message: String){
        Log.e( TAG, "${this.javaClass.canonicalName} line ${this.run { lineNumber }}: $message")
    }
}