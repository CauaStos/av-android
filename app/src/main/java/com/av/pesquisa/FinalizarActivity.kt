package com.av.pesquisa

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class FinalizarActivity : Activity() {

    private lateinit var espontanea: String
    private lateinit var estimulada: String
    private lateinit var problemas: String

    private var atualizandoCelular = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finalizar)

        espontanea = intent.getStringExtra("espontanea") ?: ""
        estimulada = intent.getStringExtra("estimulada") ?: ""
        problemas = intent.getStringExtra("problemas") ?: ""

        val editCelular = findViewById<EditText>(R.id.editCelular)
        editCelular.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(texto: Editable) {
                if (atualizandoCelular) return
                atualizandoCelular = true
                val formatado = formatarCelular(texto.toString())
                if (texto.toString() != formatado) {
                    editCelular.setText(formatado)
                    editCelular.setSelection(formatado.length)
                }
                atualizandoCelular = false
            }
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
        })

        val textoResumo = findViewById<TextView>(R.id.textoResumo)
        textoResumo.text =
            "Resposta espontânea: ${if (espontanea.isEmpty()) "não respondida" else espontanea}\n" +
            "Candidato escolhido: ${if (estimulada.isEmpty()) "não respondida" else estimulada}\n" +
            "Problemas: ${if (problemas.isEmpty()) "não respondida" else problemas}"

        val botaoConcluir = findViewById<Button>(R.id.botaoConcluir)
        botaoConcluir.setOnClickListener { concluirPesquisa() }

        val botaoFinalizar = findViewById<Button>(R.id.botaoFinalizar)
        botaoFinalizar.setOnClickListener { concluirPesquisa(end_app = true) }
    }

    private fun concluirPesquisa(end_app: Boolean = false) {
        val editNome = findViewById<EditText>(R.id.editNome)
        val editCelular = findViewById<EditText>(R.id.editCelular)
        val nome = editNome.text.toString().trim()
        val celular = editCelular.text.toString().trim()

        if (nome.isEmpty()) {
            Toast.makeText(this, "Informe o nome do entrevistado", Toast.LENGTH_SHORT).show()
            return
        }
        if (celular.isEmpty()) {
            Toast.makeText(this, "Informe o celular do entrevistado", Toast.LENGTH_SHORT).show()
            return
        }

        val permissaoOk = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

        if (permissaoOk) {
            if (!end_app) salvarPesquisa(nome, celular) else finalizarPesquisa(nome, celular)
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }
    }

    override fun onRequestPermissionsResult(
        codigo: Int,
        permissoes: Array<out String>,
        resultados: IntArray
    ) {
        super.onRequestPermissionsResult(codigo, permissoes, resultados)
        if (codigo == 100) {
            val editNome = findViewById<EditText>(R.id.editNome)
            val editCelular = findViewById<EditText>(R.id.editCelular)
            salvarPesquisa(
                editNome.text.toString().trim(),
                editCelular.text.toString().trim()
            )
        }
    }

    private fun formatarCelular(entrada: String): String {
        val digitos = entrada.filter { it.isDigit() }.take(11)
        val posicaoTraco = if (digitos.length > 10) 7 else 6
        val formatado = StringBuilder()
        for (i in digitos.indices) {
            if (i == 0) formatado.append("(")
            if (i == 2) formatado.append(") ")
            if (i == posicaoTraco && digitos.length > posicaoTraco) formatado.append("-")
            formatado.append(digitos[i])
        }
        if (digitos.length == 2) formatado.append(") ")
        return formatado.toString()
    }

    private fun salvarPesquisa(nome: String, celular: String, end_task: Boolean = false) {
        val gerenciador = getSystemService(LOCATION_SERVICE) as LocationManager

        var local: Location? = null
        val permissaoOk = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

        if (permissaoOk) {
            for (provedor in gerenciador.allProviders) {
                val atual = gerenciador.getLastKnownLocation(provedor)
                if (atual != null) {
                    local = atual
                    break
                }
            }
        }

        val latitude = local?.latitude ?: 0.0
        val longitude = local?.longitude ?: 0.0

        val banco = BancoHelper(this)
        val ok = banco.inserirPesquisa(
            nome, celular, espontanea, estimulada, problemas, latitude, longitude
        )

        if (ok) {
            Toast.makeText(this, "Pesquisa finalizada com sucesso!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Erro ao salvar a pesquisa", Toast.LENGTH_SHORT).show()
        }

        val intent = Intent(this, EspontaneaActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        if(!end_task) startActivity(intent)
    }

    private fun finalizarPesquisa(nome: String, celular: String) {
        salvarPesquisa(nome, celular, end_task = true)
        finishAffinity()
    }
}
