package com.example.msd_project

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SettingsFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private var fontSizeChanged = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)

        setupListView(view)
        return view
    }

    private fun setupListView(view: View) {
        val listView = view.findViewById<ListView>(R.id.listView)
        listView.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            arrayOf("Themes", "Font Size")
        )

        listView.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> navigateToThemeFragment()
                1 -> toggleFontSize()
            }
        }
    }

    private fun navigateToThemeFragment() {
        findNavController().navigate(R.id.action_settingsFragment_to_themeFragment)
    }

    private fun toggleFontSize() {
        val isLargeFont = sharedPreferences.getBoolean("isLargeFont", false)
        sharedPreferences.edit().putBoolean("isLargeFont", !isLargeFont).apply()
        fontSizeChanged = true
        applyFontSize()
    }

    private fun applyFontSize() {
        if (!fontSizeChanged) return

        val isLargeFont = sharedPreferences.getBoolean("isLargeFont", false)
        val scale = if (isLargeFont) 1.3f else 1.0f

        activity?.apply {
            val config = resources.configuration
            if (config.fontScale != scale) {
                config.fontScale = scale
                val metrics = resources.displayMetrics
                resources.updateConfiguration(config, metrics)

                // Postpone recreation to avoid mid-transition changes
                view?.post {
                    recreate()
                    Toast.makeText(
                        context,
                        if (isLargeFont) "Large font enabled" else "Normal font enabled",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        if (fontSizeChanged) {
            resetFontScale()
        }
        super.onDestroyView()
    }

    private fun resetFontScale() {
        activity?.apply {
            val config = resources.configuration
            config.fontScale = 1.0f
            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }
}