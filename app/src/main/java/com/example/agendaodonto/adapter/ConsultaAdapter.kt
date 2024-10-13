package com.example.agendaodonto.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.Consulta
import com.example.agendaodonto.R

class ConsultaAdapter(private val consultas: List<Consulta>) :
    RecyclerView.Adapter<ConsultaAdapter.ConsultaViewHolder>() {

    class ConsultaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvData: TextView = view.findViewById(R.id.tv_data_consulta)
        val tvMedico: TextView = view.findViewById(R.id.tv_nome_medico)
        val expandedView: LinearLayout = view.findViewById(R.id.expanded_view)
        val collapsedView: LinearLayout = view.findViewById(R.id.collapsed_view)
        val tvArquivos: TextView = view.findViewById(R.id.tv_arquivo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_consulta, parent, false)
        return ConsultaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsultaViewHolder, position: Int) {
        val consulta = consultas[position]
        holder.tvData.text = consulta.data
        holder.tvMedico.text = consulta.medico
        holder.tvArquivos.text = consulta.arquivos.joinToString("\n")

        // Expand/Collapse logic
        holder.collapsedView.setOnClickListener {
            if (holder.expandedView.visibility == View.VISIBLE) {
                holder.expandedView.visibility = View.GONE
            } else {
                holder.expandedView.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return consultas.size
    }
}