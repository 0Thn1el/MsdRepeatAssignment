package com.example.msd_project

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        // Setup window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val listView: ListView = findViewById(R.id.listView)

        // Add "Font Size" as an option
        val listItems = arrayOf(
            "Themes",
            "Font Size"
        )

        val listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listView.adapter = listAdapter

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Handle ListView item clicks
        listView.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            when (selectedItem) {
                "Themes" -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main, ThemeFragment())
                        .addToBackStack(null)
                        .commit()
                }
                "Font Size" -> toggleFontSize()
                else -> Toast.makeText(this, "You clicked on: $selectedItem", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle BottomNavigationView item clicks
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
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

        // Apply saved font size preference when activity starts
        applyFontSize()
    }

    private fun toggleFontSize() {
        // Toggle font size preference
        val sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val isLargeFont = sharedPreferences.getBoolean("isLargeFont", false)
        sharedPreferences.edit().putBoolean("isLargeFont", !isLargeFont).apply()

        // Apply the new font size and notify the user
        applyFontSize()
        Toast.makeText(
            this,
            if (!isLargeFont) "Switched to Large Font Size" else "Switched to Normal Font Size",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun applyFontSize() {
        // Apply the font size preference
        val sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE)
        val isLargeFont = sharedPreferences.getBoolean("isLargeFont", false)
        val scale = if (isLargeFont) 1.3f else 1.0f
        val configuration = resources.configuration
        configuration.fontScale = scale
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}
