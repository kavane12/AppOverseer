package com.example.planmyworkout.ui.home

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import com.example.planmyworkout.R

class Pop : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.pop_window)

        var dm : DisplayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout((width * 0.7).toInt(), (height *0.5).toInt())
    }
}
