package com.example.dodolist.ui.theme
import Model.Task
import TaskAdapter
import TaskService
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.dodolist.R
import com.example.dodolist.network.RetrofitClient
import com.example.dodolist.ui.theme.JwtUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TasksActivity : AppCompatActivity(),TaskSettingsFragment.OnTaskDeletedListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskService: TaskService
    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blockview)

        val fragment = supportFragmentManager.findFragmentByTag(TaskSettingsFragment::class.java.simpleName) as? TaskSettingsFragment
        fragment?.listener = this

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val deletedId = result.data?.getIntExtra("DELETED_TASK_ID", -1) ?: -1
                if (deletedId != -1) {
                    Log.d("TasksActivity", "ActivityResult: taskId = $deletedId")
                    onTaskDeleted(deletedId)
                }
            }
        }

        val fabAdd: FloatingActionButton = findViewById(R.id.fab_add)
        fabAdd.setOnClickListener {
            showAddTaskDialog()
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(emptyList()) { task ->
            val intent = Intent(this, TaskSettingsActivity::class.java)
            intent.putExtra("TASK_ID", task.feladat_id)
            launcher.launch(intent)
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

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val editTextTaskName = dialogView.findViewById<EditText>(R.id.editTextTaskName)
        val buttonCreateTask = dialogView.findViewById<Button>(R.id.buttonCreateTask)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        buttonCreateTask.setOnClickListener {
            val taskName = editTextTaskName.text.toString().trim()
            if (taskName.isNotEmpty()) {
                val authToken = RetrofitClient.getCookieJar().getAuthToken()
                if (authToken != null) {
                    val email = JwtUtils.decodeJwt(authToken)
                    if (email != null) {
                        createTask(taskName, email, dialog)
                    } else {
                        Toast.makeText(this, "Hiba: Nem sikerült dekódolni az e-mailt.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Hiba: Nincs auth token.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Adj meg egy feladatnevet!", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun createTask(taskName: String, email: String, dialog: AlertDialog) {
        val taskData: Map<String, String> = mapOf(
            "feladat_nev" to taskName,
            "felhasznalo_email" to email
        )

        taskService.createTask(taskData).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@TasksActivity, "Feladat létrehozva: $taskName", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    fetchTasks(email)
                } else {
                    Toast.makeText(this@TasksActivity, "Hiba történt a feladat létrehozásakor!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@TasksActivity, "Sikertelen kapcsolat!", Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onTaskDeleted(taskId: Int) {
        taskAdapter.removeTaskById(taskId)
        Toast.makeText(this, "Feladat Törölve!", Toast.LENGTH_SHORT).show()
    }



    private fun fetchTasks(email: String) {
        taskService.getUserTasks(email).enqueue(object : Callback<List<Task>> {
            override fun onResponse(call: Call<List<Task>>, response: Response<List<Task>>) {
                if (response.isSuccessful) {
                    response.body()?.let { taskList ->
                        taskAdapter.updateTasks(taskList)                    }
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