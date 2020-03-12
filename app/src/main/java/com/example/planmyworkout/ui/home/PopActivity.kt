package com.example.planmyworkout.ui.home

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.TextView
import com.example.planmyworkout.R

class PopActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.pop_window)

        var dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout((width * 0.8).toInt(), (height *0.7).toInt())

        val customTitle = findViewById<TextView>(R.id.custom_popup_title)
        customTitle.setText("YOLO SWAG")
    }
}
