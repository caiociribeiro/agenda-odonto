package com.example.agendaodonto

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.niwattep.materialslidedatepicker.SlideDatePickerDialog
import com.niwattep.materialslidedatepicker.SlideDatePickerDialogCallback
import java.util.Calendar
import java.util.Locale

class RegisterActivity : BaseActivity(), SlideDatePickerDialogCallback {
    private lateinit var tilName: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilPasswordConfirm: TextInputLayout
    private lateinit var tilDob: TextInputLayout

    private lateinit var etDob: EditText
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPasswordConfirm: EditText

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth

        tilName = findViewById(R.id.til_name)
        tilPassword = findViewById(R.id.til_password)
        tilPasswordConfirm = findViewById(R.id.til_password_confirm)
        tilEmail = findViewById(R.id.til_email)
        tilDob = findViewById(R.id.til_dob)

        etName = findViewById(R.id.et_name)
        etEmail = findViewById(R.id.et_email)
        etDob = findViewById(R.id.et_dob)
        etPassword = findViewById(R.id.et_password)
        etPasswordConfirm = findViewById(R.id.et_password_confirm)

        val btnRegister: Button = findViewById(R.id.btn_register)

        applyInputFilters(etName)
        applyInputFilters(etEmail, true)
        applyInputFilters(etPassword)
        applyInputFilters(etPasswordConfirm)

        etDob.setOnClickListener {
            showSlideDatePickerDialog()
        }

        etDob.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) showSlideDatePickerDialog()
        }

        btnRegister.setOnClickListener {
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val dob = etDob.text.toString()
            val password = etPassword.text.toString()
            val passwordConfirm = etPasswordConfirm.text.toString()

            if (isValidUserInformation(name, email, dob, password, passwordConfirm)) {
                Toast.makeText(
                    baseContext,
                    "Tudo ok",
                    Toast.LENGTH_SHORT
                ).show()
                signUp(auth, name, email, dob, password)
            }
        }

        etPassword.doOnTextChanged { _, _, _, _ ->
            tilPassword.error = null
            tilPasswordConfirm.error = null
        }

        etPasswordConfirm.doOnTextChanged { _, _, _, _ ->
            tilPassword.error = null
            tilPasswordConfirm.error = null
        }

        etName.doOnTextChanged { _, _, _, _ ->
            tilName.error = null
        }

        etEmail.doOnTextChanged { _, _, _, _ ->
            tilEmail.error = null
        }
    }

    override fun onPositiveClick(day: Int, month: Int, year: Int, calendar: Calendar) {
        val formattedDate = String.format("%02d/%02d/%d", day, month, year)
        etDob.setText(formattedDate)
    }

    private fun passwordsMatch(password: String, passwordConfirm: String): Boolean {
        return password == passwordConfirm
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        if (!emailPattern.matches(email)) {
            tilEmail.error = "Insira um e-mail valido."
            return false
        }

        return true
    }

    private fun isValidDob(dob: String): Boolean {
        if (dob.isBlank()) {
            tilDob.error = "Insira uma data de nascimento"
            return false
        }
        return true
    }

    private fun isValidName(name: String): Boolean {
        val namePattern = Regex("^[A-Za-zÀ-ÖØ-öø-ÿ '-]{2,}$")

        if (name.isBlank() || !namePattern.matches(name)) {
            tilName.error = "Nome invalido. Insira outro"
            return false
        }

        return true
    }

    private fun isValidPassword(password: String, passwordConfirm: String): Boolean {
        if (!passwordsMatch(password, passwordConfirm)) {
            tilPassword.error = " "
            tilPasswordConfirm.error = "Senhas não coincidem"
            return false
        }

        if (password.length !in 8..36) {
            tilPassword.error = "A senha precisa ter de 8 a 36 caracteres."
            return false
        }

        return true
    }

    private fun showSlideDatePickerDialog() {
        SlideDatePickerDialog.Builder()
            .setLocale(Locale("pt", "BR"))
            .setThemeColor(ContextCompat.getColor(this, R.color.accent))
            .setHeaderTextColor(R.color.grey)
            .setCancelText("Cancelar")
            .setConfirmText("Ok")
            .build()
            .show(supportFragmentManager, "TAG")
    }


    private fun isValidUserInformation(
        name: String,
        email: String,
        dob: String,
        password: String,
        passwordConfirm: String
    ): Boolean {
        var valid = true;

        if (!isValidName(name)) valid = false
        if (!isValidEmail(email)) valid = false
        if (!isValidPassword(password, passwordConfirm)) valid = false
        if (!isValidDob(dob)) valid = false

        return valid
    }

}