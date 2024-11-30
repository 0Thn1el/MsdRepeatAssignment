package com.example.msd_project

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment

class ThemeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_theme, container, false)

        // Reference to the Switch in the layout
        val themeSwitch = view.findViewById<Switch>(R.id.themeSwitch)

        // Load saved theme preference
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("ThemePrefs", AppCompatActivity.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("isDarkMode", false)

        // Set the Switch state based on the saved preference
        themeSwitch.isChecked = isDarkMode

        // Handle theme switch toggling
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            // Save the theme preference
            sharedPreferences.edit().putBoolean("isDarkMode", isChecked).apply()
        }

        return view
    }
}

