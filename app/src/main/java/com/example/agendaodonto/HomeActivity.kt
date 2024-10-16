package com.example.agendaodonto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_home, findViewById(R.id.content_frame))

        // Botão Agendar Consulta
        val agendarConsulta: Button = findViewById(R.id.btn_home_agendar)
        agendarConsulta.setOnClickListener {
            val intent = Intent(this, DoctorListActivity::class.java)
            startActivity(intent)
        }

        // Botão Lista de Médicos
        val medicosButton: Button = findViewById(R.id.btn_home_medicos)
        medicosButton.setOnClickListener {
            val intent = Intent(this, ProfileListActivity::class.java)
            startActivity(intent)
        }
    }
}
