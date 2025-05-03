package com.example.dodolist.ui.fragments

import Model.UpdateTaskRequest
import Model.UpdateTaskResponse
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dodolist.R
import com.example.dodolist.network.RetrofitClient
import utils.JwtUtils
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TaskDescriptionFragment : Fragment() {

    private lateinit var descriptionEditText: TextInputEditText
    private var taskId: Int? = null
    private var userEmail: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_description, container, false)
        descriptionEditText = view.findViewById(R.id.descriptionInputEditText)
        arguments?.let {
            taskId = it.getInt("TASK_ID", -1)
            val taskDescription = it.getString("TASK_DESCRIPTION") ?: ""
            updateTaskDescription(taskDescription)
        }

        getUserEmail()
        return view
    }

    fun updateTaskDescription(description: String) {
        descriptionEditText.setText(description)
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

    private fun validateDescription(description: String): Boolean {
        return description.length <= 2000
    }

    private fun onDescriptionChange() {
        val newDescription = descriptionEditText.text?.toString() ?: ""
        if (!validateDescription(newDescription)) {
            Toast.makeText(requireContext(), "A leírás nem lehet üres vagy túl hosszú (max 500 karakter).", Toast.LENGTH_SHORT).show()
        } else {
            updateTaskField("feladat_leiras", newDescription)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        descriptionEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                onDescriptionChange()
            }
        }
    }
}