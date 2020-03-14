package com.example.planmyworkout.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.planmyworkout.PlaylistActivity
import com.example.planmyworkout.R
import com.example.planmyworkout.RecommendActivity


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

        // Custom Workout Popup
        val customButton : Button = root.findViewById(R.id.custom_button)

        customButton?.setOnClickListener {
            val intent = Intent(activity, PopActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    private fun switchToRocommendActivity() {
        val intent = Intent(activity, RecommendActivity::class.java)
        startActivity(intent)
    }


}
