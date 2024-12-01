package com.example.msd_project

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class EditUserFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_user, container, false)

        val usernameEditText = view.findViewById<EditText>(R.id.usernameEditText)
        val emailEditText = view.findViewById<EditText>(R.id.emailEditText)
        val saveButton = view.findViewById<Button>(R.id.saveButton)

        // Load saved data
        val sharedPreferences = requireContext().getSharedPreferences("UserSettings", Context.MODE_PRIVATE)
        usernameEditText.setText(sharedPreferences.getString("username", ""))
        emailEditText.setText(sharedPreferences.getString("email", ""))

        // Save updated data
        saveButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()

            if (username.isEmpty() || email.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sharedPreferences.edit().apply {
                putString("username", username)
                putString("email", email)
                apply()
            }

            Toast.makeText(requireContext(), "Details Updated Successfully", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
