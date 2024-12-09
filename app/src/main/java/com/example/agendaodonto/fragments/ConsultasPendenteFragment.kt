package com.example.agendaodonto.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.ConsultaFormDentistaActivity
import com.example.agendaodonto.R
import com.example.agendaodonto.adapters.ConsultaPacienteAdapter
import com.example.agendaodonto.models.ConsultaPaciente
import com.example.agendaodonto.ui.components.CustomDividerItemDecoration
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class ConsultasPendenteFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ConsultaPacienteAdapter
    private val consultas = mutableListOf<ConsultaPaciente>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_consultas_pendente, container, false)

        recyclerView = view.findViewById(R.id.rv_consultas)

        adapter = ConsultaPacienteAdapter(consultas) { consulta ->
            val intent = Intent(requireContext(), ConsultaFormDentistaActivity::class.java)
            intent.putExtra("pacienteName", consulta.paciente)
            intent.putExtra("consultaData", consulta.date)
            intent.putExtra("consultaId", consulta.id)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val divider = ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!
        val itemDecoration = CustomDividerItemDecoration(requireContext(), divider)
        recyclerView.addItemDecoration(itemDecoration)

        fetchConsultasPendentes()

        return view
    }

    private fun fetchConsultasPendentes() {
        val db = FirebaseFirestore.getInstance()
        val dentistaId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        Log.i("dentistaID", dentistaId)

        db.collection("dentistas").document(dentistaId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val consultaRefs =
                        document.get("consultas") as? List<DocumentReference> ?: emptyList()

                    if (consultaRefs.isNotEmpty()) {
                        fetchDetalhesConsulta(consultaRefs)
                    }
                }
            }

    }

    private fun fetchDetalhesConsulta(consultaRefs: List<DocumentReference>) {
        consultas.clear()

        for (ref in consultaRefs) {
            Log.i("consultaRef", ref.toString())
            ref.get()
                .addOnSuccessListener { document ->
                    if (document.exists() && document.getBoolean("status.formPendente") == true) {
                        val id = document.id
                        val pacienteRef = document.getDocumentReference("userID")
                        val timestamp = document.getTimestamp("data")
                        val data = timestamp?.toDate()?.let { date ->
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            formatter.format(date)
                        } ?: "Data Desconhecida"

                        pacienteRef?.get()
                            ?.addOnSuccessListener { pacienteDocument ->
                                val paciente = pacienteDocument.getString("name") ?: "Erro"

                                Log.i("nomePaciente", paciente)

                                consultas.add(ConsultaPaciente(id, paciente, data))
                                adapter.notifyItemInserted(consultas.size - 1);
                            }
                    }
                }
        }
    }
}