package com.example.agendaodonto

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ConsultaFormDentistaActivity : AppCompatActivity() {
    private lateinit var btnConfirmar: Button
    // TODO: instanciar componentes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consulta_form_dentista)

        val pacienteName = intent.getStringExtra("PATIENT_NAME")
        val date = intent.getStringExtra("CONSULTATION_DATE")

        val tvSubtitle: TextView = findViewById(R.id.tv_subtitle)
        val tvDate: TextView = findViewById(R.id.tv_consulta_date)

        tvSubtitle.text = "Dados de consulta de $pacienteName"
        tvDate.text = date
    }

    override fun onStart() {
        super.onStart()

        btnConfirmar = findViewById(R.id.btn_enviar)
        btnConfirmar.setOnClickListener {
            // TODO: post no firebase
        }
    }
}