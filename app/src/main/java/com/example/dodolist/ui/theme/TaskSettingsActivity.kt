package com.example.dodolist.ui.theme

import Model.TaskDetails
import TaskService
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dodolist.R
import com.example.dodolist.network.RetrofitClient
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskSettingsActivity : AppCompatActivity() {
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var taskService: TaskService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasksettings)

        textInputLayout = findViewById(R.id.textInputLayout)

        val taskId = intent.getIntExtra("TASK_ID", -1)
        if (taskId != -1) {
            fetchTaskDetails(taskId)
        } else {
            Toast.makeText(this, "Érvénytelen feladat ID!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchTaskDetails(taskId: Int) {
        taskService = RetrofitClient.taskService
        taskService.getTaskById(taskId).enqueue(object : Callback<TaskDetails> {
            override fun onResponse(call: Call<TaskDetails>, response: Response<TaskDetails>) {
                if (response.isSuccessful) {
                    val task = response.body()
                    if (task != null) {
                        textInputLayout.editText?.setText(task.feladat_nev)
                    } else {
                        Toast.makeText(this@TaskSettingsActivity, "A feladat adatai üresek!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("API", "Hiba a válaszban: ${response.code()} - ${response.message()}")
                    try {
                        val responseBody = response.errorBody()?.string()
                        Log.e("API", "Válasz szövege: $responseBody")
                    } catch (e: Exception) {
                        Log.e("API", "Hiba a válasz szövegének naplózása közben: ${e.message}")
                    }
                    Toast.makeText(this@TaskSettingsActivity, "Hiba a feladat lekérésében: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TaskDetails>, t: Throwable) {
                Log.e("API", "API hiba: ${t.message}")
                Toast.makeText(this@TaskSettingsActivity, "API hiba: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
