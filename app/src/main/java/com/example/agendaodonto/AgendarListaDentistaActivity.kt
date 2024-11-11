package com.example.agendaodonto

import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.adapters.DentistaAdapter
import com.example.agendaodonto.models.Dentista
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.InputStreamReader

class AgendarListaDentistaActivity : CommonInterfaceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(
            R.layout.activity_agendar_lista_dentista,
            findViewById(R.id.content_frame)
        )

        val pageName = findViewById<TextView>(R.id.tv_page_name)
        pageName.text = "Agendamento"

        val doctorList = loadDoctorList()

        val recyclerView = findViewById<RecyclerView>(R.id.rv_dentistas_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DentistaAdapter(doctorList)
    }

    private fun loadDoctorList(): List<Dentista> {
        val inputStream = assets.open("doctors.json")
        val reader = InputStreamReader(inputStream)
        val doctorsListType = object : TypeToken<List<Dentista>>() {}.type
        return Gson().fromJson(reader, doctorsListType)
    }
}