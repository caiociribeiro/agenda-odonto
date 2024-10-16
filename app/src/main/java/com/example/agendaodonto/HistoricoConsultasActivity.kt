package com.example.agendaodonto

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.adapter.ConsultaAdapter
import java.util.Calendar

class HistoricoConsultasActivity : CommonInterfaceActivity() {

    private lateinit var selectedYear: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(
            R.layout.activity_historico_consultas,
            findViewById(R.id.content_frame)
        )

        val pageName = findViewById<TextView>(R.id.tv_page_name)
        pageName.text = getString(R.string.historico)

        val recyclerView: RecyclerView = findViewById(R.id.rv_consultas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val consultas = loadConsultas()

        val adapter = ConsultaAdapter(consultas)
        recyclerView.adapter = adapter

        selectedYear = findViewById(R.id.btn_selected_year)

        selectedYear.setOnClickListener {
            showYearPickerDialog()
        }

    }

    private fun showYearPickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_year_picker, null)

        val yearPicker: NumberPicker = dialogView.findViewById(R.id.picker_year)

        yearPicker.textSize = 80f

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

    private fun loadConsultas(): List<Consulta> {
        return listOf(
            Consulta("10/01/2023", "Dr. José", listOf("Arquivo1.pdf", "Arquivo2.jpg")),
            Consulta("20/05/2023", "Dra. Ana", listOf("Arquivo3.pdf")),
        )
    }
}
