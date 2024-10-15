package com.example.agendaodonto

import android.os.Bundle
import android.text.InputFilter
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputLayout
import com.niwattep.materialslidedatepicker.SlideDatePickerDialog
import com.niwattep.materialslidedatepicker.SlideDatePickerDialogCallback
import java.util.Calendar
import java.util.Locale

class RegisterActivity : BaseActivity(), SlideDatePickerDialogCallback {
    private lateinit var tilName: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilPasswordConfirm: TextInputLayout

    private lateinit var etDob: EditText
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPasswordConfirm: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        tilName = findViewById(R.id.til_name)
        tilPassword = findViewById(R.id.til_password)
        tilPasswordConfirm = findViewById(R.id.til_password_confirm)

        etName = findViewById(R.id.et_name)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        etPasswordConfirm = findViewById(R.id.et_password_confirm)

        applyInputFilters(etName)
        applyInputFilters(etEmail, true)
        applyInputFilters(etPassword)
        applyInputFilters(etPasswordConfirm)

        val btnRegister: Button = findViewById(R.id.btn_register)

        etEmail.filters = arrayOf(InputFilter { source, start, end, dest, dstart, dend ->
            val input = source.toString().lowercase(Locale.getDefault())
            if (input != source.toString()) {
                return@InputFilter input
            }
            null
        })

        etDob = findViewById(R.id.et_dob)

        etDob.setOnClickListener {
            showSlideDatePickerDialog()
        }

        etDob.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) showSlideDatePickerDialog()
        }

        btnRegister.setOnClickListener {
            if (!passwordsMatch(etPassword.text.toString(), etPasswordConfirm.text.toString())) {
                tilPassword.error = " "
                tilPasswordConfirm.error = "Senhas não coincidem"
                print("test")
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
        return emailPattern.matches(email)
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

}