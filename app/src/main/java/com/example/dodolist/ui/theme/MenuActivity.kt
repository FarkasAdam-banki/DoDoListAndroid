package com.example.dodolist.ui.theme

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.dodolist.R

class MenuActivity : AppCompatActivity() {

    private lateinit var menuFeladatok: LinearLayout
    private lateinit var menuBeallitasok: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        menuFeladatok = findViewById(R.id.menuFeladatok)
        menuFeladatok.setOnClickListener {
            val intent = Intent(this, TasksActivity::class.java)
            startActivity(intent)
        }
        menuBeallitasok = findViewById(R.id.menuBeallitasok)
        menuBeallitasok.setOnClickListener {
            val intent = Intent(this, ProfileSettingsActivity::class.java)
            startActivity(intent)
        }





    }
}