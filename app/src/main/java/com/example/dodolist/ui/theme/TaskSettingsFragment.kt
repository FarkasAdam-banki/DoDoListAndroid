package com.example.dodolist.ui.theme

import Model.TaskDetails
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dodolist.R
import com.google.android.material.textfield.TextInputEditText


class TaskSettingsFragment : Fragment() {

    private lateinit var taskNameEditText: TextInputEditText
    private lateinit var dateEditText: TextInputEditText
    private lateinit var timeEditText: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_settings, container, false)

        val textInputLayout = view.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.textInputLayout)
        taskNameEditText = textInputLayout.findViewById(R.id.textInputEditText)
        dateEditText = view.findViewById(R.id.dateInputEditText)
        timeEditText = view.findViewById(R.id.timeInputEditText)

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val taskDetails = arguments?.getParcelable<TaskDetails>("TASK_DETAILS")
        taskDetails?.let {
            updateTaskDetails(it)
        }
    }

    fun updateTaskDetails(taskDetails: TaskDetails) {
        taskNameEditText.setText(taskDetails.feladat_nev)

        val dateTimeParts = taskDetails.feladat_hatarido.split(" ")
        if (dateTimeParts.size == 2) {
            dateEditText.setText(dateTimeParts[0])
            timeEditText.setText(dateTimeParts[1])
        }
    }
}

