package com.example.planmyworkout

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Button
import kotlinx.android.synthetic.main.activity_finish_popup.*

class FinishPopActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish_popup)

        var dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)

        val width = dm.widthPixels
        val height = dm.heightPixels

        window.setLayout((width * 0.8).toInt(), (height *0.5).toInt())

        val hb = findViewById<Button>(R.id.back_to_home_button)
        hb.setOnClickListener{
            backToNav()
        }
    }
    private fun backToNav(){
        val intent = Intent(this,NavActivity::class.java)
        startActivity(intent)
    }
}
