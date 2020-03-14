package com.example.planmyworkout

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavArgument
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class NavActivity : AppCompatActivity() {

    val user = FirebaseAuth.getInstance().currentUser
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)

        // Get relevant data
        val temperature = intent.getDoubleExtra("temperature", -273.0)


        // Set up bottom navigation bar
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        val navInflater = navController.navInflater
        val graph = navInflater.inflate(R.navigation.mobile_navigation)

        graph.addArgument("temperature", NavArgument.Builder().setDefaultValue(temperature).build())

        navController.graph = graph

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home,
            R.id.navigation_dashboard,
            R.id.navigation_others
        ))

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.navigation_home -> {
                    destination.addArgument("temperature", NavArgument.Builder().setDefaultValue(temperature).build())
                }
            }
        }

        // Goals Popup
        val docRef = db.collection("Users").document(user!!.email.toString())
        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.data?.get("desiredMuscles") != null) {
                // User already has inputted goals
            } else {
                // User hasn't inputted goals
                val intent = Intent(this, GoalActivity::class.java)
                startActivity(intent)
            }
        }
    }
}