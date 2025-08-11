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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout first
        return inflater.inflate(R.layout.fragment_edit_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireActivity().getSharedPreferences("UserSettings", Context.MODE_PRIVATE)

        // Load current values
        view.findViewById<EditText>(R.id.usernameEditText).setText(
            sharedPreferences.getString("username", "")
        )
        view.findViewById<EditText>(R.id.emailEditText).setText(
            sharedPreferences.getString("email", "")
        )

        view.findViewById<Button>(R.id.saveButton).setOnClickListener {
            val username = view.findViewById<EditText>(R.id.usernameEditText).text.toString()
            val email = view.findViewById<EditText>(R.id.emailEditText).text.toString()

            if (username.isBlank() || email.isBlank()) {
                Toast.makeText(requireContext(), "All fields required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            sharedPreferences.edit()
                .putString("username", username)
                .putString("email", email)
                .apply()

            Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show()

            // Navigate back
            parentFragmentManager.popBackStack()
        }
    }
}
