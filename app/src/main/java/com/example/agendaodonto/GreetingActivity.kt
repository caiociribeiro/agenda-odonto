package com.example.agendaodonto

import android.content.Intent
import android.os.Bundle
import android.widget.Button

class GreetingActivity : BaseActivity() {
    lateinit var btnPaciente: Button
    lateinit var btnDentista: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_greeting)

        btnPaciente = findViewById(R.id.btn_paciente)
        btnDentista = findViewById(R.id.btn_dentista)

        btnPaciente.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        btnDentista.setOnClickListener {
            val intent = Intent(this, LoginDentistaActivity::class.java)
            startActivity(intent)
        }

    }
}