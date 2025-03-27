package com.example.dodolist.network

import com.example.dodolist.model.RegisterData
import com.example.dodolist.model.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterService {

    @POST("dodo/register.php")
    fun register(@Body registerData: RegisterData): Call<RegisterResponse>
}
