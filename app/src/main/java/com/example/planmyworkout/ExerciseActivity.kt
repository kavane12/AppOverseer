package com.example.planmyworkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_exercise.*

class ExerciseActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        val exercises: Array<String>? = intent.getStringArrayExtra("exercise_list")
        val index: Int = intent.getIntExtra("exercise_list_index",0)
        if(exercises != null && index >= exercises.size || exercises == null){
            goBackToPlaylist(exercises)
        }
        val skipbutton = findViewById<Button>(R.id.skip_button)
        if(exercises != null){
            supportActionBar?.title = exercises[index]

            val docRef = db.collection("Exercises").document(exercises[index])
            docRef.get().addOnCompleteListener(OnCompleteListener {
                exercise_description_textView.text = it.result?.getString("Description")
                exercise_intensity_textView.text = "Intensity: " + it.result?.getLong("Intensity").toString()
            })

            skipbutton.setOnClickListener{
                nextExerciseScreen(exercises,index)
            }
        }

    }

    private fun goBackToPlaylist(exercises:Array<String>?){
        val intent = Intent(this,PlaylistActivity::class.java)
        intent.putExtra("exercise_list", exercises)
        startActivity(intent)
    }

    private fun nextExerciseScreen(exercises:Array<String>?,index:Int){
        val intent = Intent(this,ExerciseActivity::class.java)
        intent.putExtra("exercise_list",exercises)
        intent.putExtra("exercise_list_index",index+1)
        startActivity(intent)
    }
}
