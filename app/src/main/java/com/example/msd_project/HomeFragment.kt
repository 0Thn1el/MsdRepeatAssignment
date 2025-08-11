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
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
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
    private lateinit var tvStepsTaken: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Setup window insets (edge-to-edge)
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        tvStepsTaken = view.findViewById(R.id.tv_stepsTaken)

        // Initialize sensor manager
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (accelerometer == null) {
            Toast.makeText(requireContext(), "Accelerometer not available", Toast.LENGTH_LONG).show()
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bottom navigation is now handled by the parent Activity
    }

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
            // Apply low-pass filter to Z-axis acceleration
            smoothedZAxis = lowPassFilterFactor * smoothedZAxis + (1 - lowPassFilterFactor) * event.values[2]

            val currentTime = System.currentTimeMillis()
            // Detect steps based on Z-axis acceleration and time delay
            if (smoothedZAxis in minStepThreshold..maxStepThreshold &&
                (currentTime - lastStepTime) > stepDelay) {
                stepCount++
                tvStepsTaken.text = "Steps: $stepCount"
                lastStepTime = currentTime
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Optional: Handle accuracy changes if needed
    }
}