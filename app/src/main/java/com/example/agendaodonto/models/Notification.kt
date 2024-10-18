package com.example.agendaodonto.models

data class Notification(
    val title: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val isRead: Boolean = false
)
