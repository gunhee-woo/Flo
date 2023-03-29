package com.example.flo.network

import com.example.flo.data.Song
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface FloService {

    @GET("song.json")
    suspend fun getSongInfo(): Response<Song>

    companion object {
        private const val BASE_URL = "https://grepp-programmers-challenges.s3.ap-northeast-2.amazonaws.com/2020-flo/"

        fun create(): FloService {
            val interceptor = FloInterceptor()

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FloService::class.java)

        }
    }
}