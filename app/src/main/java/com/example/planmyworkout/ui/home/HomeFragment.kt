package com.example.planmyworkout.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.planmyworkout.R
import com.example.planmyworkout.RecommendActivity


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer { textView.text = it })

        // Start Button
        val startButton = root.findViewById<Button>(R.id.start_button)
        startButton?.setOnClickListener {
            switchToActivity(RecommendActivity::class.java)
        }

        // Custom Workout Popup
        val customButton = root.findViewById<Button>(R.id.custom_button)
        customButton?.setOnClickListener {
            switchToActivity(PopActivity::class.java)
        }

        return root
    }

    private fun switchToActivity(act: Class<out Activity>) {
        // Switches to given Activity with relevant data points
        val intent = Intent(activity, act)

        intent.putExtra("temperature", arguments?.getDouble("temperature"))
        intent.putExtra("sleep", arguments?.getDouble("sleep"))
        intent.putExtra("steps", arguments?.getLong("steps"))

        startActivity(intent)
    }


}
