package com.example.agendaodonto

import android.os.Bundle
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView


class AgendarCalendarioActivity : CommonInterfaceActivity() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var chipGroup: ChipGroup
    private lateinit var btnNext: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agendar_calendario)

        calendarView = findViewById(R.id.calendarView)
        chipGroup = findViewById(R.id.chipGroup)
        btnNext = findViewById(R.id.btn_next)

    }

    override fun onStart() {
        super.onStart()

        val dentistaID = intent.getStringExtra("dentistaId") ?: ""

        if (dentistaID.isNotEmpty()) {
            carregarDiasDisponiveis(dentistaID)
        } else {
            Toast.makeText(this, "Erro ao carregar dentista.", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnNext.setOnClickListener {
            val chipSelecionado = chipGroup.checkedChipId
            if (chipSelecionado != View.NO_ID) {
                val chip = findViewById<Chip>(chipSelecionado)
                val horarioSelecionado = chip.text.toString()
                Toast.makeText(this, "Horário selecionado: $horarioSelecionado", Toast.LENGTH_SHORT)
                    .show()

            } else {
                Toast.makeText(this, "Selecione um horário antes de avançar.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        calendarView.setOnDateChangedListener { _, date, selected ->
            if (selected) {
                val diaSelecionado = "${date.day}/${date.month + 1}/${date.year}"
                carregarHorariosDisponiveis(diaSelecionado, dentistaID)
            }
        }

    }

    private fun carregarDiasDisponiveis(dentistaID: String) {
        val db = FirebaseFirestore.getInstance()
        val disponibilidadeRef =
            db.collection("dentistas").document(dentistaID).collection("disponibilidade")

        disponibilidadeRef.get().addOnSuccessListener { querySnapshot ->
            val diasComDisponibilidade = mutableListOf<CalendarDay>()

            for (document in querySnapshot.documents) {
                val data = document.id
                val partes = data.split("-") // Divide em ano, mês e dia
                if (partes.size == 3) {
                    val year = partes[0].toInt()
                    val month = partes[1].toInt()
                    val day = partes[2].toInt()

                    diasComDisponibilidade.add(CalendarDay.from(year, month, day))
                }
            }

            atualizarCalendario(diasComDisponibilidade)
        }.addOnFailureListener { e ->
            Toast.makeText(
                this,
                "Erro ao carregar disponibilidade: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun atualizarCalendario(diasDisponiveis: List<CalendarDay>) {
        calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                return day in diasDisponiveis
            }

            override fun decorate(view: DayViewFacade) {
                view.addSpan(object : ForegroundColorSpan(
                    ContextCompat.getColor(
                        this@AgendarCalendarioActivity,
                        R.color.primary
                    )
                ) {})
                view.addSpan(object : BackgroundColorSpan(
                    ContextCompat.getColor(
                        this@AgendarCalendarioActivity,
                        R.color.accent
                    )
                ) {})
            }
        })

        calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                return day !in diasDisponiveis
            }

            override fun decorate(view: DayViewFacade) {
                view.setDaysDisabled(true)
            }
        })
    }

    private fun carregarHorariosDisponiveis(dataSelecionada: String, dentistaID: String) {
        val db = FirebaseFirestore.getInstance()
        val dataFormatada =
            dataSelecionada.replace("/", "-") // Converte "1/12/2024" para "2024-12-01"
        val horariosRef =
            db.collection("dentistas").document(dentistaID).collection("disponibilidade")
                .document(dataFormatada)

        horariosRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val horariosDisponiveis = document.get("horarios") as? List<String> ?: emptyList()
                atualizarChips(horariosDisponiveis)
            } else {
                atualizarChips(emptyList())
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Erro ao carregar horários: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun atualizarChips(horarios: List<String>) {
        chipGroup.removeAllViews()

        horarios.forEach { horario ->
            val chip = Chip(this)
            chip.text = horario
            chip.isClickable = true
            chip.isCheckable = true

            chipGroup.addView(chip)
        }

        if (horarios.isEmpty()) {
            Toast.makeText(this, "Nenhum horário disponível para esta data.", Toast.LENGTH_SHORT)
                .show()
        }
    }

}
