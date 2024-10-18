package com.example.agendaodonto

import android.os.Bundle
import android.widget.TextView

class NotificationsActivity : CommonInterfaceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_notifications, findViewById(R.id.content_frame))

        val pageName = findViewById<TextView>(R.id.tv_page_name)
        pageName.text = getString(R.string.settings_notification)

    }
}