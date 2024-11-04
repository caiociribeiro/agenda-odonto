package com.example.agendaodonto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import java.util.Locale

class AgendarCalendarioActivity : CommonInterfaceActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var availableTimesTextView: TextView
    private lateinit var doctorSpinner: Spinner // Spinner para selecionar médicos
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(
            R.layout.activity_agendar_calendario,
            findViewById(R.id.content_frame)
        )

        val pageName = findViewById<TextView>(R.id.tv_page_name)
        pageName.text = "Agendamento"

        val locale = Locale("pt", "BR")
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Inicializa os componentes da View
        calendarView = findViewById(R.id.calendarView)
        availableTimesTextView = findViewById(R.id.available_times)
        doctorSpinner = findViewById(R.id.doctor_spinner)

        // Recebe o nome do médico selecionado a partir do Intent
        val selectedDoctorName = intent.getStringExtra("doctorName")
        Log.d("Doctor Name: ", selectedDoctorName.toString())

        // Lista de todos os médicos
        val doctors = mutableListOf("Dr. João", "Dra. Maria", "Dr. Pedro", "Dra. Ana")

        // Organiza a lista para colocar o médico selecionado em primeiro lugar
        setupDoctorSpinner(selectedDoctorName, doctors)

        // Quando uma data for selecionada
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = "$dayOfMonth/${month + 1}/$year"
            loadAvailableTimes(selectedDate)
        }

        btnNext = findViewById(R.id.btn_next)
        btnNext.setOnClickListener {
            val intent = Intent(this, AgendarFormActivity::class.java)
            startActivity(intent)

            // TODO: enviar via intent info do dia/hora escolhido
        }
    }

    // Configuração do Spinner de médicos
    private fun setupDoctorSpinner(selectedDoctorName: String?, doctors: MutableList<String>) {
        // Coloca o médico selecionado como o primeiro da lista
        if (selectedDoctorName != null && doctors.contains(selectedDoctorName)) {
            doctors.remove(selectedDoctorName) // Remove da lista original
            doctors.add(0, selectedDoctorName) // Adiciona como o primeiro
        }

        // Cria um ArrayAdapter para alimentar o Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, doctors)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        doctorSpinner.adapter = adapter

        // Configura o listener para capturar a seleção do médico
        doctorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedDoctor = parent.getItemAtPosition(position).toString()
                Toast.makeText(
                    applicationContext,
                    "Médico selecionado: $selectedDoctor",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Nenhum item foi selecionado, pode deixar vazio ou tratar
            }
        }
    }

    // Exemplo simples: mostrar horários disponíveis
    private fun loadAvailableTimes(date: String) {
        val availableTimes = listOf("12:00", "15:25", "17:30") // Ajuste para dados reais
        availableTimesTextView.text = availableTimes.joinToString(", ")
    }
}
