package com.example.agendaodonto

data class Consulta(
    val data: String,
    val medico: String,
    val arquivos: List<String>
)