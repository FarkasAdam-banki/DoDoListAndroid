package com.example.dodolist.ui.theme
import Model.Task
import TaskAdapter
import TaskService
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.dodolist.R
import com.example.dodolist.network.RetrofitClient
import com.example.dodolist.ui.theme.JwtUtils
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TasksActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskService: TaskService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blockview)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(emptyList()) { task ->
            val intent = Intent(this, TaskSettingsActivity::class.java)
            intent.putExtra("TASK_ID", task.feladat_id)
            startActivity(intent)
        }
        recyclerView.adapter = taskAdapter

        val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://dodolist.hu/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()


        taskService = retrofit.create(TaskService::class.java)

        val authToken = RetrofitClient.getCookieJar().getAuthToken()
        if (authToken != null) {
            val email = JwtUtils.decodeJwt(authToken)
            if (email != null) {
                fetchTasks(email)
            } else {
                Log.e("JWT", "Nem sikerült dekódolni az emailt.")
            }
        } else {
            Log.e("JWT", "Nincs auth_token a sütiben.")
        }

    }

    private fun fetchTasks(email: String) {
        taskService.getUserTasks(email).enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                if (response.isSuccessful) {
                    response.body()?.let { taskList ->
                        taskAdapter.updateTasks(taskList)
                    }
                } else {
                    Toast.makeText(this@TasksActivity, "Hiba történt!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Task>>, t: Throwable) {
                Log.e("TaskActivity", "API hiba: ${t.message}")
                Toast.makeText(this@TasksActivity, "Sikertelen kapcsolat!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
