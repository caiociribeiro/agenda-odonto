import android.Manifest
import android.R.drawable.ic_dialog_info
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.CommonInterfaceActivity
import com.example.agendaodonto.R
import com.example.agendaodonto.adapters.NotificationAdapter
import com.example.agendaodonto.models.Notification
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NotificationsActivity : CommonInterfaceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_notifications, findViewById(R.id.content_frame))

        val pageName = findViewById<TextView>(R.id.tv_page_name)
        pageName.text = getString(R.string.settings_notification)

        val recyclerView: RecyclerView = findViewById(R.id.rv_notifications)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val notifications = mutableListOf<Notification>()
        val adapter = NotificationAdapter(notifications)
        recyclerView.adapter = adapter

        val currentUserId = getCurrentUserId()
        if (currentUserId != null) {
            loadNotificationsFromFirebase(currentUserId, adapter)
            showNotificationForUpcomingConsultation(currentUserId) // Adiciona a exibição de notificação
        } else {
            Toast.makeText(this, "Usuário não autenticado!", Toast.LENGTH_SHORT).show()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

    }

    fun getCurrentUserId(): String? {
        val auth = FirebaseAuth.getInstance()
        return auth.currentUser?.uid
    }

    private fun getDentistName(dentistRef: String, onComplete: (String) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val dentistDocRef = db.document(dentistRef)

        dentistDocRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val dentistName = document.getString("name") ?: "Nome não encontrado"
                    onComplete(dentistName)
                } else {
                    onComplete("Nome não encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao buscar dentista: ${e.message}")
                onComplete("Erro ao buscar nome")
            }
    }

    private fun loadNotificationsFromFirebase(userId: String, adapter: NotificationAdapter) {
        val db = FirebaseFirestore.getInstance()

        // Busca a referência da consulta no campo "proximaConsulta" do usuário
        db.collection("users").document(userId).get()
            .addOnSuccessListener { userDocument ->
                Log.d("Firestore", "Documento do usuário: ${userDocument.data}")

                // Obtendo o campo como DocumentReference
                val proximaConsultaRef = userDocument.getDocumentReference("proximaConsulta")
                Log.d("Firebase", "proximaconsulta: ${userDocument.getDocumentReference("proximaConsulta")}")
                if (proximaConsultaRef != null) {
                    // Usa a referência para buscar os dados da consulta
                    proximaConsultaRef.get()
                        .addOnSuccessListener { consultaDocument ->
                            if (consultaDocument.exists()) {
                                val consultaDate = consultaDocument.getTimestamp("date")?.toDate()
                                val dentistRef = consultaDocument.getString("dentistaID") ?: ""

                                if (consultaDate != null) {
                                    val calendar = Calendar.getInstance()
                                    calendar.time = consultaDate
                                    calendar.add(Calendar.DATE, -1) // Um dia antes da consulta

                                    val currentDate = Calendar.getInstance()

                                    // Verifica se hoje é um dia antes da consulta
                                    if (isSameDay(currentDate, calendar)) {
                                        getDentistName(dentistRef) { dentistName ->
                                            val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(consultaDate)
                                            val title = "Lembrete de Consulta"
                                            val message = "Você tem uma consulta amanhã às $formattedTime com $dentistName."

                                            // Cria a notificação e adiciona ao adaptador
                                            val notification = Notification(
                                                title = title,
                                                message = message,
                                                timestamp = System.currentTimeMillis(),
                                                isRead = false
                                            )
                                            adapter.apply {
                                                this.notifications.clear()
                                                this.notifications.add(notification)
                                                notifyDataSetChanged()
                                            }

                                            // Exibe a notificação para o usuário
                                            showNotification(title, message)
                                        }
                                    }
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Erro ao buscar consulta: ${e.message}")
                        }
                } else {
                    Log.d("Firestore", "Nenhuma referência de consulta encontrada.")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao buscar dados do usuário: ${e.message}")
            }

    }


    // Método para exibir a notificação no dispositivo
    private fun showNotification(title: String, message: String) {
        val notificationManager = NotificationManagerCompat.from(this)
        val channelId = "agendaodonto_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1
                )
            }
        }


        // Construção da notificação
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_bell)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        // Exibe a notificação
        notificationManager.notify(1, notification)
    }


    private fun isSameDay(date1: Calendar, date2: Calendar): Boolean {
        return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
                date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR)
    }

    fun showNotificationForUpcomingConsultation(userId: String) {
        val db = FirebaseFirestore.getInstance()
        val tomorrow = Calendar.getInstance().apply {
            add(Calendar.DATE, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfTomorrow = tomorrow.time

        tomorrow.add(Calendar.DATE, 1)
        val endOfTomorrow = tomorrow.time

        db.collection("consultas")
            .whereEqualTo("userID", "users/$userId")
            .whereGreaterThanOrEqualTo("date", Timestamp(startOfTomorrow))
            .whereLessThan("date", Timestamp(endOfTomorrow))
            .get()
            .addOnSuccessListener { documents ->

                if (documents.isEmpty) {
                    Log.d("Notifications", "Nenhuma consulta encontrada para amanhã.")

                    return@addOnSuccessListener
                }

                val consultation = documents.documents.first()
                val consultationDate = consultation.getTimestamp("date")?.toDate()
                val dentistRef = consultation.getString("dentistaID") ?: ""

                getDentistName(dentistRef) { dentistName ->
                    val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(consultationDate)
                    val notificationTitle = "Consulta Amanhã"
                    val notificationMessage = "Você tem uma consulta amanhã às $formattedTime com $dentistName."

                    Log.d("Notifications", "Exibindo notificação: $notificationMessage")
                    createNotification(notificationTitle, notificationMessage)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Notifications", "Erro ao buscar consultas: ${e.message}")
            }
    }


    private fun createNotification(title: String, message: String) {
        val channelId = "notifications_channel"
        val notificationId = 1

        // Verifique permissões no Android 13 ou superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                Log.e("Notification", "Permissão de notificação não concedida.")
                return
            }
        }

        // Configura o canal de notificação para Android 8.0 (Oreo) ou superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notificações"
            val descriptionText = "Canal para notificações da Agenda Odonto"
            val importance = NotificationManager.IMPORTANCE_HIGH // Prioridade alta
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            // Cria o canal no NotificationManager
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

            Log.d("Notification", "Canal de notificação criado com ID: $channelId")
        }

        // Cria a notificação usando NotificationCompat.Builder
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(ic_dialog_info) // Ícone padrão do Android
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Alta prioridade
            .setAutoCancel(true) // Notificação é descartada ao clicar

        try {
            // Obtém o NotificationManager e exibe a notificação
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            Log.d("Notification", "NotificationManager obtido: $notificationManager")
            notificationManager.notify(notificationId, builder.build())
            Log.d("Notification", "Notificação exibida com sucesso: Título: $title, Mensagem: $message")
        } catch (e: Exception) {
            Log.e("Notification", "Erro ao exibir notificação: ${e.message}")
        }
    }





}