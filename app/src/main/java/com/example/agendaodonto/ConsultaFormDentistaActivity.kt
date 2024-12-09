package com.example.agendaodonto

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agendaodonto.adapters.ArquivoAdapter
import com.example.agendaodonto.models.Arquivo
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ConsultaFormDentistaActivity : AppCompatActivity() {
    private lateinit var rvArquivos: RecyclerView
    private lateinit var adapter: ArquivoAdapter
    private val arquivos: MutableList<Arquivo> = mutableListOf()
    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>
    private lateinit var loading: CircularProgressIndicator

    private lateinit var etObs: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consulta_form_dentista)

        val consultaId = intent.getStringExtra("consultaId") ?: ""
        val pacienteName = intent.getStringExtra("pacienteName") ?: ""
        val date = intent.getStringExtra("consultaData") ?: ""

        val tvSubtitle: TextView = findViewById(R.id.tv_subtitle)
        val tvDate: TextView = findViewById(R.id.tv_consulta_date)

        tvSubtitle.text = "Dados de consulta de $pacienteName"
        tvDate.text = date

        rvArquivos = findViewById(R.id.rv_arquivos_lista)
        adapter = ArquivoAdapter(arquivos) { arquivo -> removerArquivo(arquivo) }
        rvArquivos.layoutManager = LinearLayoutManager(this)
        rvArquivos.adapter = adapter

        filePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri = result.data?.data
                    if (uri != null) {
                        val nome = getNomeArquivo(uri)
                        arquivos.add(Arquivo(nome, uri.toString()))
                        adapter.notifyItemInserted(arquivos.size - 1)
                    }
                }
            }

        etObs = findViewById(R.id.et_consulta_obs)

        val btnUpload: Button = findViewById(R.id.btn_arquivos_upload)
        btnUpload.setOnClickListener {
            selecionarArquivo()
        }

        loading = findViewById(R.id.loading_bar)

        val btnEnviar: Button = findViewById(R.id.btn_enviar)
        btnEnviar.setOnClickListener {
            val observacoes = etObs.text.toString().trim()

            Log.d("ObservacoesDebug", "Texto capturado: $observacoes")

            btnEnviar.visibility = View.GONE
            loading.visibility = View.VISIBLE

            enviarArquivos(consultaId, observacoes)

        }
    }

    private fun selecionarArquivo() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "application/pdf"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        filePickerLauncher.launch(Intent.createChooser(intent, "Selecione um arquivo"))
    }

    private fun getNomeArquivo(uri: Uri): String {
        var nome = "arquivo.pdf"
        val cursor = contentResolver.query(uri, null, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                val nomeIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nomeIndex != -1) {
                    nome = it.getString(nomeIndex) ?: nome
                }
            }
        }

        return nome
    }

    private fun removerArquivo(arquivo: Arquivo) {
        val position = arquivos.indexOf(arquivo)
        if (position != -1) {
            arquivos.removeAt(position)
            adapter.notifyItemRemoved(position)
        }
    }

    private fun enviarArquivos(consultaId: String, observacoes: String) {
        Log.d("ObservacoesDebug", "Texto capturado: $observacoes")
        val storage = FirebaseStorage.getInstance()
        val db = FirebaseFirestore.getInstance()
        val consultaRef = db.collection("consultas").document(consultaId)

        if (arquivos.isEmpty()) {
            atualizarConsulta(consultaRef, emptyList(), observacoes)
            return
        }

        val fileUrls = mutableListOf<String>()
        var arquivosProcessados = 0

        for (arquivo in arquivos) {
            val uri = Uri.parse(arquivo.uri)
            val nomeUnico = "${UUID.randomUUID()}_${arquivo.nome}"
            val storageRef = storage.reference.child("consultas/$consultaId/$nomeUnico")

            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        fileUrls.add(downloadUrl.toString())
                        arquivosProcessados++

                        if (arquivosProcessados == arquivos.size) {
                            atualizarConsulta(consultaRef, fileUrls, observacoes)
                        }
                    }
                }
        }
    }

    private fun atualizarConsulta(
        consultaRef: DocumentReference,
        fileUrls: List<String>,
        observacoes: String
    ) {

        Log.d("ObservacoesDebug", "Texto capturado: $observacoes")

        val dentistaFormData = mapOf(
            "arquivos" to fileUrls,
            "observacoes" to observacoes
        )

        consultaRef.update(
            "dentistaFormData", dentistaFormData,
            "status.formPendente", false
        ).addOnSuccessListener {
            val intent = Intent(this, HomeDentistaActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}