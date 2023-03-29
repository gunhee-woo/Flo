package com.example.flo

import android.app.Application
import timber.log.Timber

class FloApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}