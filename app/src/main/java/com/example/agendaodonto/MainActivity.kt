package com.example.agendaodonto

import android.content.Intent
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onStart() {
        super.onStart()

        auth = Firebase.auth

        val currentUser = auth.currentUser
        if (currentUser != null) {

            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val userType = sharedPreferences.getString("userType", null)

            if (userType == "dentista") {
                val intent = Intent(this, HomeDentistaActivity::class.java)
                startActivity(intent)
                finish()
            } else if (userType == "paciente") {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                auth.signOut()
                val intent = Intent(this, GreetingActivity::class.java)
                startActivity(intent)
                finish()
            }

        } else {
            val intent = Intent(this, GreetingActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}