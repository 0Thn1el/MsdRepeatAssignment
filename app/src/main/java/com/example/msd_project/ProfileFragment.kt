package com.example.msd_project

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class ProfileFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("UserSettings", Context.MODE_PRIVATE)

        // Initialize views
        val editProfileButton = view.findViewById<Button>(R.id.editProfileButton)
        val profileImage = view.findViewById<ImageView>(R.id.profileImage)

        // Load user data
        loadUserData(view)

        // Handle Edit Profile button click
        editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editUserFragment)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Update displayed user data whenever fragment resumes
        view?.let { loadUserData(it) }
    }

    private fun loadUserData(view: View) {
        val username = sharedPreferences.getString("username", "John Doe")
        val email = sharedPreferences.getString("email", "johndoe@example.com")

        view.findViewById<TextView>(R.id.username).text = username
        view.findViewById<TextView>(R.id.email).text = email
    }
}