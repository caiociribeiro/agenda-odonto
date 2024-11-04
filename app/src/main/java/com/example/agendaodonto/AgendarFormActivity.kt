package com.example.agendaodonto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class AgendarFormActivity : CommonInterfaceActivity() {
    private lateinit var btnConfirmar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_agendar_form, findViewById(R.id.content_frame))

        val pageName = findViewById<TextView>(R.id.tv_page_name)
        pageName.text = "Agendamento"

        btnConfirmar = findViewById(R.id.btn_confirmar)
        btnConfirmar.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()

            // TODO: conexao com o firebase (post)
        }


    }
}