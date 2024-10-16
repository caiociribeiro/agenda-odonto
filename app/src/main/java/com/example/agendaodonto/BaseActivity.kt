package com.example.agendaodonto

import android.content.Intent
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
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
        onDataFetched: (String, String, String, String, String, String) -> Unit
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
                        val avatar = document.getString("avatar") ?: ""

                        val dobTimestamp = document.get("dateOfBirth") as? Timestamp
                        val dob = dobTimestamp?.toDate()?.let { date ->
                            SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(date)
                        } ?: ""

                        onDataFetched(name, email, phoneNumber, dob, userType, avatar)
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

        val main: LinearLayout = findViewById(R.id.main)
        val loading: ConstraintLayout = findViewById(R.id.loading_overlay)

        main.visibility = View.GONE
        loading.visibility = View.VISIBLE

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()

                    fetchUserData(user) { name, email, phoneNumber, dob, userType, avatar ->
                        saveUserData(name, email, phoneNumber, dob, userType, avatar)
                    }

                } else {
                    main.visibility = View.VISIBLE
                    loading.visibility = View.GONE

                    emailInputText.error = " "
                    passwordInputText.error = "E-mail ou senha incorretos."
                }
            }
    }


    private fun saveUserData(
        name: String,
        email: String,
        phoneNumber: String,
        dob: String,
        userType: String,
        avatar: String
    ) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("name", name)
        editor.putString("email", email)
        editor.putString("phoneNumber", phoneNumber)
        editor.putString("dob", dob)
        editor.putString("userType", userType)
        editor.putString("avatar", avatar)

        editor.apply()
    }

    fun getUserData(): Map<String, String?> {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val name = sharedPreferences.getString("name", null)
        val email = sharedPreferences.getString("email", null)
        val phoneNumber = sharedPreferences.getString("phoneNumber", null)
        val dob = sharedPreferences.getString("dob", null)
        val userType = sharedPreferences.getString("userType", null)
        val avatar = sharedPreferences.getString("avatar", null)

        return mapOf(
            "name" to name,
            "email" to email,
            "phoneNumber" to phoneNumber,
            "dob" to dob,
            "userType" to userType,
            "avatar" to avatar
        )
    }
}