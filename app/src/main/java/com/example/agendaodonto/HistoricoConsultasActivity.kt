package com.example.agendaodonto

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.adapters.ConsultaAdapter
import com.example.agendaodonto.models.Consulta
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
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
        layoutInflater.inflate(R.layout.activity_historico_consultas, findViewById(R.id.content_frame))

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
                    val consultaRefs = document.get("consultas") as? List<DocumentReference> ?: emptyList()
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
            ref.get()
                .addOnSuccessListener { document ->
                    if (!isActivityAlive()) return@addOnSuccessListener

                    if (document.exists()) {
                        val dateTimestamp = document.getTimestamp("data")
                        val date = dateTimestamp?.toDate()?.let {
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                        } ?: "Data desconhecida"

                        val dentistRef = document.getDocumentReference("dentistaID")
                        val attachments = document.get("dentistaFormData.arquivos") as? List<String> ?: emptyList()
                        val observacoes = document.getString("dentistaFormData.observacoes") ?: "Sem observações"

                        dentistRef?.get()
                            ?.addOnSuccessListener { dentistDoc ->
                                val doctorName = dentistDoc.getString("name") ?: "Médico não informado"

                                consultas.add(Consulta(date, doctorName, attachments, observacoes))
                                consultaAdapter.notifyDataSetChanged()
                                tvNoInfo.visibility = View.GONE
                            }
                            ?.addOnFailureListener { e ->
                                Log.e("HistoricoConsultas", "Erro ao buscar dentista: ${e.message}")
                            }
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