package com.example.agendaodonto

import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.adapters.NotificationsAdapter
import com.example.agendaodonto.models.Notification

class NotificationsActivity : CommonInterfaceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_notifications, findViewById(R.id.content_frame))

        val pageName = findViewById<TextView>(R.id.tv_page_name)
        pageName.text = getString(R.string.settings_notification)

        val recyclerView: RecyclerView = findViewById(R.id.rv_notifications)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val notifications = loadNotifications()

        val adapter = NotificationsAdapter(notifications)
        recyclerView.adapter = adapter
    }

    private fun loadNotifications(): MutableList<Notification> {
        return mutableListOf(
            Notification(
                "Consulta amanha!",
                "Sua proxima consulta eh amanha as 16:30. Ate la!",
                0,
                false
            ),
            Notification(
                "Feriado do dia 15/11",
                "Funcionaremos normalmente no feriado. Agende sua consulta.",
                0,
                false
            )
        )
    }
}