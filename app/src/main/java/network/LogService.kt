package service

import Model.ApiResponse
import Model.LogEntry
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LogService {
    @GET("api/log/")
    fun getLogsForTask(@Query("feladat_id") taskId: Int): Call<ApiResponse>
}