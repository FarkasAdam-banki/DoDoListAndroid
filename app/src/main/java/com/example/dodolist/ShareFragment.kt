package com.example.dodolist.ui.fragments


import Model.InvitationRequest
import Model.UserItem
import adapter.ShareAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dodolist.R
import com.example.dodolist.network.RetrofitClient
import utils.JwtUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ShareFragment : Fragment() {

    private lateinit var emailInput: EditText
    private lateinit var shareButton: Button
    private lateinit var recyclerView: RecyclerView
    private val users = mutableListOf<UserItem>()
    private lateinit var adapter: ShareAdapter

    private val taskId: Int by lazy {
        arguments?.getInt("TASK_ID", -1) ?: -1
    }

    private var userEmail: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_share, container, false)
        emailInput = view.findViewById(R.id.email_input)
        shareButton = view.findViewById(R.id.share_button)
        recyclerView = view.findViewById(R.id.members_recycler_view)

        adapter = ShareAdapter(users) { user -> removeUser(user.felhasznalo_email) }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        shareButton.setOnClickListener {
            inviteUser()
        }

        getUserEmail()
        getUsers()
        return view
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

    private fun inviteUser() {
        val email = emailInput.text.toString().trim()
        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Hiányzó email!", Toast.LENGTH_SHORT).show()
            return
        }


        if (userEmail == null) {
            Toast.makeText(requireContext(), "Bejelentkezett felhasználó e-mailje nem elérhető.", Toast.LENGTH_SHORT).show()
            return
        }

        val request = InvitationRequest(action = "invite", feladat_id = taskId, email = email)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.invitationService.inviteUser(request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.error == null) {
                        Toast.makeText(requireContext(), "Sikeres meghívás!", Toast.LENGTH_SHORT).show()
                        emailInput.setText("")
                        getUsers()
                    } else {
                        Toast.makeText(requireContext(), response.body()?.error ?: "Ismeretlen hiba", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Hálózati hiba: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getUsers() {
        val request = InvitationRequest(action = "getUsers", feladat_id = taskId, email = userEmail)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.invitationService.getUsers(request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.users != null) {
                        users.clear()
                        users.addAll(response.body()!!.users ?: emptyList())
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(requireContext(), "Hiba a felhasználók lekérésénél", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Hálózati hiba: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun removeUser(email: String) {
        val request = InvitationRequest(action = "remove", feladat_id = taskId, email = email)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.invitationService.removeUser(request)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body()?.error == null) {
                        Toast.makeText(requireContext(), "Felhasználó törölve", Toast.LENGTH_SHORT).show()
                        getUsers()
                    } else {
                        Toast.makeText(requireContext(), response.body()?.error ?: "Törlési hiba", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Hiba kelektezett", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
