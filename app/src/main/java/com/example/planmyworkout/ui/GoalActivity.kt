package com.example.planmyworkout.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.SeekBar.OnSeekBarChangeListener
import com.example.planmyworkout.R
import com.example.planmyworkout.ui.Workout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class GoalActivity : Activity() {

    val user = FirebaseAuth.getInstance().currentUser
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_goal_popup)

        var dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout((width * 0.8).toInt(), (height *0.7).toInt())

        val goalTitle = findViewById<TextView>(R.id.goal_title)
        goalTitle.setText("Welcome, ${user!!.displayName}")

        // Goal Muscle Group Spinner
        val goalMuscleSpinner = findViewById<Spinner>(R.id.goal_spinner)

        // List of items for the spinner.
        val items: MutableList<String> = mutableListOf<String>()
        items.add ("All")

        val muscleDocRef = db.collection("Muscle Groups").document("muscles")
        muscleDocRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val muscleList : List<String> = document.data?.get("muscleList") as List<String>
                for (muscle in muscleList.sorted()) {
                    items.add(muscle)
                }
            }
        }





        // Create an adapter to describe how the items are displayed
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        goalMuscleSpinner.adapter = adapter

        // For access of "this" inside callback
        val that = this

        goalMuscleSpinner.setOnItemSelectedListener(object : OnItemSelectedListener {
            var check = false

            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                if (check) {
                    // Remove selection from dropdown
                    items.removeAt(position)

                    val newAdapter = ArrayAdapter(that, android.R.layout.simple_spinner_dropdown_item, items)
                    goalMuscleSpinner.adapter = newAdapter

                    check = false
                } else {
                    check = true
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        })
    }



}
