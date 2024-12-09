package com.example.agendaodonto

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class DoctorProfileActivity : CommonInterfaceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_doctor_profile, findViewById(R.id.content_frame))

        val pageName = findViewById<TextView>(R.id.tv_page_name)
        pageName.text = "Perfil de Dentista"

        val doctorName = intent.getStringExtra("name") ?: "Nome não disponível"
        val doctorSpecialty =
            intent.getStringExtra("especialidade") ?: "Especialidade não disponível"
        val doctorRating = intent.getDoubleExtra("rating", 3.5)

        val dentistNameTextView = findViewById<TextView>(R.id.tv_dentist_name)
        val specialtyTextView = findViewById<TextView>(R.id.tv_specialty)

        dentistNameTextView.text = doctorName
        specialtyTextView.text = doctorSpecialty

        setRating(doctorRating)
    }

    private fun setRating(rating: Double) {
        val starViews = listOf(
            findViewById<ImageView>(R.id.star1),
            findViewById<ImageView>(R.id.star2),
            findViewById<ImageView>(R.id.star3),
            findViewById<ImageView>(R.id.star4),
            findViewById<ImageView>(R.id.star5)
        )

        for (i in starViews.indices) {
            when {
                i + 1 <= rating.toInt() -> starViews[i].setImageResource(R.drawable.ic_star_full) // Estrela cheia
                i < rating && rating % 1 != 0.0 -> starViews[i].setImageResource(R.drawable.ic_star_half) // Meia estrela
                else -> starViews[i].setImageResource(R.drawable.ic_star_empty) // Estrela vazia
            }
        }
    }

}