package com.example.flo.network

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber

class FloInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Timber.d("intercept")
        val request = chain.request().newBuilder().run {
            build()
        }
        return chain.proceed(request)
    }
}