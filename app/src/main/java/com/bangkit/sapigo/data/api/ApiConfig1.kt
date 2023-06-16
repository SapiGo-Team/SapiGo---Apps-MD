package com.bangkit.sapigo.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class ApiConfig1 {
    companion object {
        fun getInterfaceApi(): ApiService1 {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://backendsapigo-u7zm6m6lkq-et.a.run.app")
                .client(client)
                .build()

            return retrofit.create(ApiService1::class.java)
        }
    }
}
