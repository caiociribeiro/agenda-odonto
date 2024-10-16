package com.example.agendaodonto

import android.content.Intent
import android.text.InputFilter
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

open class BaseActivity : AppCompatActivity() {
    fun applyInputFilters(editText: EditText, isEmail: Boolean = false) {
        val inputFilter = InputFilter { source, start, end, dest, dstart, dend ->
            for (i in start until end) {
                if (source[i] == '\\' || source[i] == '\n' || source[i] == '\t') return@InputFilter "" // Block backslash
            }

            if (isEmail) {
                val lowercaseInput = source.toString().lowercase(Locale.getDefault())
                if (lowercaseInput != source.toString()) {
                    return@InputFilter lowercaseInput // Convert email to lowercase
                }
            }
            null
        }
        editText.filters = arrayOf(inputFilter)
    }

    fun fetchUserData(
        user: FirebaseUser?,
        onDataFetched: (String, String, String, String, String) -> Unit
    ) {
        if (user != null) {
            val firestore = FirebaseFirestore.getInstance()
            val userId = user.uid

            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: ""
                        val email = document.getString("email") ?: ""
                        val phoneNumber = document.getString("phoneNumber") ?: ""
                        val userType = document.getString("userType") ?: ""

                        val dobTimestamp = document.get("dateOfBirth") as? Timestamp
                        val dob = dobTimestamp?.toDate()?.let { date ->
                            SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(date)
                        } ?: ""

                        onDataFetched(name, email, phoneNumber, dob, userType)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("FetchUserData", "Error fetching user data", exception)
                }
        }
    }

    fun signIn(
        auth: FirebaseAuth,
        email: String,
        password: String,
        emailInputText: TextInputLayout,
        passwordInputText: TextInputLayout
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                    // updateUI(user)
                } else {
                    emailInputText.error = " "
                    passwordInputText.error = "E-mail ou senha incorretos."
                }
            }
    }
}