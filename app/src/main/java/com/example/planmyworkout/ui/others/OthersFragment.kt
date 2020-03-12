package com.example.planmyworkout.ui.others

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
import com.example.planmyworkout.MainActivity
import com.example.planmyworkout.R
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth


class OthersFragment : Fragment() {

    private lateinit var othersViewModel: OthersViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        othersViewModel = ViewModelProviders.of(this).get(OthersViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_others, container, false)

        val textView: TextView = root.findViewById(R.id.text_others)
        othersViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = FirebaseAuth.getInstance().currentUser?.email
        })

        // OTHERS FRAGMENT - Sign out button
        val signoutButton: Button = root.findViewById(R.id.signout_button)
        signoutButton?.setOnClickListener {
            activity?.let { it1 ->
                AuthUI.getInstance().signOut(it1)
                    .addOnCompleteListener {
                        val intent = Intent(it1, MainActivity::class.java)
                        startActivity(intent)
                        it1.finish()
                    }
            }
        }

        return root
    }
}
