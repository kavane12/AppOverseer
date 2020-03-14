package com.example.planmyworkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.TimeKeyListener
import android.util.Log
import android.widget.Button
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_exercise.*
import java.sql.Timestamp

class ExerciseActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise)

        val exercises: Array<String>? = intent.getStringArrayExtra("exercise_list")
        val index: Int = intent.getIntExtra("exercise_list_index",0)

        if(exercises != null){
            supportActionBar?.title = exercises[index]

            val docRef = db.collection("Exercises").document(exercises[index])
            docRef.get().addOnCompleteListener(OnCompleteListener {
                exercise_description_textView.text = it.result?.getString("Description")
                exercise_intensity_textView.text = "Intensity: " + it.result?.getLong("Intensity").toString()
            })

            skip_button.setOnClickListener{
                if(index >= exercises.size-1){
                    finishWorkout(exercises)
                }else {
                    nextExerciseScreen(exercises, index)
                }
            }
            ratingbutton.setOnClickListener{
                if(index >= exercises.size-1){
                    finishWorkout(exercises)
                }else {
                    nextExerciseScreen(exercises, index)
                }
            }
        }

    }

    private fun finishWorkout(exercises:Array<String>?){
        // store exercises into user workout history
        val data = hashMapOf(
            "date" to Timestamp(System.currentTimeMillis()),
            "exercises" to exercises?.toList(),
            "user_email" to FirebaseAuth.getInstance().currentUser?.email.toString()
        )
        db.collection("Workout Sessions").add(data)
            .addOnSuccessListener{
                Log.w("Firestore", "DocumentSnapshot written with ID: ${it.id}")
            }
            .addOnFailureListener{
                Log.w("Firestore","Error adding document",it)
            }

        val intent = Intent(this,FinishPopActivity::class.java)
        startActivity(intent)
    }

    private fun nextExerciseScreen(exercises:Array<String>?,index:Int){
        val intent = Intent(this,ExerciseActivity::class.java)
        intent.putExtra("exercise_list",exercises)
        intent.putExtra("exercise_list_index",index+1)
        startActivity(intent)
    }
}
