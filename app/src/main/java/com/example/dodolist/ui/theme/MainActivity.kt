package com.example.dodolist.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dodolist.R
import com.example.dodolist.model.LoginData
import com.example.dodolist.model.LoginResponse
import com.example.dodolist.network.RetrofitClient
import com.example.dodolist.ui.theme.RegisterActivity
import com.example.dodolist.ui.theme.JwtUtils
import com.example.dodolist.ui.theme.TasksActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var registerText: TextView

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

        RetrofitClient.instance.login(loginData).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.error != null) {
                        Toast.makeText(this@MainActivity, loginResponse.error, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@MainActivity, "Sikeres bejelentkezés!", Toast.LENGTH_SHORT).show()

                        val authToken = RetrofitClient.getCookieJar().getAuthToken()
                        if (authToken != null) {
                            val email = JwtUtils.decodeJwt(authToken)
                            if (email == null) {
                                Log.e("JWT", "Nem sikerült dekódolni az emailt.")
                            }else{
                                val intent = Intent(this@MainActivity, TasksActivity::class.java)
                                startActivity(intent)

                            }
                        } else {
                            Log.e("JWT", "Nincs auth_token a sütiben.")
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
