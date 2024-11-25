package com.example.agendaodonto.adapters

import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.R
import com.example.agendaodonto.models.Notification

class NotificationsAdapter(
    private val notifications: MutableList<Notification>
) : RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.tv_title)
        val descriptionTextView: TextView = itemView.findViewById(R.id.tv_description)
        val btnDelete: Button = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification, parent, false)
        return NotificationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]

        holder.titleTextView.text = notification.title
        holder.descriptionTextView.text = notification.message
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    fun removeNotification(notification: Notification) {
        val position = notifications.indexOf(notification)
        if (position != -1) {
            notifications.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}