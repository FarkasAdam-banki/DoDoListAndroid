package com.example.dodolist.ui.theme

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dodolist.model.RegisterData
import com.example.dodolist.model.RegisterResponse
import com.example.dodolist.network.RegisterService
import com.example.dodolist.R
import com.example.dodolist.ui.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegisterActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var secondPasswordInput: EditText
    private lateinit var registerButton: Button

    private val apiBaseUrl = "https://dodolist.hu/"
    private val apiService: RegisterService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(RegisterService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        usernameInput = findViewById(R.id.usernameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        secondPasswordInput = findViewById(R.id.secondPasswordInput)
        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val password2 = secondPasswordInput.text.toString().trim()

            if (!validateInputs(username, email, password, password2)) {
                return@setOnClickListener
            }

            register(username, email, password, password2)
        }
    }

    private fun validateInputs(username: String, email: String, password: String, password2: String): Boolean {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty()) {
            showToast("Minden mezőt ki kell tölteni.")
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Érvénytelen email cím.")
            return false
        }

        if (!username.matches(Regex("^[a-zA-Z0-9_]{3,20}$"))) {
            showToast("A felhasználónév 3-20 karakter hosszú lehet, és csak betűket, számokat vagy aláhúzást tartalmazhat.")
            return false
        }

        if (!password.matches(Regex("^(?=.*[A-Z])(?=.*\\d).{8,}$"))) {
            showToast("A jelszónak legalább 8 karakter hosszúnak kell lennie, tartalmaznia kell egy nagybetűt és egy számot.")
            return false
        }

        if (password != password2) {
            showToast("A jelszavak nem egyeznek.")
            return false
        }

        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun register(username: String, email: String, password: String, password2: String) {
        val registerData = RegisterData(username, email, password, password2)

        apiService.register(registerData).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse != null) {
                        if (registerResponse.error != null) {
                            showToast(registerResponse.error)
                        } else {
                            showToast(registerResponse.message ?: "Sikeres regisztráció!")
                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                } else {
                    showToast("Hiba történt a regisztráció során")
                    Log.e("RegisterActivity", "Hibás válasz: \${response.code()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                showToast("Hálózati hiba: \${t.message}")
                Log.e("RegisterActivity", "Hiba a hálózati kérés során: \${t.message}")
            }
        })
    }
}
