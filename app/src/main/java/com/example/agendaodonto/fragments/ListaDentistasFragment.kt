package com.example.agendaodonto.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.R
import com.example.agendaodonto.adapters.DentistaAdapter
import com.example.agendaodonto.models.Dentista
import com.example.agendaodonto.ui.components.CustomDividerItemDecoration
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ListaDentistasFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DentistaAdapter
    private val dentistasList = mutableListOf<Dentista>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_agendar_lista_dentista, container, false)

        recyclerView = view.findViewById(R.id.rv_dentistas_list)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = DentistaAdapter(dentistasList)
        recyclerView.adapter = adapter

        val divider = ContextCompat.getDrawable(requireContext(), R.drawable.divider)!!
        val itemDecoration = CustomDividerItemDecoration(requireContext(), divider)
        recyclerView.addItemDecoration(itemDecoration)

        loadDentistasList()

        return view
    }

    private fun loadDentistasList() {
        val db = FirebaseFirestore.getInstance()
        val dentistasRef = db.collection("dentistas")

        dentistasRef.get()
            .addOnSuccessListener { documents ->
                dentistasList.clear()

                for (document in documents) {
                    val id = document.id
                    val name = document.getString("name") ?: ""
                    val especialidade = document.getString("especialidade") ?: ""
                    val avatarUrl = document.getString("avatarUrl") ?: ""

                    val ratingsRefs =
                        document.get("ratings") as? List<DocumentReference> ?: emptyList()
                    calculateAverageRating(ratingsRefs) { averageRating ->
                        val dentista = Dentista(id, name, especialidade, averageRating, avatarUrl)
                        dentistasList.add(dentista)
                        adapter.notifyItemInserted(dentistasList.size - 1)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("ListaDentistasFragment", "Erro ao buscar dados: ", e)
            }
    }

    private fun calculateAverageRating(
        ratingsRefs: List<DocumentReference>,
        callback: (Float) -> Unit
    ) {
        if (ratingsRefs.isEmpty()) {
            callback(0f)
            return
        }

        var totalRating = 0f
        var ratingsCount = 0

        for (ratingRef in ratingsRefs) {
            ratingRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val ratingValue = document.getDouble("rating") ?: 0.0
                        totalRating += ratingValue.toFloat()
                    }
                    ratingsCount++

                    if (ratingsCount == ratingsRefs.size) {
                        val averageRating = totalRating / ratingsCount
                        callback(averageRating)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("ListaDentistasFragment", "Erro ao calcular média dos ratings: ", e)
                    callback(0f)
                }
        }
    }
}

