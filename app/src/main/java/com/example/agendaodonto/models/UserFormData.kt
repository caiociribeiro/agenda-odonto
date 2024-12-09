package com.example.agendaodonto.models

data class UserFormData(
    val alergias: String?,
    val escovacao: String?,
    val fuma: Boolean,
    val motivoConsulta: String?,
    val usaAparelho: Boolean
)
