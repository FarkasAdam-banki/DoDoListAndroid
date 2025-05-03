package com.example.dodolist.network

import com.example.dodolist.model.LoginData
import com.example.dodolist.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {

    @POST("api/login/")
    fun login(@Body loginData: LoginData): Call<LoginResponse>
}
