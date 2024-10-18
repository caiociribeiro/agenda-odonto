package com.example.agendaodonto.models

data class Consulta(
    val data: String,
    val medico: String,
    val arquivos: List<String>
)