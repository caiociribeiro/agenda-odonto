package com.example.agendaodonto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.Firebase

class LoginActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth

    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        tilEmail = findViewById(R.id.til_email)
        tilPassword = findViewById(R.id.til_password)

        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)


        applyInputFilters(etEmail)
        applyInputFilters(etPassword)

        val btnLogin = findViewById<Button>(R.id.btn_register)
        btnLogin.setOnClickListener {
            if (etEmail.text.toString() != "" && etPassword.text.toString() != "") {
                signIn(
                    auth,
                    etEmail.text.toString(),
                    etPassword.text.toString(),
                    tilEmail,
                    tilPassword,
                )
            }
        }

        etEmail.doOnTextChanged { _, _, _, _ ->
            tilEmail.error = null
        }

        etPassword.doOnTextChanged { _, _, _, _ ->
            tilPassword.error = null
        }

        val tvRegister = findViewById<TextView>(R.id.tv_register)
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}