package com.example.msd_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        // Apply edge-to-edge insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile -> true // Already in the profile activity
                R.id.home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Handle Edit Profile button click
        val editProfileButton = findViewById<Button>(R.id.editProfileButton)
        editProfileButton.setOnClickListener {
            // Load EditUserFragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.main, EditUserFragment())
                .addToBackStack(null) // Add to backstack to allow navigation back
                .commit()
        }


    }

    override fun onResume() {
        super.onResume()
        // Update displayed user data whenever the activity resumes
        loadUserData()
    }

    private fun loadUserData() {
        val sharedPreferences = getSharedPreferences("UserSettings", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "John Doe")
        val email = sharedPreferences.getString("email", "johndoe@example.com")

        findViewById<TextView>(R.id.username).text = username
        findViewById<TextView>(R.id.email).text = email
    }
}
