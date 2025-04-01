package com.example.dodolist.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.dodolist.R
import com.google.android.material.textfield.TextInputEditText

class TaskDescriptionFragment : Fragment() {

    private lateinit var descriptionEditText: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_description, container, false)
        descriptionEditText = view.findViewById(R.id.descriptionInputEditText)
        arguments?.getString("TASK_DESCRIPTION")?.let {
            updateTaskDescription(it)
        }

        return view
    }

    fun updateTaskDescription(description: String) {
        descriptionEditText.setText(description)
    }
}
