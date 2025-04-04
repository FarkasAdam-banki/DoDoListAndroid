package com.example.dodolist.ui.theme

import Model.DeleteTaskResponse
import Model.TaskDetails
import adapter.StatusAdapter
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.dodolist.R
import com.example.dodolist.network.RetrofitClient
import com.example.dodolist.utils.getStatusColorClass
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskSettingsFragment : Fragment() {
    interface OnTaskDeletedListener {
        fun onTaskDeleted(taskId: Int)
    }

    var listener: OnTaskDeletedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnTaskDeletedListener) {
            listener = context
        } else {
            throw ClassCastException("$context must implement OnTaskDeletedListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    private lateinit var taskNameEditText: TextInputEditText
    private lateinit var dateEditText: TextInputEditText
    private lateinit var timeEditText: TextInputEditText
    private lateinit var statusSpinner: Spinner
    private lateinit var titleTextView: TextView
    private lateinit var deleteTaskButton: TextView
    private var taskId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_settings, container, false)

        taskNameEditText = view.findViewById(R.id.textInputEditText)
        dateEditText = view.findViewById(R.id.dateInputEditText)
        timeEditText = view.findViewById(R.id.timeInputEditText)
        statusSpinner = view.findViewById(R.id.statusSpinner)
        titleTextView = view.findViewById(R.id.titleTextView)
        deleteTaskButton = view.findViewById(R.id.deleteTask)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val taskDetails = arguments?.getParcelable<TaskDetails>("TASK_DETAILS")
        taskDetails?.let {
            updateTaskDetails(it)
            taskId = it.feladat_id
        }
        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedStatus = (position + 1).toString()
                statusSpinner.setBackgroundColor(getStatusColorClass(selectedStatus))
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        deleteTaskButton.setOnClickListener {
            taskId?.let {
                showDeleteConfirmationDialog(it)
            } ?: Toast.makeText(requireContext(), "Feladat ID nem található!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTaskDetails(taskDetails: TaskDetails) {
        taskNameEditText.setText(taskDetails.feladat_nev)
        titleTextView.text = "${taskDetails.feladat_nev} feladat beállításai"

        val dateTimeParts = taskDetails.feladat_hatarido?.split(" ") ?: listOf("", "")
        if (dateTimeParts.size == 2) {
            dateEditText.setText(dateTimeParts[0])
            timeEditText.setText(dateTimeParts[1])
        }else {
            dateEditText.setText("")
            timeEditText.setText("")
        }

        val adapter = StatusAdapter(requireContext())
        statusSpinner.adapter = adapter
        statusSpinner.setSelection(taskDetails.allapot_id - 1)

        statusSpinner.setBackgroundColor(getStatusColorClass(taskDetails.allapot_id.toString()))
    }

    private fun showDeleteConfirmationDialog(taskId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Feladat törlése")
            .setMessage("Biztosan törölni szeretnéd ezt a feladatot?")
            .setPositiveButton("Igen") { _, _ -> deleteTask(taskId) }
            .setNegativeButton("Nem", null)
            .show()
    }

    private fun deleteTask(taskId: Int) {
        RetrofitClient.taskService.deleteTask(taskId).enqueue(object : Callback<DeleteTaskResponse> {
            override fun onResponse(call: Call<DeleteTaskResponse>, response: Response<DeleteTaskResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.success != null) {
                        listener?.onTaskDeleted(taskId)
                        requireActivity().setResult(AppCompatActivity.RESULT_OK)
                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireContext(), result?.error ?: "Hiba történt", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<DeleteTaskResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Hálózati hiba!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
