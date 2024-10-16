package com.example.agendaodonto

import ProfilesAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.DoctorProfileActivity
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.io.InputStreamReader


class ProfileListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_list)

        // Carrega a lista de médicos
        val doctorList = loadDoctorList()

        // Configura o RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.rv_doctor_list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = ProfilesAdapter(doctorList) // Atribui o adaptador


    }

    // Função para carregar a lista de médicos do arquivo JSON
    fun loadDoctorList(): List<Doctor> {
        val inputStream = assets.open("doctors.json")
        val reader = InputStreamReader(inputStream)
        val doctorsListType = object : TypeToken<List<Doctor>>() {}.type
        return Gson().fromJson(reader, doctorsListType)
    }
}