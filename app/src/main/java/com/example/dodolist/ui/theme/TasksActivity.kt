package com.example.dodolist.ui.theme
import InvitationAdapter
import Model.Invitation
import Model.InvitationRequest
import Model.Task
import TaskAdapter
import TaskService
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TasksActivity : AppCompatActivity(),TaskSettingsFragment.OnTaskDeletedListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskService: TaskService
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private lateinit var invitationAdapter: InvitationAdapter
    private lateinit var recyclerViewInvitations: RecyclerView
    private lateinit var menu_icon: ImageView


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
        menu_icon = findViewById(R.id.menu_icon)
        menu_icon.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
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
            var email = JwtUtils.decodeJwt(authToken)
            if (email != null) {
                fetchTasks(email)
            } else {
                Log.e("JWT", "Nem sikerült dekódolni az emailt.")
            }
        } else {
            Log.e("JWT", "Nincs auth_token a sütiben.")
        }

        recyclerViewInvitations = findViewById(R.id.recyclerViewInvitations)
        recyclerViewInvitations.layoutManager = LinearLayoutManager(this)

        invitationAdapter = InvitationAdapter(emptyList(),
            onAccept = { invitation -> handleInvitation(invitation, "accept") },
            onReject = { invitation -> handleInvitation(invitation, "reject") }
        )
        recyclerViewInvitations.adapter = invitationAdapter

        fetchInvitations()


    }
    private fun fetchInvitations() {
        val authToken = RetrofitClient.getCookieJar().getAuthToken()
        if (authToken != null) {
            val email = JwtUtils.decodeJwt(authToken)
            if (email != null) {
                RetrofitClient.invitationService.checkInvites(email)
                    .enqueue(object : Callback<List<Invitation>> {
                        override fun onResponse(
                            call: Call<List<Invitation>>,
                            response: Response<List<Invitation>>
                        ) {
                            if (response.isSuccessful) {
                                val invites = response.body()

                                runOnUiThread {
                                    invitationAdapter.updateInvites(invites ?: emptyList())
                                }
                            } else {
                                Log.e("Invitations", "Hibás válasz: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<List<Invitation>>, t: Throwable) {
                            Log.e("Invitations", "Hálózati hiba: ${t.message}")
                        }
                    })
            } else {
                Log.e("JWT", "Nem sikerült dekódolni az emailt.")
            }
        } else {
            Log.e("JWT", "Nincs auth_token a sütiben.")
        }
    }




    private fun handleInvitation(invitation: Invitation, action: String) {
        val authToken = RetrofitClient.getCookieJar().getAuthToken()
        val email = authToken?.let { JwtUtils.decodeJwt(it) }
        if (email == null) {
            Log.e("InvitationAction", "Email nem található!")
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = InvitationRequest(
                    action = action,
                    feladat_id = invitation.feladat_id,
                    email = email
                )
                val response = when (action) {
                    "accept" -> RetrofitClient.invitationService.acceptInvite(request)
                    "reject" -> RetrofitClient.invitationService.rejectInvite(request)
                    else -> null
                }

                if (response?.isSuccessful == true) {
                    fetchInvitations()
                    fetchTasks(email);
                }

            } catch (e: Exception) {
                Log.e("InvitationAction", "Hiba: ${e.message}")
            }
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