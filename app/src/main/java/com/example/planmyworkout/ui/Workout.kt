package com.example.planmyworkout.ui

import android.app.Activity
import android.os.Bundle
import android.util.Log

class Workout : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("this ran", "switched to workout!")
    }

}
