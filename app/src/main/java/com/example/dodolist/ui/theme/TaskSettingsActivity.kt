package com.example.dodolist.ui.theme

import Model.TaskDetails
import TaskService
import adapter.TaskPagerAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.dodolist.R
import com.example.dodolist.network.RetrofitClient
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskSettingsActivity : AppCompatActivity(),TaskSettingsFragment.OnTaskDeletedListener {

    private lateinit var taskService: TaskService
    private var taskDetails: TaskDetails? = null
    private var taskId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasksettings)

        taskId = intent.getIntExtra("TASK_ID", -1)
        if (taskId == -1) {
            Toast.makeText(this, "Érvénytelen feladat ID!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        taskService = RetrofitClient.taskService
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        viewPager.adapter = null

        fetchTaskDetails(viewPager, tabLayout)
    }

    private fun fetchTaskDetails(viewPager: ViewPager2, tabLayout: TabLayout) {
        taskService.getTaskById(taskId).enqueue(object : Callback<TaskDetails> {
            override fun onResponse(call: Call<TaskDetails>, response: Response<TaskDetails>) {
                if (response.isSuccessful) {
                    taskDetails = response.body()
                    val adapter = TaskPagerAdapter(this@TaskSettingsActivity, taskDetails)
                    viewPager.adapter = adapter
                    TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                        when (position) {
                            0 -> tab.text = "Feladat beállításai"
                            1 -> tab.text = "Feladat leírása"
                            2 -> tab.text = "Alfeladatok"
                            3 -> tab.text = "Megosztás"
                            4 -> tab.text = "Log"
                        }
                    }.attach()

                } else {
                    Toast.makeText(this@TaskSettingsActivity, "Hiba történt az adatok betöltésekor!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<TaskDetails>, t: Throwable) {
                Log.e("TaskSettingsActivity", "API hiba: ${t.message}")
                Toast.makeText(this@TaskSettingsActivity, "Nem sikerült lekérni az adatokat!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onTaskDeleted(taskId: Int) {
        Toast.makeText(this, "Feladat törölve!", Toast.LENGTH_SHORT).show()
        finish()
    }
}

