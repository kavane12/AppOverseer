package com.example.planmyworkout

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.content.Intent
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

const val RC_SIGN_IN = 7
const val USER = "com.example.planmyworkout.CURRENT_USER"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val user = FirebaseAuth.getInstance().currentUser

        // If user is already logged in, go to home page, else prompt login
        if (user != null) {
            switchToHomePage(user)
        } else {
            // Choose authentication providers
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build()
            )

            // Create and launch sign-in intent
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN
            )

            setContentView(R.layout.activity_main)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in, go to home page
                val user = FirebaseAuth.getInstance().currentUser
                switchToHomePage(user)
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
            }
        }
    }

    private fun switchToHomePage(user: FirebaseUser?) {
        // Switch to home page and end current activity
        val intent = Intent(this, NavActivity::class.java)

        // Passes user variable to next activity
        intent.putExtra(USER, user)

        // Prevents animation when switching to new event
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

        // Stop current activity
        finish()
        overridePendingTransition(0, 0); //0 for no animation on finish()

        startActivity(intent)
    }
}
