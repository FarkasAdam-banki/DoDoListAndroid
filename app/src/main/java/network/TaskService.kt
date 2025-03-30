import Model.Task
import Model.TaskDetails
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TaskService {
    @GET("dodo/task.php")
    fun getTaskById(@Query("taskId") taskId: Int): Call<TaskDetails>

    @GET("dodo/task.php")
    fun getUserTasks(@Query("userEmail") email: String): Call<List<Task>>
}
