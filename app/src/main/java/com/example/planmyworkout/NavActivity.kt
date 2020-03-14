package com.example.planmyworkout

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.androdocs.httprequest.HttpRequest
import com.example.planmyworkout.ui.GoalActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONException
import org.json.JSONObject


class NavActivity : AppCompatActivity() {

    val user = FirebaseAuth.getInstance().currentUser
    val db = Firebase.firestore

    // Data we need for recommendation
    // todo Latitude and Longitude of Irvine for testing
    val LAT = 33
    val LONG = -118
    private var temperature : Double = -273.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nav)

        // Set up bottom navigation bar
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home,
            R.id.navigation_dashboard,
            R.id.navigation_others
        ))

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

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

        // Get the weather
        val temperature = Weather().execute()
    }

    internal inner class Weather :AsyncTask<String?, Void?, String>() {

        val OPENWEATHER_KEY: String = BuildConfig.OPENWEATHER_KEY

        override fun onPreExecute() {
            super.onPreExecute()
            // Loading progress bar?
        }

        override fun doInBackground(vararg params: String?): String {
            return HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat=$LAT&lon=$LONG&units=metric&appid=$OPENWEATHER_KEY")
    }

        override fun onPostExecute(result: String) {
            try {
                Log.i("Entire Weather JSON", JSONObject(result).toString())

                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)

                val weatherInfoText = ".\nWEATHER\nCurrently: ${weather.getString("main")}\nTemp max: ${main.getString("temp_max")}\nTemp min: ${main.getString("temp_min")}\nFeelsLike: ${main.getString("feels_like")}"
                Log.i("WEATHER", weatherInfoText)
                return main.getString("FeelsLike");

            } catch (e: JSONException) {
                Log.w("API Fetch", "Failed to fetch valid weather data")
            }
        }
    }
}