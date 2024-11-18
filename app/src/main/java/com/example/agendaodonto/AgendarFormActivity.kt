package com.example.agendaodonto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore


class AgendarFormActivity : CommonInterfaceActivity() {
    private lateinit var rgAparelho: RadioGroup
    private lateinit var rgFumante: RadioGroup
    private lateinit var rgAlergico: RadioGroup
    private lateinit var btnConfirmar: Button

    // TODO: instanciar os outros componentes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_agendar_form, findViewById(R.id.content_frame))

        val pageName = findViewById<TextView>(R.id.tv_page_name)
        pageName.text = "Agendamento"

        btnConfirmar = findViewById(R.id.btn_confirmar)
        btnConfirmar.setOnClickListener {
            rgAparelho = findViewById(R.id.rg_aparelho)
            rgFumante = findViewById(R.id.rg_fumante)
            rgAlergico = findViewById(R.id.rg_alergico)

            // TODO: instanciar os outros componentes

            val db = FirebaseFirestore.getInstance()

            // TODO: post no firebase

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()

        }


    }
}