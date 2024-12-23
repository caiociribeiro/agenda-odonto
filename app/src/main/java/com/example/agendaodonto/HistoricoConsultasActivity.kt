package com.example.agendaodonto

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.adapters.ConsultaAdapter
import com.example.agendaodonto.models.Consulta
import com.example.agendaodonto.models.DentistaFormData
import com.example.agendaodonto.ui.components.CustomDividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HistoricoConsultasActivity : CommonInterfaceActivity() {

    private lateinit var selectedYear: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var consultaAdapter: ConsultaAdapter
    private lateinit var tvNoInfo: TextView
    private val consultas = mutableListOf<Consulta>()

    private val db = FirebaseFirestore.getInstance()
    private val userId: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(
            R.layout.activity_historico_consultas,
            findViewById(R.id.content_frame)
        )

        val pageName = findViewById<TextView>(R.id.tv_page_name)
        pageName.text = getString(R.string.historico)

        recyclerView = findViewById(R.id.rv_consultas)
        tvNoInfo = findViewById(R.id.tv_no_info)
        recyclerView.layoutManager = LinearLayoutManager(this)

        consultaAdapter = ConsultaAdapter(consultas)
        recyclerView.adapter = consultaAdapter

        selectedYear = findViewById(R.id.btn_selected_year)

        selectedYear.setOnClickListener {
            showYearPickerDialog()
        }

        fetchConsultas()
    }

    private fun fetchConsultas() {
        Log.i("HistoricoConsultas", "Buscando consultas para o usuário: $userId")

        if (userId.isEmpty()) {
            Log.e("HistoricoConsultas", "User ID está vazio.")
            tvNoInfo.visibility = View.VISIBLE
            tvNoInfo.text = "Erro ao buscar usuário."
            return
        }

        db.collection("users").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (!isActivityAlive()) return@addOnSuccessListener

                if (document.exists()) {
                    val consultaRefs =
                        document.get("consultas") as? List<DocumentReference> ?: emptyList()
                    Log.i("HistoricoConsultas", "Consulta Refs: $consultaRefs")

                    if (consultaRefs.isNotEmpty()) {
                        fetchDetalhesConsultas(consultaRefs)
                    } else {
                        Log.i("HistoricoConsultas", "Nenhuma consulta encontrada.")
                        tvNoInfo.visibility = View.VISIBLE
                    }
                } else {
                    Log.i("HistoricoConsultas", "Documento do usuário não encontrado.")
                    tvNoInfo.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { e ->
                Log.e("HistoricoConsultas", "Erro ao buscar consultas: ${e.message}")
                tvNoInfo.visibility = View.VISIBLE
                tvNoInfo.text = "Erro ao buscar consultas."
            }
    }

    private fun fetchDetalhesConsultas(consultaRefs: List<DocumentReference>) {
        consultas.clear()

        for (ref in consultaRefs) {
            Log.i("HistoricoConsultas", "Buscando detalhes da consulta: ${ref.path}")

            ref.get()
                .addOnSuccessListener { document ->
                    if (!isActivityAlive()) return@addOnSuccessListener

                    if (document.exists()) {

                        // Verificar se o campo formPendente é true, ou seja que a consulta já foi feita
                        val formPendente = document.getBoolean("status.formPendente") ?: true
                        if (formPendente) {
                            Log.i("HistoricoConsultas", "Consulta ignorada, formPendente é true.")
                            return@addOnSuccessListener
                        }

                        val dateTimestamp = document.getTimestamp("data")
                        val date = dateTimestamp?.toDate()?.let { date ->
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
                        } ?: "Data desconhecida"

                        val dentistRef = document.getDocumentReference("dentistaID")
                        val attachments = document.get("dentistaFormData.arquivos") as? List<String>
                            ?: emptyList()
                        val observacoes = document.getString("dentistaFormData.observacoes") ?: ""

                        val dentistaID = document.getDocumentReference("dentistaID")

                        Log.i(
                            "HistoricoConsultas",
                            "Data: $date, Dentista Ref: $dentistRef, Arquivos: $attachments"
                        )

                        dentistRef?.get()
                            ?.addOnSuccessListener { dentistDoc ->
                                if (!isActivityAlive()) return@addOnSuccessListener

                                val doctorName =
                                    dentistDoc.getString("name") ?: "Médico não informado"
                                Log.i("HistoricoConsultas", "Nome do dentista: $doctorName")

                                if (dateTimestamp != null && dentistaID != null)
                                    consultas.add(
                                        Consulta(
                                            DentistaFormData(
                                                attachments,
                                                observacoes
                                            ), dentistaID, dateTimestamp
                                        )
                                    )

                                consultaAdapter.notifyDataSetChanged()
                                tvNoInfo.visibility = View.GONE
                            }
                            ?.addOnFailureListener { e ->
                                Log.e("HistoricoConsultas", "Erro ao buscar dentista: ${e.message}")
                            }
                    } else {
                        Log.e("HistoricoConsultas", "Documento da consulta não encontrado.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("HistoricoConsultas", "Erro ao buscar detalhes da consulta: ${e.message}")
                }
        }
    }

    private fun isActivityAlive(): Boolean {
        return !(isFinishing || isDestroyed)
    }

    private fun showYearPickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_year_picker, null)

        val yearPicker: NumberPicker = dialogView.findViewById(R.id.picker_year)

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        yearPicker.minValue = currentYear - 10
        yearPicker.maxValue = currentYear + 10
        yearPicker.value = currentYear

        val dialogTitleView = layoutInflater.inflate(R.layout.title_dialog_year_picker, null)

        val builder = AlertDialog.Builder(this)
            .setCustomTitle(dialogTitleView)
            .setView(dialogView)

        val dialog: AlertDialog = builder.create()

        dialogView.findViewById<Button>(R.id.btn_ok).setOnClickListener {
            val year = yearPicker.value
            selectedYear.text = year.toString()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}