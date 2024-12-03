package com.example.msd_project

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.math.sqrt

class HomeActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var running = false
    private val stepDelay = 250
    private var stepCount = 0
    private var smoothedZAxis = 0f
    private val lowPassFilterFactor = 0.8f
    private var lastStepTime: Long = 0
    private val minStepThreshold = 0.6f // Minimum Z-axis value for step detection
    private val maxStepThreshold = 1.5f // Maximum Z-axis value for step detection
    private lateinit var tvStepsTaken: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Setup window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        tvStepsTaken = findViewById(R.id.tv_stepsTaken)

        // Initialize sensor manager and check for the step counter sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (accelerometer == null) {
            Toast.makeText(this, "Accelerometer not available", Toast.LENGTH_LONG).show()
        }

        // Setup bottom navigation
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
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


    }

    override fun onResume() {
        super.onResume()
        // Register the accelerometer listener
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        // Unregister the accelerometer listener
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null) {
            // Apply low-pass filter to Z-axis acceleration
            smoothedZAxis = lowPassFilterFactor * smoothedZAxis + (1 - lowPassFilterFactor) * event.values[2]

            val currentTime = System.currentTimeMillis()
            // Detect steps based on Z-axis acceleration and time delay
            if (smoothedZAxis in minStepThreshold..maxStepThreshold && (currentTime - lastStepTime) > stepDelay) {
                stepCount++
                //stepCountTextView.text = "Steps: $stepCount"

                lastStepTime = currentTime // Update the last step time

            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
    }

}