package com.example.agendaodonto.adapters

import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.models.Consulta
import com.example.agendaodonto.R
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

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

        holder.tvData.text = formatDate(consulta.data)

        holder.tvObservacoes.text = consulta.dentistaFormData.observacoes

        holder.llArquivos.removeAllViews()
        for (arquivo in consulta.dentistaFormData.arquivos) {
            val fileName = arquivo.substringAfterLast("_").substringBefore("?")
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

        consulta.dentistaID.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val dentistaName =
                    documentSnapshot.getString("name")
                holder.tvMedico.text = dentistaName ?: "Nome não encontrado"
            } else {
                holder.tvMedico.text = "Dentista não encontrado"
            }
        }

        holder.expandedView.visibility = View.GONE
        holder.btnIconArrowDown.visibility = View.VISIBLE
        holder.btnIconArrowUp.visibility = View.GONE

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

    private fun formatDate(timestamp: Timestamp): String {
        val date = timestamp.toDate()
        val format = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        )
        return format.format(date)
    }


    override fun getItemCount(): Int {
        return consultas.size
    }
}