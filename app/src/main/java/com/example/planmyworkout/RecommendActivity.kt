package com.example.planmyworkout

import android.app.Activity
import android.os.Bundle
import android.util.Log

class RecommendActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_fetch)

        Log.i("DATA", intent.extras?.getDouble("temperature").toString())

    }
}