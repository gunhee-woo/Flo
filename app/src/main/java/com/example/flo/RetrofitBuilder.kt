package com.example.flo

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {
    private const val BASE_URL = "https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/"

    const val CONNECT_TIMEOUT: Long = 15
    const val WRITE_TIMEOUT: Long = 15
    const val READ_TIMEOUT: Long = 15

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClient())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val httpLoggingInterceptor = HttpLoggingInterceptor().also {
        it.level = HttpLoggingInterceptor.Level.BODY
    }

    private val mOkHttpClient = OkHttpClient().newBuilder().apply {
        addInterceptor(httpLoggingInterceptor)
        connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
        readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
    }.build()

    private val retrofit2: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(mOkHttpClient)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val floApi2: FloApi = retrofit2.create(FloApi::class.java)

    val floApi: FloApi = retrofit.create(FloApi::class.java)
}