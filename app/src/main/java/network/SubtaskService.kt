package com.example.dodolist.network

import com.example.dodolist.model.Subtask
import retrofit2.Response
import retrofit2.http.*

interface SubtaskService {

    @GET("api/subtask/")
    suspend fun getSubtasks(
        @Query("feladat_id") taskId: String,
        @Query("felhasznalo_email") email: String
    ): List<Subtask>

    @POST("api/subtask/")
    suspend fun createSubtask(
        @Body body: Map<String, String>
    ): Response<Unit>

    @PUT("api/subtask/")
    suspend fun updateSubtask(
        @Body body: Map<String, String>
    ): Response<Unit>

    @HTTP(method = "DELETE", path = "api/subtask/", hasBody = true)
    suspend fun deleteSubtask(
        @Body body: Map<String, String>
    ): Response<Unit>
}
