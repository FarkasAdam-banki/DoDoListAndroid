import Model.DeleteTaskResponse
import Model.Task
import Model.TaskDetails
import Model.UpdateTaskRequest
import Model.UpdateTaskResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query


interface TaskService {
    @GET("api/task/")
    fun getTaskById(@Query("taskId") taskId: Int): Call<TaskDetails>

    @GET("api/task/")
    fun getUserTasks(@Query("userEmail") email: String): Call<List<Task>>

    @POST("api/task/")
    fun createTask(@Body taskData: Map<String, String>): Call<Void>

    @DELETE("api/task/")
    fun deleteTask(@Query("taskId") taskId: Int): Call<DeleteTaskResponse>

    @PUT("api/task/")
    fun updateTask(@Body updateData: UpdateTaskRequest): Call<UpdateTaskResponse>
}
