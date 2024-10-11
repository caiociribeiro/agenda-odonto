package com.example.agendaodonto

import DoctorAdapter
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.InputStreamReader

class DoctorListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_doctor_list, findViewById(R.id.content_frame))

        val pageName = findViewById<TextView>(R.id.tv_page_name)
        pageName.text = "Agendamento"

        // Carrega a lista de médicos
        val doctorList = loadDoctorList()

        // Configura o RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.rv_doctor_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = DoctorAdapter(doctorList) // Atribui o adaptador
    }

    // Função para carregar a lista de médicos do arquivo JSON
    private fun loadDoctorList(): List<Doctor> {
        val inputStream = assets.open("doctors.json")
        val reader = InputStreamReader(inputStream)
        val doctorsListType = object : TypeToken<List<Doctor>>() {}.type
        return Gson().fromJson(reader, doctorsListType)
    }
}