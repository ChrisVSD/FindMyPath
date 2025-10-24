package com.example.findmypath.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    fun create(apiBaseUrl: String, apiKey: String? = null): RoutesApi {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        val clientBuilder = OkHttpClient.Builder().addInterceptor(logging)
        if (!apiKey.isNullOrBlank()) {
            clientBuilder.addInterceptor(Interceptor { chain ->
                val req = chain.request().newBuilder()
                req.addHeader("Authorization", "Bearer ${"$"}{apiKey}")
                chain.proceed(req.build())
            })
        }
        val client = clientBuilder.build()
        val retrofit = Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(RoutesApi::class.java)
    }
}
