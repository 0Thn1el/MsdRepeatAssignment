package com.example.msd_project

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.sqrt

class HomeFragment : Fragment(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var running = false
    private val stepDelay = 250
    private var stepCount = 0
    private var smoothedZAxis = 0f
    private val lowPassFilterFactor = 0.8f
    private var lastStepTime: Long = 0
    private val minStepThreshold = 0.6f
    private val maxStepThreshold = 1.5f

    // Views
    private lateinit var tvStepsTaken: TextView
    private lateinit var workoutTypeSpinner: Spinner
    private lateinit var durationInput: TextInputEditText
    private lateinit var distanceInput: TextInputEditText
    private lateinit var caloriesInput: TextInputEditText
    private lateinit var notesInput: TextInputEditText
    private lateinit var logWorkoutButton: Button
    private lateinit var recentWorkoutsList: RecyclerView

    private val workoutTypes = arrayOf("Running", "Cycling", "Weightlifting", "Swimming", "Yoga")
    private val recentWorkouts = mutableListOf<Workout>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        tvStepsTaken = view.findViewById(R.id.tv_stepsTaken)
        workoutTypeSpinner = view.findViewById(R.id.workoutTypeSpinner)
        durationInput = view.findViewById(R.id.durationInput)
        distanceInput = view.findViewById(R.id.distanceInput)
        caloriesInput = view.findViewById(R.id.caloriesInput)
        notesInput = view.findViewById(R.id.notesInput)
        logWorkoutButton = view.findViewById(R.id.logWorkoutButton)
        recentWorkoutsList = view.findViewById(R.id.recentWorkoutsList)

        // Setup workout type spinner
        workoutTypeSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            workoutTypes
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        // Setup RecyclerView
        recentWorkoutsList.layoutManager = LinearLayoutManager(requireContext())
        recentWorkoutsList.adapter = WorkoutAdapter(recentWorkouts)

        // Log workout button click
        logWorkoutButton.setOnClickListener {
            logWorkout()
        }

        // Initialize sensor manager
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (accelerometer == null) {
            Toast.makeText(requireContext(), "Accelerometer not available", Toast.LENGTH_LONG).show()
        }

        // Edge-to-edge handling
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        return view
    }

    private fun logWorkout() {
        if (durationInput.text.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please enter duration", Toast.LENGTH_SHORT).show()
            return
        }

        val workout = Workout(
            type = workoutTypeSpinner.selectedItem.toString(),
            duration = durationInput.text.toString().toInt(),
            distance = distanceInput.text?.toString()?.toFloatOrNull(),
            calories = caloriesInput.text?.toString()?.toIntOrNull(),
            notes = notesInput.text?.toString()
        )

        recentWorkouts.add(0, workout)
        recentWorkoutsList.adapter?.notifyDataSetChanged()

        // Clear inputs
        durationInput.text?.clear()
        distanceInput.text?.clear()
        caloriesInput.text?.clear()
        notesInput.text?.clear()

        Toast.makeText(requireContext(), "Workout logged!", Toast.LENGTH_SHORT).show()
    }

    // Sensor event listeners (keep your existing step counter implementation)
    override fun onResume() {
        super.onResume()
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            smoothedZAxis = lowPassFilterFactor * smoothedZAxis + (1 - lowPassFilterFactor) * event.values[2]
            val currentTime = System.currentTimeMillis()
            if (smoothedZAxis in minStepThreshold..maxStepThreshold &&
                (currentTime - lastStepTime) > stepDelay) {
                stepCount++
                tvStepsTaken.text = stepCount.toString()
                lastStepTime = currentTime
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Optional: Handle accuracy changes if needed
    }

    data class Workout(
        val type: String,
        val duration: Int,
        val distance: Float?,
        val calories: Int?,
        val notes: String?,
        val timestamp: Long = System.currentTimeMillis()
    )

    class WorkoutAdapter(private val workouts: List<Workout>) :
        RecyclerView.Adapter<WorkoutAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_workout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(workouts[position])
        }

        override fun getItemCount() = workouts.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(workout: Workout) {
                // Bind workout data to views
                itemView.findViewById<TextView>(R.id.workoutType).text = workout.type
                itemView.findViewById<TextView>(R.id.workoutDuration).text = "${workout.duration} min/s"
                workout.distance?.let {
                    itemView.findViewById<TextView>(R.id.workoutDistance).text = "%.2f km/s".format(it)
                }
                workout.calories?.let {
                    itemView.findViewById<TextView>(R.id.workoutCalories).text = "$it kcal/s"
                }
                workout.notes?.let {
                    itemView.findViewById<TextView>(R.id.workoutNotes).text = it
                }
            }
        }
    }
}