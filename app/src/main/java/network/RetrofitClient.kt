package com.example.dodolist.network

import TaskService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

object RetrofitClient {
    private val cookieJar = CustomCookieJar()

    private val client = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .build()

    val instance: LoginService by lazy {
        Retrofit.Builder()
            .baseUrl("https://dodolist.hu/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(LoginService::class.java)
    }
    val taskService: TaskService by lazy {
        Retrofit.Builder()
            .baseUrl("https://dodolist.hu/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(TaskService::class.java)
    }

    fun getCookieJar(): CustomCookieJar {
        return cookieJar
    }
}
