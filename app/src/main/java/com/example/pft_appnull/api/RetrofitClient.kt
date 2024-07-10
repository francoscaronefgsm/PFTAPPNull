package com.example.pft_appnull.api

import com.example.pft_appnull.App
import com.example.pft_appnull.utils.PreferenceHelper
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                val token = PreferenceHelper.getToken(App.context) // Obtén el token

                if (token != null) {
                    requestBuilder.addHeader("Authorization", "Bearer $token") // Agrega el token JWT
                }

                chain.proceed(requestBuilder.build())
            }
            .followRedirects(false)
            .followSslRedirects(false)
            .build()
    }

    private val gson: Gson by lazy {
        GsonBuilder()
            .setLenient()
            .create()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create()) // Para manejar respuestas de texto
            .addConverterFactory(GsonConverterFactory.create(gson)) // Para manejar respuestas JSON
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}