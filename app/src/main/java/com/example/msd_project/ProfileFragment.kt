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
import com.google.android.material.textfield.TextInputEditText

class ProfileFragment : Fragment() {
    private lateinit var sharedPreferences: SharedPreferences
    private var workoutsGoal = 3
    private var distanceGoal = 5.0f

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
        val workoutsGoalText = view.findViewById<TextView>(R.id.workoutsGoalText)
        val distanceGoalInput = view.findViewById<TextInputEditText>(R.id.distanceGoalInput)
        val decreaseWorkoutsButton = view.findViewById<Button>(R.id.decreaseWorkoutsButton)
        val increaseWorkoutsButton = view.findViewById<Button>(R.id.increaseWorkoutsButton)
        val saveGoalsButton = view.findViewById<Button>(R.id.saveGoalsButton)

        // Load user data
        loadUserData(view)
        loadGoals()

        workoutsGoalText.text = workoutsGoal.toString()
        distanceGoalInput.setText(distanceGoal.toString())

        // Handle button clicks
        decreaseWorkoutsButton.setOnClickListener {
            if (workoutsGoal > 1) {
                workoutsGoal--
                workoutsGoalText.text = workoutsGoal.toString()
            }
        }

        increaseWorkoutsButton.setOnClickListener {
            workoutsGoal++
            workoutsGoalText.text = workoutsGoal.toString()
        }

        saveGoalsButton.setOnClickListener {
            distanceGoal = distanceGoalInput.text.toString().toFloatOrNull() ?: 5.0f
            saveGoals()
            Toast.makeText(context, "Goals saved!", Toast.LENGTH_SHORT).show()
        }

        // Handle Edit Profile button click
        editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editUserFragment)
        }

        return view
    }


    private fun loadUserData(view: View) {
        val username = sharedPreferences.getString("username", "John Doe")
        val email = sharedPreferences.getString("email", "johndoe@example.com")

        view.findViewById<TextView>(R.id.username).text = username
        view.findViewById<TextView>(R.id.email).text = email
    }

    private fun loadGoals() {
        workoutsGoal = sharedPreferences.getInt("workouts_goal", 3)
        distanceGoal = sharedPreferences.getFloat("distance_goal", 5.0f)
    }

    private fun saveGoals() {
        sharedPreferences.edit()
            .putInt("workouts_goal", workoutsGoal)
            .putFloat("distance_goal", distanceGoal)
            .apply()
    }

    override fun onResume() {
        super.onResume()
        view?.let {
            loadUserData(it)
            loadGoals()
            it.findViewById<TextView>(R.id.workoutsGoalText)?.text = workoutsGoal.toString()
            it.findViewById<TextInputEditText>(R.id.distanceGoalInput)?.setText(distanceGoal.toString())
        }
    }

}