package com.example.planmyworkout

import android.app.Activity
import android.os.Bundle
import android.util.Log

class RecommendActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_fetch)

        // DATA INPUTS
        // Temperature: -273.0 if not supplied
        // Duration: 0 if not supplied
        // Intensity: 0 if not supplied
        // Selected Muscles: [] if not supplied
        // Steptracker:
        // Sleep:

        Log.i("DATANOW", intent.extras?.getDouble("temperature").toString())
        Log.i("DATANOW", intent.extras?.getLong("sleep").toString())
        Log.i("DATANOW", intent.extras?.getLong("steps").toString())
        Log.i("DATANOW", intent.extras?.getInt("intensity").toString())
        Log.i("DATANOW", intent.extras?.getInt("duration").toString())
        Log.i("DATANOW", intent.extras?.getStringArray("selectedMuscles").toString())


    }
}