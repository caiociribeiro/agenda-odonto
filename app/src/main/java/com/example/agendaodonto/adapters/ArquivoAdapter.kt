package com.example.agendaodonto.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.R
import com.example.agendaodonto.models.Arquivo

class ArquivoAdapter(
    private val arquivos: MutableList<Arquivo>,
    private val onRemoveClick: (Arquivo) -> Unit
) : RecyclerView.Adapter<ArquivoAdapter.ArquivoViewHolder>() {

    inner class ArquivoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFileName: TextView = itemView.findViewById(R.id.tv_file_name)
        val btnRemove: Button = itemView.findViewById(R.id.btn_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArquivoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.lista_arquivos, parent, false)
        return ArquivoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArquivoViewHolder, position: Int) {
        val arquivo = arquivos[position]
        holder.tvFileName.text = arquivo.nome
        holder.btnRemove.setOnClickListener { onRemoveClick(arquivo) }
    }

    override fun getItemCount(): Int = arquivos.size
}