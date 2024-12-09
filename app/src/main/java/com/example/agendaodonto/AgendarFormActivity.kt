package com.example.agendaodonto

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale


class AgendarFormActivity : CommonInterfaceActivity() {
    private lateinit var tvSubtitle: TextView
    private lateinit var tvInfo: TextView

    private lateinit var rgAparelho: RadioGroup
    private lateinit var rgFumante: RadioGroup
    private lateinit var rgEscovacao: RadioGroup
    private lateinit var etAlergias: EditText
    private lateinit var btnConfirmar: Button
    private lateinit var loading: CircularProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_agendar_form, findViewById(R.id.content_frame))

        val pageName = findViewById<TextView>(R.id.tv_page_name)
        pageName.text = "Agendamento"

        val dentistaName = intent.getStringExtra("dentistaName")
        val dentistaID = intent.getStringExtra("dentistaID")
        val dia = intent.getStringExtra("dia") ?: ""
        val horario = intent.getStringExtra("horario") ?: ""

        val partes = dia.split("-")
        val diaFormatado = if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else dia

        tvSubtitle = findViewById(R.id.tv_subtitle)
        tvSubtitle.text = "Formulário de consulta com $dentistaName"

        tvInfo = findViewById(R.id.tv_info)
        tvInfo.text = "$diaFormatado às $horario"

        rgAparelho = findViewById(R.id.rg_aparelho)
        rgFumante = findViewById(R.id.rg_fumante)
        rgEscovacao = findViewById(R.id.rg_escova)
        etAlergias = findViewById(R.id.et_alergias)

        loading = findViewById(R.id.loading_bar)

        btnConfirmar = findViewById(R.id.btn_confirmar)
        btnConfirmar.setOnClickListener {

            btnConfirmar.visibility = View.GONE
            loading.visibility = View.VISIBLE

            val dateTimeString = "$dia $horario"
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val consultaData = dateFormat.parse(dateTimeString)?.let { Timestamp(it) }

            val usaAparelho = rgAparelho.checkedRadioButtonId == R.id.aparelho_sim
            val fuma = rgFumante.checkedRadioButtonId == R.id.fumante_sim
            val escovacao = when (rgEscovacao.checkedRadioButtonId) {
                R.id.escova_opcao_1 -> "Menos de 2 vezes ao dia"
                R.id.escova_opcao_2 -> "De 2 a 3 vezes ao dia"
                R.id.escova_opcao_3 -> "Mais de 3 vezes ao dia"
                else -> "Não informado"
            }
            val alergias = etAlergias.text.toString().trim()

            val currentUser = FirebaseAuth.getInstance().currentUser
            val userID = currentUser?.uid

            val consulta = hashMapOf(
                "data" to consultaData,
                "dentistaFormData" to hashMapOf(
                    "arquivos" to emptyList<String>(),
                    "observacoes" to ""
                ),
                "dentistaID" to FirebaseFirestore.getInstance().collection("dentistas")
                    .document(dentistaID ?: ""),
                "status" to hashMapOf(
                    "avaliada" to false,
                    "formPendente" to true
                ),
                "userFeedback" to null,
                "userFormData" to hashMapOf(
                    "alergias" to alergias,
                    "escovacao" to escovacao,
                    "fuma" to fuma,
                    "motivoConsulta" to "",
                    "usaAparelho" to usaAparelho
                ),
                "userID" to FirebaseFirestore.getInstance().collection("users")
                    .document(userID ?: "")
            )

            val db = FirebaseFirestore.getInstance()

            db.collection("consultas")
                .add(consulta)
                .addOnSuccessListener { _ ->
                    val disponibilidadeRef = db.collection("dentistas")
                        .document(dentistaID ?: "")
                        .collection("disponibilidade")
                        .document(dia)

                    val updates = mapOf(
                        "horarios.$horario" to false
                    )

                    disponibilidadeRef.update(updates)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Consulta agendada com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "Erro ao atualizar disponibilidade: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Erro ao agendar consulta: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Erro ao agendar consulta: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}
