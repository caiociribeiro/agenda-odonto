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
import com.example.agendaodonto.activities.HomeActivityTemp
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
        auth: FirebaseAuth,
        user: FirebaseUser?,
        isDentist: Boolean,
        onDataFetched: (String, String, String, String, String) -> Unit,
    ) {
        if (user != null) {
            val firestore = FirebaseFirestore.getInstance()
            val userId = user.uid

            val collectionPath = if (isDentist) "dentistas" else "users"


            firestore.collection(collectionPath)
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: ""
                        val email = document.getString("email") ?: ""
                        val phoneNumber = document.getString("phoneNumber") ?: ""
                        val avatar = document.getString("avatarUrl") ?: ""

                        val userType = if (isDentist) "dentista" else "paciente"


                        onDataFetched(name, email, phoneNumber, userType, avatar)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("FetchUserData", "Error fetching user data", e)
                    auth.signOut()
                    Toast.makeText(
                        baseContext,
                        "Algo deu errado. Tente novamente mais tarde.",
                        Toast.LENGTH_LONG,
                    ).show()

                }
        }

    }

    fun signIn(
        auth: FirebaseAuth,
        email: String,
        password: String,
        emailInputText: TextInputLayout,
        passwordInputText: TextInputLayout,
        isDentist: Boolean
    ) {

        val main: LinearLayout = findViewById(R.id.main)
        val loading: ConstraintLayout = findViewById(R.id.loading_overlay)


        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    main.visibility = View.GONE
                    loading.visibility = View.VISIBLE

                    val user = auth.currentUser

                    fetchUserData(
                        auth,
                        user,
                        isDentist
                    ) { name, email, phoneNumber, userType, avatar ->
                        saveUserData(name, email, phoneNumber, userType, avatar)

                        if (isDentist) {
                            val intent = Intent(this, HomeDentistaActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val intent = Intent(this, HomeActivityTemp::class.java)
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

    fun signUp(auth: FirebaseAuth, name: String, email: String, password: String) {

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

                    val userData = hashMapOf(
                        "name" to name,
                        "email" to email,
                        "phoneNumber" to "",
                        "userType" to "Paciente",
                        "avatar" to ""
                    )

                    userId?.let {
                        firestore.collection("users").document(it).set(userData)
                            .addOnSuccessListener {
                                fetchUserData(
                                    auth,
                                    user,
                                    false
                                ) { name, email, phoneNumber, userType, avatar ->
                                    saveUserData(name, email, phoneNumber, userType, avatar)

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
        editor.putString("userType", userType)

        editor.apply()
    }

    fun getUserData(): Map<String, String?> {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return mapOf(
            "name" to sharedPreferences.getString("name", null),
            "email" to sharedPreferences.getString("email", null),
            "phoneNumber" to sharedPreferences.getString("phoneNumber", null),
            "userType" to sharedPreferences.getString("userType", null),
            "avatar" to sharedPreferences.getString("avatar", null)
        )
    }

    fun gerarDisponibilidadeDentista(dentistaId: String) {
        val db = FirebaseFirestore.getInstance()
        val disponibilidadeRef =
            db.collection("dentistas").document(dentistaId).collection("disponibilidade")

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        for (i in 0 until 30) {
            val data = dateFormat.format(calendar.time)

            disponibilidadeRef.document(data).get()
                .addOnSuccessListener { document ->
                    if (!document.exists()) {
                        val horarios = hashMapOf(
                            "09:00" to true,
                            "10:00" to true,
                            "11:00" to true,
                            "14:00" to true,
                            "15:00" to true,
                            "16:00" to true
                        )

                        val diaDisponivel = hashMapOf(
                            "data" to data,
                            "horarios" to horarios
                        )

                        disponibilidadeRef.document(data)
                            .set(diaDisponivel)
                            .addOnSuccessListener {
                                println("Disponibilidade para o dia $data adicionada com sucesso!")
                            }
                            .addOnFailureListener { e ->
                                println("Erro ao adicionar disponibilidade para o dia $data: ${e.message}")
                            }
                    } else {
                        println("Dia $data já existe, não será necessário criar novamente.")
                    }
                }
                .addOnFailureListener { e ->
                    println("Erro ao verificar disponibilidade para o dia $data: ${e.message}")
                }

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

}