package com.example.planmyworkout.ui.home

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import com.example.planmyworkout.R


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
    }



}
