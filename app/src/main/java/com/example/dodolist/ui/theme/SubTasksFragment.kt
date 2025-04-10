package com.example.dodolist.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dodolist.R
import com.example.dodolist.adapter.SubtaskAdapter
import com.example.dodolist.adapter.SubtaskUpdateListener
import com.example.dodolist.databinding.FragmentSubtasksBinding
import com.example.dodolist.model.Subtask
import com.example.dodolist.network.RetrofitClient
import com.example.dodolist.ui.theme.JwtUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class SubTasksFragment : Fragment(), SubtaskUpdateListener {

    private var _binding: FragmentSubtasksBinding? = null
    private val binding get() = _binding!!

    private val subtaskService = RetrofitClient.subtaskService

    private val taskId: Int by lazy {
        arguments?.getInt("TASK_ID", -1) ?: -1
    }

    private var userEmail: String? = null

    private var subtasks: MutableList<Subtask> = mutableListOf()
    private lateinit var adapter: SubtaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubtasksBinding.inflate(inflater, container, false)
        getUserEmail()

        val fabAdd: FloatingActionButton = binding.subAdd
        fabAdd.setOnClickListener {
            createSubtask()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = SubtaskAdapter(
            subtasks,
            onDeleteClick = { subtask -> deleteSubtask(subtask.alfeladat_id) },
            updateListener = this
        )

        binding.subtaskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.subtaskRecyclerView.adapter = adapter

        loadSubtasks()
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

    override fun onSubtaskUpdate(subtask: Subtask) {
        lifecycleScope.launch {
            try {
                val updateMap = mapOf(
                    "feladat_id" to taskId.toString(),
                    "felhasznalo_email" to (userEmail ?: ""),
                    "alfeladat_id" to subtask.alfeladat_id,
                    "alfeladat_nev" to subtask.alfeladat_nev,
                    "alfeladat_leiras" to subtask.alfeladat_leiras,
                    "allapot_id" to subtask.allapot_id
                )
                subtaskService.updateSubtask(updateMap)
                loadSubtasks()
                Toast.makeText(requireContext(), "Módosítás elmentve", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Hiba frissítés közben", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteSubtask(subtaskId: String) {
        lifecycleScope.launch {
            try {
                subtaskService.deleteSubtask(
                    mapOf(
                        "feladat_id" to taskId.toString(),
                        "felhasznalo_email" to (userEmail ?: ""),
                        "alfeladat_id" to subtaskId
                    )
                )
                loadSubtasks()
                Toast.makeText(requireContext(), "Alfeladat törölve", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Hiba törlés közben", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createSubtask() {
        lifecycleScope.launch {
            try {
                if (taskId == -1 || userEmail.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "Hiba: érvénytelen adatok", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val newSubtask = mapOf(
                    "feladat_id" to taskId.toString(),
                    "felhasznalo_email" to (userEmail ?: ""),
                    "alfeladat_nev" to "Új alfeladat",
                    "alfeladat_leiras" to " ",
                    "allapot_id" to "1"
                )

                val response = subtaskService.createSubtask(newSubtask)

                if (response.isSuccessful) {
                    loadSubtasks()
                    Toast.makeText(requireContext(), "Alfeladat létrehozva", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Hiba történt az alfeladat létrehozásakor", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Hiba létrehozás közben", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadSubtasks() {
        lifecycleScope.launch {
            try {
                val result = subtaskService.getSubtasks(taskId.toString(), userEmail ?: "")
                adapter.updateSubtasks(result)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Hiba betöltéskor: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
