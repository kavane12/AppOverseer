package com.example.planmyworkout

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import com.androdocs.httprequest.HttpRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class DataFetchActivity : Activity() {

    val user = FirebaseAuth.getInstance().currentUser
    val db = Firebase.firestore

    // Data we need for recommendation
    private var LAT = 0
    private var LONG = 0
    private var TEMPERATURE: Double = -273.0
    private var SLEEP_DURATION: Number = -1.0
    private var STEPS_WALKED: Long = -1

    // Check if tasks are done
    private var gotWeather = false
    private var gotSleep = false
    private var gotSteps = false

    private lateinit var fusedLocationClient : FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_fetch)

        // LOCATION
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                LAT = location.latitude.toInt()
                LONG = location.longitude.toInt()

                // WEATHER
                Weather().execute()
            }
        }

        val dateFormatter = SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss")

//        val currentDay = Date() not using current date for demo purposes

        // For demo, current day will be 3/15/2019 at 12PM
        val currentDay = dateFormatter.parse("2020/03/15T12:00:00")
        Log.i("fetches current", currentDay.toString())

        // SLEEP
        val sleepDocRef = db.collection("Sleep Log").whereEqualTo("user_email", user?.email).orderBy("date", Query.Direction.DESCENDING).limit(1)
        sleepDocRef.get()
            .addOnSuccessListener { documents ->
                // Will only have 1 document, the most recent one
                for (doc in documents) {
                    val logDate = (doc.get("date") as Timestamp).toDate()

                    if (daysBetween(logDate, currentDay) == 1) {
                        SLEEP_DURATION = doc.get("hours") as Number
                    }
                }
                gotSleep = true
                switchToHomePage()
            }.addOnFailureListener {error ->
                Log.i("Failed to fetch sleep", error.toString())
            }

        // STEP TRACKER
        val stepDocRef = db.collection("Steptracker").whereEqualTo("user_email", user?.email).orderBy("date", Query.Direction.DESCENDING).limit(1)
        stepDocRef.get()
            .addOnSuccessListener { documents ->
                // Will only have 1 document, the most recent one
                for (doc in documents) {
                    val logDate = (doc.get("date") as Timestamp).toDate()

                    if (daysBetween(logDate, currentDay) == 0) {
                        STEPS_WALKED = doc.get("steps") as Long
                    }
                }
                gotSteps = true
                switchToHomePage()
            }.addOnFailureListener {error ->
                Log.i("Failed to fetch steps", error.toString())
            }
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
        if (gotWeather && gotSleep && gotSteps) {

            Log.i("fetch temp", TEMPERATURE.toString())
            Log.i("fetch sleep", SLEEP_DURATION.toString())
            Log.i("fetch steps", STEPS_WALKED.toString())

            // Switch to home page and end current activity
            val intent = Intent(this, NavActivity::class.java)

            // For demo purposes, hardcoded temperature
            TEMPERATURE = 22.0

            intent.putExtra("temperature", TEMPERATURE)
            intent.putExtra("sleep", SLEEP_DURATION)
            intent.putExtra("steps", STEPS_WALKED)

            startActivity(intent)

            // Stop current activity
            finish()
        }
    }

    private fun daysBetween(startDate: Date?, endDate: Date?): Int {
        val sDate: Calendar = getDatePart(startDate)
        val eDate: Calendar = getDatePart(endDate)

        var daysBetween = 0
        while (sDate.before(eDate)) {
            sDate.add(Calendar.DAY_OF_MONTH, 1)
            daysBetween++
        }

        return daysBetween
    }

    private fun getDatePart(date: Date?): Calendar {
        val cal: Calendar = Calendar.getInstance()
        cal.setTime(date)
        cal[Calendar.HOUR_OF_DAY] = 0 // set hour to midnight
        cal[Calendar.MINUTE] = 0 // set minute in hour
        cal[Calendar.SECOND] = 0 // set second in minute
        cal[Calendar.MILLISECOND] = 0 // set millis in second

        val zeroedDate = cal.time // actually computes the new Date
        return cal

    }

}

