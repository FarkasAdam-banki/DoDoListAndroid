package com.example.dodolist.network

import com.example.dodolist.model.Subtask
import retrofit2.Response
import retrofit2.http.*

interface SubtaskService {

    @GET("dodo/subtask.php")
    suspend fun getSubtasks(
        @Query("feladat_id") taskId: String,
        @Query("felhasznalo_email") email: String
    ): List<Subtask>

    @POST("dodo/subtask.php")
    suspend fun createSubtask(
        @Body body: Map<String, String>
    ): Response<Unit>

    @PUT("dodo/subtask.php")
    suspend fun updateSubtask(
        @Body body: Map<String, String>
    ): Response<Unit>

    @HTTP(method = "DELETE", path = "dodo/subtask.php", hasBody = true)
    suspend fun deleteSubtask(
        @Body body: Map<String, String>
    ): Response<Unit>
}
