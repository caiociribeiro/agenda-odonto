package com.example.agendaodonto.models

import com.google.firebase.firestore.DocumentReference
import java.sql.Timestamp

data class Consulta(
    val dentista: String,
    val data: Timestamp,
    val dentistaFormData: DentistaFormData,
    val dentistaID: DocumentReference,
    val status: Status,
    val userFeedback: DocumentReference? = null,
    val userFormData: UserFormData,
    val userID: DocumentReference
)



