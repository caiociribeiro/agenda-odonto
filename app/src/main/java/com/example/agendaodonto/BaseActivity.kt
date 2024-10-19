package com.example.agendaodonto

import android.content.Intent
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
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


            firestore.collection("users")
                .document(userId)
                .get()
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


        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    main.visibility = View.GONE
                    loading.visibility = View.VISIBLE

                    val user = auth.currentUser

                    fetchUserData(user) { name, email, phoneNumber, dob, userType, avatar ->
                        saveUserData(name, email, phoneNumber, dob, userType, avatar)

                        if (userType == "dentista") {
                            val intent = Intent(this, HomeDentistaActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else if (userType == "paciente") {
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }


                } else {
                    emailInputText.error = " "
                    passwordInputText.error = "E-mail ou senha incorretos."
                }
            }
    }

    fun signUp(auth: FirebaseAuth, name: String, email: String, dob: String, password: String) {

        val registerLayout: LinearLayout = findViewById(R.id.register)
        val loading: ConstraintLayout = findViewById(R.id.loading_overlay)


        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    registerLayout.visibility = View.GONE
                    loading.visibility = View.VISIBLE

                    val user = auth.currentUser
                    val userId = user?.uid

                    val firestore = FirebaseFirestore.getInstance()

                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val date: Date = sdf.parse(dob) ?: Date()

                    val dobAsTimestamp = Timestamp(date)

                    val userData = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "dateOfBirth" to dobAsTimestamp,
                        "phoneNumber" to "",
                        "userType" to "Paciente",
                        "avatar" to ""
                    )

                    userId?.let {
                        firestore.collection("users").document(it).set(userData)
                            .addOnSuccessListener {
                                fetchUserData(user) { name, email, phoneNumber, dob, userType, avatar ->
                                    saveUserData(name, email, phoneNumber, dob, userType, avatar)

                                    val intent = Intent(this, HomeActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    baseContext,
                                    "Error saving user data: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                } else {

                    Toast.makeText(
                        baseContext,
                        "Algo deu errado. Tente novamente mais tarde.",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }

    }

    fun saveUserData(
        name: String,
        email: String,
        phoneNumber: String,
        dob: String,
        userType: String,
        avatar: String
    ) {
        Log.d("myTag", "Salvando dados do usuario...")

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("name", name)
        editor.putString("email", email)
        editor.putString("avatar", avatar)
        editor.putString("phoneNumber", phoneNumber)
        editor.putString("dob", dob)
        editor.putString("userType", userType)

        editor.apply()
    }

    fun getUserData(): Map<String, String?> {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return mapOf(
            "name" to sharedPreferences.getString("name", null),
            "email" to sharedPreferences.getString("email", null),
            "phoneNumber" to sharedPreferences.getString("phoneNumber", null),
            "dob" to sharedPreferences.getString("dob", null),
            "userType" to sharedPreferences.getString("userType", null),
            "avatar" to sharedPreferences.getString("avatar", null)
        )
    }

}