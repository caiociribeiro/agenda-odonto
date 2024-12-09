package com.example.agendaodonto.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class Consulta(
    val data: Timestamp,
    val dentistaFormData: DentistaFormData,
    val dentistaID: DocumentReference,
    val status: Status?,
    val userFeedback: DocumentReference?,
    val userFormData: UserFormData?,
    val userID: DocumentReference?
) {
    constructor(
        dentistaFormData: DentistaFormData,
        dentistaID: DocumentReference,
        data: Timestamp
    ) :
            this(
                data = data,
                dentistaFormData = dentistaFormData,
                dentistaID = dentistaID,
                status = null,
                userFeedback = null,
                userFormData = null,
                userID = null
            )
}

