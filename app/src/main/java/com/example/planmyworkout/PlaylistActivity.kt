package com.example.planmyworkout

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_playlist.*

class PlaylistActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)

        // For demo purposes, show the calculated intensity
        val calculatedIntensity = intent.getDoubleExtra("calculatedIntensity", 5.0)
        val intensityTextView = findViewById<TextView>(R.id.playlist_demo_intensity)
        intensityTextView.text = "Calculated Final Intensity: ${calculatedIntensity}"


        val exercises: Array<String> = intent.getStringArrayExtra("exerciseList")
//        val exercises: Array<String> = arrayOf("Barbell Curl", "Twisting Crunch", "Dumbbell Kickback") //placeholder
        val listView = findViewById<ListView>(R.id.playlist_listview)
        listView.adapter = CustomAdapter(this, exercises)
        launchNextScreen(exercises)
    }

    private class CustomAdapter(context: Context, exercises: Array<String>) : BaseAdapter() {
        var db = FirebaseFirestore.getInstance()

        private val mContext: Context = context
        private val ex_list: Array<String> = exercises

        override fun getCount(): Int {
            return ex_list.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return ex_list[position]
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val row = layoutInflater.inflate(R.layout.playlist_row, parent, false)

            val exerciseNameTextView = row.findViewById<TextView>(R.id.exercise_name_textview)
            val exerciseDescriptionTextView = row.findViewById<TextView>(R.id.exercise_sublabel_textview)

            exerciseNameTextView.text = ex_list[position]

            val docRef = db.collection("Exercises").document(ex_list[position])

            docRef.get().addOnCompleteListener(OnCompleteListener {
                if (it.isSuccessful) {
                    val equip = it.result?.getBoolean("Equipment")
                    if (equip != null) {
                        if (equip) {
                            exerciseDescriptionTextView.text = "Equipment required"
                        } else {
                            exerciseDescriptionTextView.text = "No equipment required"
                        }
                    }
                }
            })

            return row
        }

    }

    private fun launchNextScreen(exercises: Array<String>) {
        play_button.setOnClickListener {
            val intent = Intent(this, ExerciseActivity::class.java)
            intent.putExtra("exercise_list", exercises)
            intent.putExtra("exercise_list_index", 0)
            startActivity(intent)
        }
    }
}
