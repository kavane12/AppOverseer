package com.example.planmyworkout

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.fitness.data.WorkoutExercises
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.math.*

class RecommendActivity : Activity() {
    val user = FirebaseAuth.getInstance().currentUser
    val db = Firebase.firestore

    private var temperature: Double = -273.0
    private var sleep: Double = -1.0
    private var steps: Long = -1
    private var intensity: Int = 5
    private var duration: Int = 60
    private var muscles : List<String> = ArrayList()

    private var listOfExercises = mutableSetOf<String>()
    private var finalIntensity: Double = 5.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_fetch)

        // DATA INPUTS DEFAULTS
        // Temperature: -273.0
        // Duration: 60
        // Intensity: 5
        // Selected Muscles: null
        // Steptracker: -1
        // Sleep: -1

        temperature = intent.extras?.getDouble("temperature")!!
        if (temperature <= -273.0) {
            temperature = 20.0
        }

        sleep = intent.extras?.getDouble("sleep")!!
        if (sleep < 0) {
            sleep = 8.0
        }

        steps = intent.extras?.getLong("steps")!!
        if (steps < 0) {
            steps = 3000
        }

        // Set intensity, otherwise default to 5
        val intentIntensity = intent.extras?.getInt("intensity")
        if (intentIntensity == null || intentIntensity == 0) {
            intensity = 5
        } else {
            intensity = intentIntensity
        }

        // Set duration, otherwise default to 1 hr
        val intentDuration = intent.extras?.getInt("duration")
        if (intentDuration == null || intentDuration == 0) {
            duration = 60
        } else {
            duration = intentDuration
        }

        // MUSCLES TO FOCUS ON
        val selectedMuscles = intent.extras?.getStringArray("selectedMuscles")

        if (selectedMuscles == null || selectedMuscles.isEmpty()) {
            val goalMuscleDocRef = db.collection("Users").document(user?.email.toString())
            goalMuscleDocRef.get().addOnSuccessListener { document ->
                muscles = document.get("desiredMuscles") as List<String>
                calculateWeights()
            }
        } else {
            muscles = selectedMuscles.asList()
            calculateWeights()
        }
    }

    private fun calculateWeights() {
        Log.i("data temp\t", temperature.toString()); Log.i("data sleep\t", sleep.toString()); Log.i("data steps\t", steps.toString()); Log.i("data intensity\t", intensity.toString()); Log.i("data duration\t", duration.toString()) ;Log.i("data muscles\t", muscles.toString())

        // DATA BREAKPOINTS
        // Temperature 10C to 30C (20C is avg)
        // Sleep 8hrs
        // Steps 5000 for moderate, 28000 for very active

        // Sleep weight = 1 / (1+e^-(x-4)). Logistic function with centerpoint at x=4
        val sleepWeight = if (sleep < 8.0) 1 / (1 + Math.E.pow(-1.0 * (sleep - 4))) else 1.0

        // Temperature weight = 250 / (z * sqrt(2pi)) * e^-(x-20)^2 / (2z). Normal distribution with midpoint at 20
        val z = 100
        val tempWeight = 250 / (z * sqrt(2 * Math.PI)) * Math.E.pow(-(temperature-20).pow(2) / (2 * z))

        // Step tracker weight
        val max = 1
        val min = 0.01
        val avgThreshold = 5000
        val activeThreshold = 28000

        var stepsWeight: Double
        if (steps < avgThreshold) {
            stepsWeight = 1.0
        } else if (steps < activeThreshold) {
            val slope = -(max-min) / (activeThreshold - avgThreshold)
            stepsWeight = slope * (steps - 28000) + min
        } else {
            stepsWeight = min
        }

        // Steps are affected by extreme weather
        stepsWeight *= tempWeight

        // Combined weights
        finalIntensity = intensity * (0.4 * stepsWeight + 0.6 * sleepWeight)

        fetchRecommendedWorkouts(finalIntensity)
    }

    private fun fetchRecommendedWorkouts(intensity: Double) {
        val numExercises = duration / 10
        val minIntensity = ceil(intensity - 1)
        val maxIntensity = floor(intensity + 1)

        Log.i("DOCS intensity", intensity.toString())

        // Recursive function to find exercises. If cannot find enough exercises, will progressively widen the intensity range
        queryForExercises(numExercises, minIntensity, maxIntensity)
    }

    private fun queryForExercises(numExercises: Int, minIntensity: Double, maxIntensity: Double) {
        listOfExercises.clear()

        val workoutDocRef = db.collection("Exercises")
            .whereGreaterThanOrEqualTo("Intensity", minIntensity)
            .whereLessThanOrEqualTo("Intensity", maxIntensity)
            .whereArrayContainsAny("Muscles", muscles).limit(numExercises.toLong())

        workoutDocRef.get()
            .addOnSuccessListener { documents ->
                if (documents.size() < numExercises) {
                    queryForExercises(numExercises, minIntensity-1, maxIntensity+1)
                } else {
                    for (doc in documents) {
                        listOfExercises.add(doc.id)
                    }
                    Log.i("DOCS FINAL", listOfExercises.toString())

                    val intent = Intent(this, PlaylistActivity::class.java)

                    intent.putExtra("exerciseList", listOfExercises.toTypedArray())
                    intent.putExtra("calculatedIntensity", finalIntensity)

                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener { error -> Log.i("Exercise fetch failed", error.toString())}
    }
}