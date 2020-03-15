package com.example.planmyworkout

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import com.androdocs.httprequest.HttpRequest
import org.json.JSONException
import org.json.JSONObject

class DataFetchActivity : Activity() {

    // Data we need for recommendation
    // todo Latitude and Longitude of Irvine for testing
    val LAT = 33
    val LONG = -118
    private var TEMPERATURE = -273.0

    // Check if tasks are done
    private var gotWeather = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_fetch)

        // WEATHER
        Weather().execute()
    }


    internal inner class Weather : AsyncTask<String?, Void?, String>() {
        private val OPENWEATHER_KEY: String = BuildConfig.OPENWEATHER_KEY

        override fun doInBackground(vararg params: String?): String {
            return HttpRequest.excuteGet("https://api.openweathermap.org/data/2.5/weather?lat=$LAT&lon=$LONG&units=metric&appid=$OPENWEATHER_KEY")
        }

        override fun onPostExecute(result: String) {
            try {
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")

                TEMPERATURE = main.getString("feels_like").toDouble();

                gotWeather = true
                switchToHomePage()

            } catch (e: JSONException) {
                Log.w("API Fetch", "Failed to fetch valid weather data")
            }
        }
    }

    private fun switchToHomePage() {
        // Make sure we have all relevant data points
        if (gotWeather) {
            // Switch to home page and end current activity
            val intent = Intent(this, NavActivity::class.java)

            intent.putExtra("temperature", TEMPERATURE)

            startActivity(intent)

            // Stop current activity
            finish()
        }
    }
}

