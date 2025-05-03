package com.example.dodolist

import Model.UpdatePasswordRequest
import Model.UpdatePasswordResponse
import Model.UpdateUsernameRequest
import Model.UpdateUsernameResponse
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.dodolist.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import utils.JwtUtils

class ProfileSettingsActivity : AppCompatActivity() {

    private lateinit var menu_icon: ImageView
    private lateinit var etUsername: EditText
    private lateinit var btnSaveUsername: Button

    private lateinit var etCurrentPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmNewPassword: EditText
    private lateinit var btnSavePassword: Button

    private var userEmail: String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_settings)
        getUserEmail()

        menu_icon = findViewById(R.id.menu_icon)
        menu_icon.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        etUsername = findViewById(R.id.et_username)
        btnSaveUsername = findViewById(R.id.btn_save_username)

        etCurrentPassword = findViewById(R.id.et_current_password)
        etNewPassword = findViewById(R.id.et_new_password)
        etConfirmNewPassword = findViewById(R.id.et_confirm_new_password)
        btnSavePassword = findViewById(R.id.btn_save_password)

        btnSaveUsername.setOnClickListener {
            val username = etUsername.text.toString().trim()
            if (username.isEmpty()) {
                etUsername.error = "Felhasználónév nem lehet üres"
                return@setOnClickListener
            }
            if (!isUsernameValid(username)) {
                etUsername.error = "A felhasználónév csak betűket, számokat és aláhúzásjeleket tartalmazhat (3-25 karakter)."
                return@setOnClickListener
            }
            updateUsername(username)
        }

        btnSavePassword.setOnClickListener {
            val current = etCurrentPassword.text.toString()
            val new = etNewPassword.text.toString()
            val confirm = etConfirmNewPassword.text.toString()

            if (current.isEmpty() || new.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Minden mezőt ki kell tölteni", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!new.matches(Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$"))) {
                Toast.makeText(
                    this,
                    "A jelszónak legalább 8 karakter hosszúnak kell lennie, tartalmaznia kell egy nagybetűt és egy számot.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (new != confirm) {
                etConfirmNewPassword.error = "A jelszavak nem egyeznek"
                return@setOnClickListener
            }

            updatePassword(current, new)
        }
    }
    private fun getUserEmail() {
        val authToken = RetrofitClient.getCookieJar().getAuthToken()
        if (authToken != null) {
            val email = JwtUtils.decodeJwt(authToken)
            if (email != null) {
                userEmail = email
            } else {
                Toast.makeText(
                    this,
                    "Hiba: Nem sikerült dekódolni az e-mailt.",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }
    private fun isUsernameValid(username: String): Boolean {
        return Regex("^[a-zA-Z0-9_]{3,25}$").matches(username)
    }


    private fun updateUsername(username: String) {
        val request = UpdateUsernameRequest(
            action = "change_username",
            email = userEmail,
            new_username = username
        )

        RetrofitClient.profileService.updateUsername(request).enqueue(object : Callback<UpdateUsernameResponse> {
            override fun onResponse(
                call: Call<UpdateUsernameResponse>,
                response: Response<UpdateUsernameResponse>
            ) {
                val res = response.body()
                if (response.isSuccessful && res?.success == "Felhasználónév sikeresen frissítve.") {
                    Toast.makeText(this@ProfileSettingsActivity, "Felhasználónév frissítve", Toast.LENGTH_SHORT).show()
                    etUsername.setText("");
                } else {
                    Toast.makeText(
                        this@ProfileSettingsActivity,
                        res?.error ?: "Hiba történt",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UpdateUsernameResponse>, t: Throwable) {
                Toast.makeText(this@ProfileSettingsActivity, "Hálózati hiba: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updatePassword(current: String, new: String) {
        val request = UpdatePasswordRequest(
            action = "change_password_email",
            email = userEmail,
            old_password = current,
            new_password = new
        )

        RetrofitClient.profileService.updatePassword(request).enqueue(object : Callback<UpdatePasswordResponse> {
            override fun onResponse(
                call: Call<UpdatePasswordResponse>,
                response: Response<UpdatePasswordResponse>
            ) {
                val res = response.body()
                if (response.isSuccessful && res?.success == "Jelszó sikeresen frissítve.") {
                    Toast.makeText(this@ProfileSettingsActivity, "Jelszó sikeresen módosítva", Toast.LENGTH_SHORT).show()
                    etNewPassword.setText("")
                    etCurrentPassword.setText("")
                    etConfirmNewPassword.setText("");
                } else {
                    Toast.makeText(
                        this@ProfileSettingsActivity,
                        res?.error ?: "Hiba történt",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<UpdatePasswordResponse>, t: Throwable) {
                Toast.makeText(this@ProfileSettingsActivity, "Hálózati hiba: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
