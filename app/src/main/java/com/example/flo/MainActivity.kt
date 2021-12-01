package com.example.flo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.flo.model.Sing
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
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
            baseHttpConnection()
//            useRetrofit()
//            useRetrofit2()

        }
        Timber.d(TAG)
    }

    private fun useRetrofit() {
        RetrofitBuilder.floApi.getSingInfo().enqueue(object: retrofit2.Callback<Sing> {
            override fun onResponse(call: Call<Sing>, response: Response<Sing>) {
                val body = response.body()
                body?.let {
                    Timber.d(it.toString())
                    testTv.text = it.singer
                    it.setImageByUrl(this@MainActivity, singIv)
                }
            }

            override fun onFailure(call: Call<Sing>, t: Throwable) {
                Timber.d("fail ${t.localizedMessage}")
            }
        })
    }

    private fun useRetrofit2() {
        RetrofitBuilder.floApi2.getRxSingInfo()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                Timber.d("onNext ${it.singer}")
            },  {
                Timber.d("onError ${it.localizedMessage}")
            },  {
                Timber.d("onComplete")
            })
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
//                    runOnUiThread {
//                        testTv.text = content.toString()
//                    }
                    baseParsing(content.toString())
                } else {
                    Timber.d(urlConn.responseCode.toString())
                }
            } catch (e: Exception) {
                Timber.d(e.toString())
            }
        }
    }

    private fun baseParsing(content: String): List<Sing> {
        val singList = ArrayList<Sing>()
        try {
            val jsonObject = JSONObject(content)
            val singer = jsonObject.getString("singer")
            val album = jsonObject.getString("album")
            val title = jsonObject.getString("title")
            val duration = jsonObject.getInt("duration")
            val image = jsonObject.getString("image")
            val file = jsonObject.getString("file")
            val lyrics = jsonObject.getString("lyrics")
            val sing = Sing(singer, album, title, duration, image, file, lyrics)
            Timber.d("sing : ${sing.toString()}")
            singList.add(sing)
        } catch (e: Exception) {
            Timber.d("error : $e")
        }
        return singList
    }

}