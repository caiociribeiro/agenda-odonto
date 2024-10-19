package com.example.agendaodonto.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.ConsultaFormDentistaActivity
import com.example.agendaodonto.R
import com.example.agendaodonto.adapters.ConsultaPacienteAdapter
import com.example.agendaodonto.models.ConsultaPaciente

class ConsultasPendenteFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_consultas_pendente, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_consultas)

        val consultations = listOf(
            ConsultaPaciente("Jacinto Pinto", "12/09/2024") // Hardcoded item
        )

        val adapter = ConsultaPacienteAdapter(consultations) { consultation ->
            val intent = Intent(requireContext(), ConsultaFormDentistaActivity::class.java)
            intent.putExtra("PATIENT_NAME", consultation.paciente)
            intent.putExtra("CONSULTATION_DATE", consultation.date)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return view
    }
}