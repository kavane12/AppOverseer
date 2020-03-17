package com.example.planmyworkout

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class GoalActivity : Activity() {

    val user = FirebaseAuth.getInstance().currentUser
    val db = Firebase.firestore

    // Run once controller for spinner
    var check = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_goal_popup)

        var dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout((width * 0.8).toInt(), (height * 0.7).toInt())

        // Goal title
        val goalTitle = findViewById<TextView>(R.id.goal_title)
        goalTitle.setText("Welcome, ${user!!.displayName}")

        // List of items that have been selected by user
        var selectedMuscles = mutableSetOf<String>()

        // Goal Muscle Group Spinner
        val goalMuscleSpinner = findViewById<Spinner>(R.id.goal_spinner)

        // List of items for the spinner.
        val items = mutableListOf<String>()
        items.add("All")

        val muscleDocRef = db.collection("Muscle Groups").document("muscles")
        muscleDocRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val muscleList: List<String> = document.data?.get("muscleList") as List<String>
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

            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                if (check) {
                    // Remove selection from dropdown, add to selected
                    val removed = items.removeAt(position)
                    selectedMuscles.add(removed)
                    addChip(removed, selectedMuscles, items, goalMuscleSpinner)

                    val newAdapter =
                        ArrayAdapter(that, android.R.layout.simple_spinner_dropdown_item, items)
                    goalMuscleSpinner.adapter = newAdapter

                    check = false
                } else {
                    check = true
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        })

        // Goals Confirm button
        val goalConfirmButton = findViewById<Button>(R.id.goal_confirm_button)

        goalConfirmButton.setOnClickListener {
            val data = hashMapOf("desiredMuscles" to selectedMuscles.toList())
            db.collection("Users").document(user.email.toString()).set(data, SetOptions.merge())
            finish()
        }


    }

    private fun addChip(tag: String, musclesSet: MutableSet<String>, dropdownItems : MutableList<String>, goalMuscleSpinner : Spinner) {
        val chipGroup: ChipGroup = findViewById(R.id.goal_chipgroup)
        // Create chip
        val chip = Chip(this)

        chip.text = tag
        chip.setCloseIconResource(android.R.drawable.ic_menu_close_clear_cancel)
        chip.isCloseIconVisible = true

        //Added click listener for close icon to remove chip from ChipGroup
        chip.setOnCloseIconClickListener {
            musclesSet.remove(tag)
            chipGroup.removeView(chip)

            // Read to dropdown menu
            dropdownItems.removeAt(0)
            dropdownItems.add(tag)
            dropdownItems.sort()
            dropdownItems.add(0, "All")

            val newAdapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dropdownItems)
            goalMuscleSpinner.adapter = newAdapter

            check = false
        }

        chipGroup.addView(chip)
    }

}
