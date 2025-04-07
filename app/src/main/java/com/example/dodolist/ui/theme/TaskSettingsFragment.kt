package com.example.dodolist.ui.theme

import Model.DeleteTaskResponse
import Model.TaskDetails
import Model.UpdateTaskRequest
import Model.UpdateTaskResponse
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
    private var userEmail: String? = null
    private var originalTaskName: String = ""
    private var originalDate: String = ""
    private var originalTime: String = ""

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
        getUserEmail()
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
                updateTaskField("allapot_id", selectedStatus)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        deleteTaskButton.setOnClickListener {
            taskId?.let {
                showDeleteConfirmationDialog(it)
            } ?: Toast.makeText(requireContext(), "Feladat ID nem található!", Toast.LENGTH_SHORT).show()
        }
        taskNameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newTaskName = taskNameEditText.text?.toString() ?: ""
                if (newTaskName != originalTaskName) {
                    updateTaskField("feladat_nev", newTaskName)
                    originalTaskName = newTaskName
                    titleTextView.text = "${newTaskName} feladat beállításai"
                }
            }
        }
        dateEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newDate = dateEditText.text?.toString() ?: ""
                if (newDate != originalDate) {
                    if (!isValidDate(newDate)) {
                        Toast.makeText(requireContext(), "Hibás dátum formátum! A dátumnak YYYY-MM-DD formátumban kell lennie.", Toast.LENGTH_SHORT).show()
                        dateEditText.setText("")
                    } else {
                        updateDeadlineIfChanged()
                    }
                }
            }
        }

        timeEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val newTime = timeEditText.text?.toString() ?: ""
                if (newTime != originalTime) {
                    if (!isValidTime(newTime)) {
                        Toast.makeText(requireContext(), "Hibás idő formátum! Az időnek HH:MM:SS formátumban kell lennie.", Toast.LENGTH_SHORT).show()
                        timeEditText.setText("")
                    } else {
                        updateDeadlineIfChanged()
                    }
                }
            }
        }

    }

    private fun isValidDate(date: String): Boolean {
        val datePattern = Regex("^\\d{4}-\\d{2}-\\d{2}\$")
        return date.matches(datePattern)
    }

    private fun isValidTime(time: String): Boolean {
        val timePattern = Regex("^\\d{2}:\\d{2}:\\d{2}\$")
        return time.matches(timePattern)
    }

    private fun updateDeadlineIfChanged() {
        val newDate = dateEditText.text?.toString() ?: ""
        val newTime = timeEditText.text?.toString() ?: ""

        if (newDate.isNotBlank() && newTime.isNotBlank()) {
            if (!isValidDate(newDate)) {
                Toast.makeText(requireContext(), "Hibás dátum formátum! A dátumnak YYYY-MM-DD formátumban kell lennie.", Toast.LENGTH_SHORT).show()
                dateEditText.setText("")
                return
            }
            if (!isValidTime(newTime)) {
                Toast.makeText(requireContext(), "Hibás idő formátum! Az időnek HH:MM:SS formátumban kell lennie.", Toast.LENGTH_SHORT).show()
                timeEditText.setText("")
                return
            }
            if (newDate != originalDate || newTime != originalTime) {
                updateTaskField("feladat_hatarido", "$newDate $newTime")
                originalDate = newDate
                originalTime = newTime
            }
        }
    }

    private fun updateTaskDetails(taskDetails: TaskDetails) {
        originalTaskName = taskDetails.feladat_nev
        taskNameEditText.setText(originalTaskName)
        titleTextView.text = "${originalTaskName} feladat beállításai"

        val dateTimeParts = taskDetails.feladat_hatarido?.split(" ") ?: listOf("", "")
        if (dateTimeParts.size == 2) {
            originalDate = dateTimeParts[0]
            originalTime = dateTimeParts[1]
            dateEditText.setText(originalDate)
            timeEditText.setText(originalTime)
        } else {
            originalDate = ""
            originalTime = ""
            dateEditText.setText("")
            timeEditText.setText("")
        }

        val adapter = StatusAdapter(requireContext())
        statusSpinner.adapter = adapter
        statusSpinner.setSelection(taskDetails.allapot_id - 1)

        statusSpinner.setBackgroundColor(getStatusColorClass(taskDetails.allapot_id.toString()))
    }

    private fun getUserEmail() {
        val authToken = RetrofitClient.getCookieJar().getAuthToken()
        if (authToken != null) {
            val email = JwtUtils.decodeJwt(authToken)
            if (email != null) {
                userEmail = email
            } else {
                Toast.makeText(requireContext(), "Hiba: Nem sikerült dekódolni az e-mailt.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTaskField(field: String, value: String) {
        val request = UpdateTaskRequest(
            feladat_id = taskId ?: return,
            mezo = field,
            uj_ertek = value,
            felhasznalo_email = userEmail ?: return
        )

        RetrofitClient.taskService.updateTask(request).enqueue(object : Callback<UpdateTaskResponse> {
            override fun onResponse(call: Call<UpdateTaskResponse>, response: Response<UpdateTaskResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.success != null) {
                        Toast.makeText(requireContext(), "Sikeres módosítás!", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(requireContext(), result?.error ?: "Ismeretlen hiba", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Szerver válasz sikertelen!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpdateTaskResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Hálózati hiba!", Toast.LENGTH_SHORT).show()
            }
        })
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