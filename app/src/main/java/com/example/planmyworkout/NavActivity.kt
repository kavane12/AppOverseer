package com.example.planmyworkout

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.androdocs.httprequest.HttpRequest
import org.json.JSONException
import org.json.JSONObject


class NavActivity : AppCompatActivity() {

    // todo Latitude and Longitude of Irvine for testing
    val LAT = 33.685909
    val LONG = -117.824722

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

        // Get the weather
        Weather().execute()
    }

    internal inner class Weather :AsyncTask<String?, Void?, String>() {

        val OPENWEATHER_KEY: String = BuildConfig.OPENWEATHER_KEY

        override fun onPreExecute() {
            super.onPreExecute()
            // Loading progress bar?
        }

        override fun doInBackground(vararg params: String?): String {
            return HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat=$LAT&long=$LONG&units=metric&appid=$OPENWEATHER_KEY")
        }

        override fun onPostExecute(result: String) {
            try {
                val jsonObj = JSONObject(result)

                Log.i("test", "Got ze json")
            } catch (e: JSONException) {
                // todo Do something
            }
        }
    }
}