package com.example.planmyworkout.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.SeekBar.OnSeekBarChangeListener
import com.example.planmyworkout.PlaylistActivity
import com.example.planmyworkout.R
import com.example.planmyworkout.RecommendActivity
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class PopActivity : Activity() {

    val db = Firebase.firestore

    // Run once for Spinner
    var check = false

    // Data from intent
    var temperature = -273.0
    var sleep: Double = -1.0
    var steps: Long = -1

    // Input data
    var selectedDuration = 60
    var selectedIntensity = 5
    var selectedAtGym = true
    var selectedMuscles = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val temp = intent.extras?.getDouble("temperature")
        if (temp != null) {
            temperature = temp
        }
        val slp = intent.extras?.getDouble("sleep")
        if (slp != null) {
            sleep = slp
        }
        val stp = intent.extras?.getLong("steps")
        if (stp != null) {
            steps = stp
        }

        setContentView(R.layout.activity_custom_popup)

        var dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout((width * 0.8).toInt(), (height *0.7).toInt())

        // Seekbars
        val durationSeekbar = findViewById<SeekBar>(R.id.custom_popup_duration_seekbar)
        val intensitySeekbar = findViewById<SeekBar>(R.id.custom_popup_intensity_seekbar)

        // TextView above seekbars
        val durationTextView : TextView = findViewById(R.id.custom_popup_duration_text)
        val intensityTextView : TextView = findViewById(R.id.custom_popup_intensity_text)

        durationSeekbar?.setOnSeekBarChangeListener(  object: OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                selectedDuration = (progress+1)*10
                durationTextView.setText("Duration: ~$selectedDuration minutes")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Required by object
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Required by object
            }
        })

        intensitySeekbar?.setOnSeekBarChangeListener(  object: OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                selectedIntensity = progress+1
                intensityTextView.setText("Intensity: $selectedIntensity")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Required by object
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Required by object
            }
        })

        // "At Gym?" Switch
        val hasEquipmentSwitch = findViewById<Switch>(R.id.custom_popup_equipment_switch)

        hasEquipmentSwitch?.setOnCheckedChangeListener( object: CompoundButton.OnCheckedChangeListener {

            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                selectedAtGym = isChecked
            }
        })

        // Muscle Group Spinner (dropdown)
        val muscleGroupSpinner = findViewById<Spinner>(R.id.custom_popup_spinner)

        // List of items for the spinner.
        val items: MutableList<String> = mutableListOf<String>()
        items.add ("Any")

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
        muscleGroupSpinner.adapter = adapter

        // For access of "this" inside callback
        val that = this

        muscleGroupSpinner.setOnItemSelectedListener(object : OnItemSelectedListener {

            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                if (check) {
                    // Remove selection from dropdown, add to selected
                    val removed = items.removeAt(position)
                    selectedMuscles.add(removed)
                    addChip(removed, selectedMuscles, items, muscleGroupSpinner)

                    val newAdapter =
                        ArrayAdapter(that, android.R.layout.simple_spinner_dropdown_item, items)
                    muscleGroupSpinner.adapter = newAdapter

                    check = false
                } else {
                    check = true
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        })

        // Start button
        val customStartButton = findViewById<Button>(R.id.custom_start_button)
        customStartButton?.setOnClickListener {
            switchToRecommendActivity()
        }
    }

    private fun addChip(tag: String, musclesSet: MutableSet<String>, dropdownItems : MutableList<String>, popupMuscleSpinner : Spinner) {
        val chipGroup: ChipGroup = findViewById(R.id.custom_popup_chips)
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
            popupMuscleSpinner.adapter = newAdapter

            check = false
        }

        chipGroup.addView(chip)
    }

    private fun switchToRecommendActivity() {
        val intent = Intent(this, RecommendActivity::class.java)

        intent.putExtra("temperature", temperature)
        intent.putExtra("sleep", sleep)
        intent.putExtra("steps", steps)
        intent.putExtra("duration", selectedDuration)
        intent.putExtra("intensity", selectedIntensity)
        intent.putExtra("selectedMuscles", selectedMuscles.toTypedArray())


        startActivity(intent)
    }
}
