package com.example.dodolist.network


import Model.UpdatePasswordRequest
import Model.UpdatePasswordResponse
import Model.UpdateUsernameRequest
import Model.UpdateUsernameResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ProfileService {
    @POST("/api/updateUserDatas/")
    fun updateUsername(@Body request: UpdateUsernameRequest): Call<UpdateUsernameResponse>

    @POST("/api/updateUserDatas/")
    fun updatePassword(@Body request: UpdatePasswordRequest): Call<UpdatePasswordResponse>
}