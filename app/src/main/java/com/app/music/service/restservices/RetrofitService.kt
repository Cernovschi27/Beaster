package com.app.music.service.restservices

import android.content.ContentValues
import android.util.Log
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val baseURL = "https://api.napster.com/"

object RetrofitService {

    val searchService: ISearchService by lazy {
        Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(initOkHttpClient())
            .build()
            .create(ISearchService::class.java)
    }
    private val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            Log.d(ContentValues.TAG, "OKHttp - $message")
        }
    }).apply {
        HttpLoggingInterceptor.Level.BODY
    }

    private fun initOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .build()
                chain.proceed(request)
            })
            .addNetworkInterceptor(loggingInterceptor)
            .build()
    }

}