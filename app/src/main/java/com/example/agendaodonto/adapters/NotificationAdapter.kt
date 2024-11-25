package com.example.agendaodonto.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.R
import com.example.agendaodonto.models.Notification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class NotificationAdapter(
    val notifications: MutableList<Notification>
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

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

        holder.btnDelete.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                removeNotificationFromFirestore(userId, notification)
                removeNotification(notification)
            }
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    private fun removeNotificationFromFirestore(userId: String, notification: Notification) {
        val db = FirebaseFirestore.getInstance()
        val notificationsRef = db.collection("users")
            .document(userId)
            .collection("notifications")

        notificationsRef.whereEqualTo("title", notification.title) // Identifique o documento corretamente
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    notificationsRef.document(document.id).delete()
                        .addOnSuccessListener {
                            // Sucesso ao deletar
                        }
                        .addOnFailureListener { e ->
                            e.printStackTrace()
                        }
                }
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    private fun removeNotification(notification: Notification) {
        val position = notifications.indexOf(notification)
        if (position != -1) {
            notifications.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
