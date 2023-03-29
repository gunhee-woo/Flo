package com.example.flo.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.flo.R
import com.example.flo.network.FloService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val test = findViewById<Button>(R.id.test)
        test.setOnClickListener {
            GlobalScope.launch {
                val service = FloService.create().getSongInfo()
                if(service.isSuccessful) {
                    Timber.d(service.body()?.album)
                }
            }
        }
    }
}