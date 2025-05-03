package com.example.dodolist

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MenuActivity : AppCompatActivity() {

    private lateinit var menuFeladatok: CardView
    private lateinit var menuBeallitasok: CardView

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