package com.example.agendaodonto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class HomeActivity : CommonInterfaceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_home, findViewById(R.id.content_frame))

        val pageName = findViewById<TextView>(R.id.tv_page_name)
        pageName.text = getString(R.string.home)

        val agendarConsulta: Button = findViewById(R.id.btn_home_agendar)
        agendarConsulta.setOnClickListener {
            val intent = Intent(this, AgendarListaDentistaActivity::class.java)
            startActivity(intent)
        }

        val historicoConsulta: Button = findViewById(R.id.btn_home_historico)
        historicoConsulta.setOnClickListener {
            val intent = Intent(this, HistoricoConsultasActivity::class.java)
            startActivity(intent)
        }

        checkPendingEvaluations()


    }


    fun checkPendingEvaluations() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val consultaRefs = document.get("consultas") as? List<DocumentReference> ?: return@addOnSuccessListener

                for (ref in consultaRefs) {
                    ref.get().addOnSuccessListener { consultaDoc ->
                        val formPendente = consultaDoc.getBoolean("status.formPendente")
                        val avaliada = consultaDoc.getBoolean("status.avaliada")

                        if ((formPendente == false) && (avaliada == false)) {
                            showEvaluationPopup(ref)
                            return@addOnSuccessListener // Mostra apenas um pop-up por vez
                        }
                    }
                }
            }
    }

    // Método para mostrar o pop-up de avaliação
    fun showEvaluationPopup(consultaRef: DocumentReference) {
        val builder = AlertDialog.Builder(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rating_popup, null)
        builder.setView(dialogView)

        val ratingBar = dialogView.findViewById<RatingBar>(R.id.rating_bar)
        val commentEditText = dialogView.findViewById<EditText>(R.id.comment_edit_text)
        val dentistFeedbackTextView = dialogView.findViewById<TextView>(R.id.tv_dentist_feedback)

        // Buscar o nome do dentista para exibir no texto
        consultaRef.get().addOnSuccessListener { consultaDoc ->
            val dentistRef = consultaDoc.getDocumentReference("dentistaID")
            dentistRef?.get()?.addOnSuccessListener { dentistDoc ->
                val dentistName = dentistDoc.getString("name") ?: "o dentista"
                dentistFeedbackTextView.text = "Nos conte o que você achou da sua consulta com Dr. $dentistName"
            }
        }

        builder.setTitle("Avalie sua Consulta")
        builder.setPositiveButton("Enviar") { _, _ ->
            val rating = ratingBar.rating
            val comment = commentEditText.text.toString()
            saveEvaluation(consultaRef, rating, comment)
        }
        builder.setNegativeButton("Cancelar", null)

        val dialog = builder.create()
        dialog.show()
    }


    // Método para salvar a avaliação no Firestore
    fun saveEvaluation(consultaRef: DocumentReference, rating: Float, comment: String) {
        val db = FirebaseFirestore.getInstance()
        val ratingsCollection = db.collection("ratings")

        val newRating = hashMapOf(
            "comment" to comment,
            "rating" to rating,
            "date" to com.google.firebase.Timestamp.now(),
            "consultaID" to consultaRef.path,
            "userID" to FirebaseAuth.getInstance().currentUser?.uid
        )

        ratingsCollection.add(newRating)
            .addOnSuccessListener { ratingDocRef ->
                // Atualizar a consulta com a referência da nova avaliação
                consultaRef.update(
                    mapOf(
                        "status.avaliada" to true,
                        "userFeedback" to ratingDocRef
                    )
                ).addOnSuccessListener {
                    Log.i("HistoricoConsultas", "Consulta atualizada com a avaliação!")

                    // Buscar a referência do dentista na consulta
                    consultaRef.get()
                        .addOnSuccessListener { consultaDoc ->
                            val dentistRef = consultaDoc.getDocumentReference("dentistaID")
                            if (dentistRef != null) {
                                addRatingToDentist(dentistRef, ratingDocRef)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("HistoricoConsultas", "Erro ao buscar a consulta: ${e.message}")
                        }
                }.addOnFailureListener { e ->
                    Log.e("HistoricoConsultas", "Erro ao atualizar a consulta: ${e.message}")
                }
            }
            .addOnFailureListener { e ->
                Log.e("HistoricoConsultas", "Erro ao salvar a avaliação: ${e.message}")
            }
    }

    // Método auxiliar para adicionar a avaliação ao array de ratings do dentista
    private fun addRatingToDentist(dentistRef: DocumentReference, ratingDocRef: DocumentReference) {
        dentistRef.update("ratings", FieldValue.arrayUnion(ratingDocRef))
            .addOnSuccessListener {
                Log.i("HistoricoConsultas", "Avaliação adicionada ao dentista com sucesso!")
            }
            .addOnFailureListener { e ->
                Log.e("HistoricoConsultas", "Erro ao adicionar avaliação ao dentista: ${e.message}")
            }
    }


}