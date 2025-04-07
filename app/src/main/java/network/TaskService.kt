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
    @GET("dodo/task.php")
    fun getTaskById(@Query("taskId") taskId: Int): Call<TaskDetails>

    @GET("dodo/task.php")
    fun getUserTasks(@Query("userEmail") email: String): Call<List<Task>>

    @POST("dodo/task.php")
    fun createTask(@Body taskData: Map<String, String>): Call<Void>

    @DELETE("dodo/task.php")
    fun deleteTask(@Query("taskId") taskId: Int): Call<DeleteTaskResponse>

    @PUT("dodo/task.php")
    fun updateTask(@Body updateData: UpdateTaskRequest): Call<UpdateTaskResponse>
}
