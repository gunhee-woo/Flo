package com.example.flo

import android.text.TextUtils

object App {

    @JvmStatic
    fun Function(): String {
        val stackTraceElements = Throwable().stackTrace
        val splitter: TextUtils.StringSplitter = TextUtils.SimpleStringSplitter('.')
        splitter.setString(stackTraceElements[1].fileName)
        return splitter.iterator().next().toString() + "." + stackTraceElements[1].methodName + "()"
    }
}