package com.example.agendaodonto

import android.content.Intent
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.firebase.firestore.FirebaseFirestore
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView


class AgendarCalendarioActivity : CommonInterfaceActivity() {

    private lateinit var tvDentistaName: TextView
    private lateinit var tvSubtitle: TextView
    private lateinit var calendarView: MaterialCalendarView
    private lateinit var chipGroup: ChipGroup
    private lateinit var btnNext: MaterialButton
    private lateinit var loadingHorarios: LinearProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(
            R.layout.activity_agendar_calendario,
            findViewById(R.id.content_frame)
        )

        calendarView = findViewById(R.id.calendarView)
        chipGroup = findViewById(R.id.chipGroup)
        btnNext = findViewById(R.id.btn_next)

    }

    override fun onStart() {
        super.onStart()

        loadingHorarios = findViewById(R.id.loading_horarios)

        val dentistaID = intent.getStringExtra("dentistaID") ?: ""
        val dentistaName = intent.getStringExtra("dentistaName") ?: ""

        tvDentistaName = findViewById(R.id.tv_dentista_name)
        tvDentistaName.text = dentistaName

        tvSubtitle = findViewById(R.id.tv_subtitle)
        tvSubtitle.text = getString(R.string.calendario_subtitle)

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
                val diaSelecionado = "${calendarView.selectedDate.year}-${
                    (calendarView.selectedDate.month + 1).toString().padStart(2, '0')
                }-${calendarView.selectedDate.day.toString().padStart(2, '0')}"

                val intent = Intent(this, AgendarFormActivity::class.java)
                intent.putExtra("dentistaID", dentistaID)
                intent.putExtra("dentistaName", dentistaName)
                intent.putExtra("dia", diaSelecionado)
                intent.putExtra("horario", horarioSelecionado)
                startActivity(intent)


            } else {
                Toast.makeText(this, "Selecione um horário antes de avançar.", Toast.LENGTH_SHORT)
                    .show()
            }
        }



        calendarView.setOnDateChangedListener { _, date, selected ->
            if (selected) {
                val diaSelecionado = "${date.year}-${
                    (date.month + 1).toString().padStart(2, '0')
                }-${date.day.toString().padStart(2, '0')}"
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
                val partes = data.split("-")
                if (partes.size == 3) {
                    val year = partes[0].toInt()
                    val month = partes[1].toInt() - 1
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
        for (dia in diasDisponiveis) {
            Log.e("data", dia.toString())
        }

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
            }
        })

        calendarView.addDecorator(object : DayViewDecorator {
            override fun shouldDecorate(day: CalendarDay): Boolean {
                return day !in diasDisponiveis
            }

            override fun decorate(view: DayViewFacade) {
                view.setDaysDisabled(true)
                view.addSpan(object : ForegroundColorSpan(
                    ContextCompat.getColor(
                        this@AgendarCalendarioActivity,
                        R.color.grey_lt
                    )
                ) {})
            }
        })
    }

    private fun carregarHorariosDisponiveis(dataSelecionada: String, dentistaID: String) {
        loadingHorarios.visibility = View.VISIBLE

        atualizarChips(emptyList())

        Log.e("Dentista", dentistaID)
        Log.e("Dia Selecionado", dataSelecionada)

        val db = FirebaseFirestore.getInstance()
        val horariosRef =
            db.collection("dentistas").document(dentistaID).collection("disponibilidade")
                .document(dataSelecionada)

        horariosRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val horarios =
                    document.get("horarios") as? Map<String, Boolean> ?: emptyMap()

                val horariosDisponiveis =
                    horarios.filter { it.value }.keys.sortedBy { it }
                atualizarChips(horariosDisponiveis)
            } else {
                atualizarChips(emptyList())
            }

            loadingHorarios.visibility = View.GONE
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

            chip.setChipBackgroundColorResource(R.color.white)
            chip.setTextColor(getColor(R.color.grey))

            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    chip.setChipBackgroundColorResource(R.color.primary)
                    chip.setTextColor(getColor(R.color.grey_ltr))
                } else {
                    chip.setChipBackgroundColorResource(R.color.white)
                    chip.setTextColor(getColor(R.color.grey))
                }
            }

            chipGroup.addView(chip)
        }
    }
}
