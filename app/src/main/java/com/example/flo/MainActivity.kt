package com.example.flo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.flo.model.Sing
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val TAG = "" + this::class.java

    private val BASE_URL = "https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/2020-flo/song.json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Timber.plant(Timber.DebugTree())
        clickBtn.setOnClickListener {
//            baseHttpConnection()
//            useRetrofit()
            useRetrofit2()

        }
        Timber.d(TAG)
    }

    private fun useRetrofit() {
        val api = RetrofitBuilder.floApi
        api.getSingInfo().enqueue(object: retrofit2.Callback<Sing> {
            override fun onResponse(call: Call<Sing>, response: Response<Sing>) {
                val body = response.body()
                Timber.d(body.toString())
                body?.let {
                    Timber.d(it.singer)
                    testTv.text = it.singer
                }
            }

            override fun onFailure(call: Call<Sing>, t: Throwable) {
                Timber.d("fail ${t.localizedMessage}")
            }

        })
    }

    private fun useRetrofit2() {
        val api = RetrofitBuilder.floApi2
        api.getRxSingInfo()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnError {
                Timber.d(it.localizedMessage)
            }
            .unsubscribeOn(Schedulers.io())
            .onErrorReturn {
                Timber.d(it.localizedMessage)
                null
            }
            .subscribe { it ->
                Timber.d(it.singer)
                testTv.text = it.singer
            }

    }

    private fun baseHttpConnection() {
        thread(start = true) {
            try {
                val url = URL(BASE_URL)
                val urlConn = url.openConnection() as HttpURLConnection
                urlConn.requestMethod = "GET"

                if(urlConn.responseCode == HttpURLConnection.HTTP_OK) {
                    val streamReader = InputStreamReader(urlConn.inputStream)
                    val buffer = BufferedReader(streamReader)
                    val content = StringBuilder()
                    while(true) {
                        val line = buffer.readLine() ?: break
                        content.append(line + "\n")
                    }
                    Timber.d(content.toString())
                    buffer.close()
                    urlConn.disconnect()
                    runOnUiThread {
                        testTv.text = content.toString()
                    }
                } else {
                    Timber.d(urlConn.responseCode.toString())
                }
            } catch (e: Exception) {
                Timber.d(e.toString())
            }
        }
    }

}