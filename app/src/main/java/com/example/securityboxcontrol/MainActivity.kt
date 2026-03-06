package com.example.securityboxcontrol

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.Menu
import android.view.MenuInflater
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val bottomMenu = findViewById<BottomNavigationView>(R.id.bottomMenu)

        bottomMenu.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.home -> {
                    println("Home pressed")
                    true
                }

                R.id.help2 -> {
                    println("Help pressed")
                    true
                }

                R.id.help -> {
                    println("Info pressed")
                    true
                }

                else -> false
            }
        }
    }


    //implementar menu





}