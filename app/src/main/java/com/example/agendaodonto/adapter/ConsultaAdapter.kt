package com.example.agendaodonto.adapter

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.models.Consulta
import com.example.agendaodonto.R

class ConsultaAdapter(private val consultas: List<Consulta>) :
    RecyclerView.Adapter<ConsultaAdapter.ConsultaViewHolder>() {

    class ConsultaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvData: TextView = view.findViewById(R.id.tv_data_consulta)
        val tvMedico: TextView = view.findViewById(R.id.tv_nome_medico)
        val expandedView: LinearLayout = view.findViewById(R.id.expanded_view)
        val collapsedView: LinearLayout = view.findViewById(R.id.collapsed_view)
        val tvArquivos: TextView = view.findViewById(R.id.tv_arquivo)
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
        holder.tvArquivos.text = consulta.arquivos.joinToString("\n")

        // Expand/Collapse logic
        holder.collapsedView.setOnClickListener {
            handleExpandedCollapsedViewVisibility(holder)
        }

        holder.btnIconArrowDown.setOnClickListener {
            handleExpandedCollapsedViewVisibility(holder)
        }

        holder.btnIconArrowUp.setOnClickListener {
            handleExpandedCollapsedViewVisibility(holder)
        }
    }

    private fun handleExpandedCollapsedViewVisibility(holder: ConsultaViewHolder) {
        if (holder.expandedView.visibility == View.VISIBLE) {
            collapseView(holder)
        } else {
            expandView(holder)
        }
    }

    private fun expandView(holder: ConsultaViewHolder) {
        holder.expandedView.measure(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val targetHeight = holder.expandedView.measuredHeight

        holder.expandedView.visibility = View.VISIBLE
        holder.expandedView.layoutParams.height = 0 // Start with height 0
        holder.expandedView.requestLayout()

        val animator = ValueAnimator.ofInt(0, targetHeight)
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            holder.expandedView.layoutParams.height = animatedValue
            holder.expandedView.requestLayout()
        }
        animator.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                holder.btnIconArrowDown.visibility = View.GONE
                holder.btnIconArrowUp.visibility = View.VISIBLE
            }
        })
        animator.duration = 150
        animator.start()
    }

    private fun collapseView(holder: ConsultaViewHolder) {
        val initialHeight = holder.expandedView.height
        val animator = ValueAnimator.ofInt(initialHeight, 0)
        animator.addUpdateListener { animation ->
            val animatedValue = animation.animatedValue as Int
            holder.expandedView.layoutParams.height = animatedValue
            holder.expandedView.requestLayout()
        }
        animator.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                holder.expandedView.visibility = View.GONE
                holder.btnIconArrowUp.visibility = View.GONE
                holder.btnIconArrowDown.visibility = View.VISIBLE
            }
        })
        animator.duration = 150
        animator.start()
    }

    override fun getItemCount(): Int {
        return consultas.size
    }
}