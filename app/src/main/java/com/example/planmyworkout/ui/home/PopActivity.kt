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
import com.example.planmyworkout.R
import com.example.planmyworkout.ui.Workout


class PopActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
            var newValue = 0
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                newValue = progress
                durationTextView.setText("Duration: ~${(newValue+1)*10} minutes")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Required by object
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Required by object
            }
        })

        intensitySeekbar?.setOnSeekBarChangeListener(  object: OnSeekBarChangeListener {
            var newValue = 0
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                newValue = progress
                intensityTextView.setText("Intensity: ${(newValue+1)}")
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
//                newValue = isChecked
            }
        })

        // Muscle Group Spinner (dropdown)
        val muscleGroupSpinner = findViewById<Spinner>(R.id.custom_popup_spinner)

        // List of items for the spinner.
        val items: MutableList<String> = mutableListOf<String>()
        items.add ("")
        items.add("Arm")
        items.add("Chest")
        items.add("Leg")

        // Create an adapter to describe how the items are displayed
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        muscleGroupSpinner.adapter = adapter

        // For access of "this" inside callback
        val that = this

        muscleGroupSpinner.setOnItemSelectedListener(object : OnItemSelectedListener {
            var check = false

            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                if (check) {
                    // Remove selection from dropdown
                    items.removeAt(position)

                    val newAdapter = ArrayAdapter(that, android.R.layout.simple_spinner_dropdown_item, items)
                    muscleGroupSpinner.adapter = newAdapter

                    check = false
                } else {
                    check = true
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        })
    }

    private fun switchToWorkoutActivity() {
        val intent = Intent(this, Workout::class.java)
    }



}
