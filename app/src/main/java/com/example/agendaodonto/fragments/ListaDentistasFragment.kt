package com.example.agendaodonto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.R
import com.example.agendaodonto.adapters.DentistaAdapter
import com.example.agendaodonto.models.Dentista
import com.example.agendaodonto.ui.components.CustomDividerItemDecoration
import com.google.gson.Gson
import java.io.InputStreamReader

class ListaDentistasFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_agendar_lista_dentista, container, false)

        val doctorList = loadDoctorList()

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_doctor_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = DentistaAdapter(doctorList)

        val divider = ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!
        val itemDecoration = CustomDividerItemDecoration(requireContext(), divider)
        recyclerView.addItemDecoration(itemDecoration)

        return view
    }

    private fun loadDoctorList(): List<Dentista> {
        val inputStream = requireContext().assets.open("doctors.json")
        val reader = InputStreamReader(inputStream)
        return Gson().fromJson(reader, Array<Dentista>::class.java).toList()
    }
}

