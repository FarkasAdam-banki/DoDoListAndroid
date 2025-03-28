package com.example.dodolist.ui.theme

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dodolist.model.LoginData
import com.example.dodolist.model.LoginResponse
import com.example.dodolist.network.LoginService
import retrofit2.Call
import com.example.dodolist.R
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerText: TextView
    private val apiBaseUrl = "https://dodolist.hu/"
    private val apiService: LoginService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(LoginService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usernameInput = findViewById(R.id.usernameInput)
        passwordInput = findViewById(R.id.passwordInput)
        loginButton = findViewById(R.id.loginButton)
        registerText = findViewById(R.id.registerText)

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                login(username, password)
            } else {
                Toast.makeText(this, "Kérlek töltsd ki az összes mezőt!", Toast.LENGTH_SHORT).show()
            }
        }

        registerText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun login(username: String, password: String) {
        val loginData = LoginData(username, password)

        apiService.login(loginData).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse != null) {
                        if (loginResponse.error != null) {
                            Toast.makeText(this@MainActivity, loginResponse.error, Toast.LENGTH_SHORT).show()
                        } else {
                            val sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("user_username", username)
                            editor.apply()

                            Toast.makeText(this@MainActivity, "Sikeres bejelentkezés!", Toast.LENGTH_SHORT).show()

                            startActivity(intent)
                        }
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Hiba történt a bejelentkezés során", Toast.LENGTH_SHORT).show()
                    Log.e("LoginActivity", "Hibás válasz: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Hálózati hiba: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("LoginActivity", "Hiba a hálózati kérés során: ${t.message}")
            }
        })
    }
}
