package com.example.agendaodonto.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.R
import com.example.agendaodonto.models.Consulta

class ConsultaAdapter(private val consultas: List<Consulta>) :
    RecyclerView.Adapter<ConsultaAdapter.ConsultaViewHolder>() {

    class ConsultaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvData: TextView = view.findViewById(R.id.tv_data_consulta)
        val tvMedico: TextView = view.findViewById(R.id.tv_nome_medico)
        val tvObservacoes: TextView = view.findViewById(R.id.tv_observacoes)
        val llArquivos: LinearLayout = view.findViewById(R.id.ll_arquivos)
        val expandedView: LinearLayout = view.findViewById(R.id.expanded_view)
        val btnIconArrowDown: Button = view.findViewById(R.id.btn_icon_arrow_down)
        val btnIconArrowUp: Button = view.findViewById(R.id.btn_icon_arrow_up)
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
        holder.tvObservacoes.text = consulta.observacoes + "\n" ?: "Sem observações"

        // Preencher a lista de arquivos com ícones de download
        holder.llArquivos.removeAllViews()
        for (arquivo in consulta.arquivos) {
            val fileName = arquivo.substringAfterLast("_").substringBefore("?")

            // Layout para cada item de arquivo
            val fileLayout = LayoutInflater.from(holder.itemView.context)
                .inflate(R.layout.item_arquivo, holder.llArquivos, false)

            val tvFileName = fileLayout.findViewById<TextView>(R.id.tv_file_name)
            val btnDownload = fileLayout.findViewById<ImageView>(R.id.btn_download)

            tvFileName.text = fileName

            btnDownload.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(arquivo))
                holder.itemView.context.startActivity(intent)
            }

            holder.llArquivos.addView(fileLayout)
        }

        // Configurar visibilidade inicial
        holder.expandedView.visibility = View.GONE
        holder.btnIconArrowDown.visibility = View.VISIBLE
        holder.btnIconArrowUp.visibility = View.GONE

        // Configurar o clique para expandir/recolher
        holder.btnIconArrowDown.setOnClickListener {
            holder.expandedView.visibility = View.VISIBLE
            holder.btnIconArrowDown.visibility = View.GONE
            holder.btnIconArrowUp.visibility = View.VISIBLE
        }

        holder.btnIconArrowUp.setOnClickListener {
            holder.expandedView.visibility = View.GONE
            holder.btnIconArrowDown.visibility = View.VISIBLE
            holder.btnIconArrowUp.visibility = View.GONE
        }
    }


    override fun getItemCount(): Int {
        return consultas.size
    }
}
