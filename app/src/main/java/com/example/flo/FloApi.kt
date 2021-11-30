package com.example.flo

import com.example.flo.model.Sing
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET

interface FloApi {
    @GET("2020-flo/song.json")
    fun getSingInfo(): Call<Sing>

    @GET("2020-flo/song.json")
    fun getRxSingInfo(): Single<Sing>
}