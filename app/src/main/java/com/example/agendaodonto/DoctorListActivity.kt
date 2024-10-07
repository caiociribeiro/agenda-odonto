package com.example.agendaodonto

import DoctorAdapter
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.InputStreamReader

class DoctorListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_list)

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