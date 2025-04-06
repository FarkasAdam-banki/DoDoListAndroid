package com.example.dodolist.network

import TaskService
import service.LogService
import Network.InvitationService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

object RetrofitClient {
    private val cookieJar = CustomCookieJar()

    private val client = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://dodolist.hu/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    val instance: LoginService by lazy {
        retrofit.create(LoginService::class.java)
    }

    val taskService: TaskService by lazy {
        retrofit.create(TaskService::class.java)
    }

    val logService: LogService by lazy {
        retrofit.create(LogService::class.java)
    }


    val invitationService: InvitationService by lazy {
        retrofit.create(InvitationService::class.java)
    }

    fun getCookieJar(): CustomCookieJar {
        return cookieJar
    }
}
