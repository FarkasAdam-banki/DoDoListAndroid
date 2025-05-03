package com.example.dodolist.ui.fragments

import Model.ApiResponse
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dodolist.R
import com.example.dodolist.network.RetrofitClient
import com.example.dodolist.ui.adapters.LogAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import service.LogService

class LogFragment : Fragment() {
    private lateinit var logService: LogService
    private var taskId: Int = -1
    private lateinit var logRecyclerView: RecyclerView
    private lateinit var logAdapter: LogAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_log, container, false)
        logRecyclerView = view.findViewById(R.id.logRecyclerView)
        logRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        logAdapter = LogAdapter(emptyList())
        logRecyclerView.adapter = logAdapter

        taskId = arguments?.getInt("TASK_ID") ?: -1
        logService = RetrofitClient.logService
        fetchLogs()
        return view
    }

    private fun fetchLogs() {
        if (taskId == -1) return

        logService.getLogsForTask(taskId).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    apiResponse?.data?.let { logs ->
                        logAdapter.updateLogs(logs)
                        Log.d("LogFragment", "Fetched logs: ${logs.size}")
                    }
                } else {
                    Log.e("LogFragment", "Error response: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.e("LogFragment", "Failed to fetch logs: ${t.message}")
            }
        })
    }
}
