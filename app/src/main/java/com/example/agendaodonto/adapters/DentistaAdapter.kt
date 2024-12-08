package com.example.agendaodonto.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.MenuRes
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.models.Dentista
import com.example.agendaodonto.R
import com.bumptech.glide.Glide
import com.example.agendaodonto.AgendarCalendarioActivity
import com.example.agendaodonto.DoctorProfileActivity
import me.zhanghai.android.materialratingbar.MaterialRatingBar


class DentistaAdapter(private val dentistaList: List<Dentista>) :
    RecyclerView.Adapter<DentistaAdapter.DentistaViewHolder>() {

    class DentistaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_doctor_name)
        val especialidade: TextView = view.findViewById(R.id.tv_specialty)
        val ratings: TextView = view.findViewById(R.id.tv_doctor_rating)
        val avatar: ImageView = view.findViewById(R.id.iv_doctor_avatar)
        val ratingBar: MaterialRatingBar = view.findViewById(R.id.rb_rating)
        val btnCardMenu: Button = view.findViewById(R.id.btn_card_menu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DentistaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_dentista, parent, false)
        return DentistaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DentistaViewHolder, position: Int) {
        val dentista = dentistaList[position]
        holder.name.text = dentista.name
        holder.especialidade.text = dentista.especialidade
        holder.ratings.text = dentista.rating.toString()
        holder.ratingBar.rating = dentista.rating

        val imageUrl = if (dentista.avatar != "") dentista.avatar else R.drawable.user

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.avatar)

        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, AgendarCalendarioActivity::class.java)
            intent.putExtra("dentistaName", dentista.name)
            intent.putExtra("dentistaID", dentista.id)
            context.startActivity(intent)
        }

        holder.btnCardMenu.setOnClickListener { v ->
            showCardMenu(v, R.menu.card_dentista_menu, dentista)
        }
    }

    override fun getItemCount(): Int {
        return dentistaList.size
    }

    private fun showCardMenu(view: View, @MenuRes menuRes: Int, dentista: Dentista) {
        val popup = PopupMenu(view.context, view)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.option_1 -> {
                    val context = view.context
                    val intent = Intent(context, DoctorProfileActivity::class.java)

                    intent.putExtra("name", dentista.name)
                    intent.putExtra("especialidade", dentista.especialidade)
                    intent.putExtra("rating", dentista.rating)

                    context.startActivity(intent)
                    true
                }

                else -> false
            }
        }
        popup.show()
    }
}