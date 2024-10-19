package com.example.agendaodonto.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.R
import com.example.agendaodonto.models.ConsultaPaciente

class ConsultaPacienteAdapter(
    private var consultations: List<ConsultaPaciente>,
    private val onItemClick: (ConsultaPaciente) -> Unit
) :
    RecyclerView.Adapter<ConsultaPacienteAdapter.ConsultationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_consulta_dentista, parent, false)
        return ConsultationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsultationViewHolder, position: Int) {
        val consultation = consultations[position]

        holder.tvPacienteName.text = consultation.paciente
        holder.tvDate.text = consultation.date
        holder.card.setOnClickListener {
            onItemClick(consultation)
        }
    }

    override fun getItemCount(): Int = consultations.size

    fun updateData(newConsultations: List<ConsultaPaciente>) {
        consultations = newConsultations
        notifyDataSetChanged()
    }

    class ConsultationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tv_data_consulta)
        val tvPacienteName: TextView = itemView.findViewById(R.id.tv_nome_paciente)
        val card: LinearLayout = itemView.findViewById(R.id.ll_card)
    }
}