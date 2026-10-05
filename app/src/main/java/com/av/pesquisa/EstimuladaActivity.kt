package com.av.pesquisa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast

class EstimuladaActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estimulada)

        val grupoCandidatos = findViewById<RadioGroup>(R.id.grupoCandidatos)
        val botaoAvancar = findViewById<Button>(R.id.botaoAvancar)

        val espontanea = intent.getStringExtra("espontanea") ?: ""
        val tipo = intent.getStringExtra("tipo") ?: "ordem"

        botaoAvancar.setOnClickListener {
            val idSelecionado = grupoCandidatos.checkedRadioButtonId

            if (idSelecionado == -1) {
                Toast.makeText(this, "Escolha um candidato", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val candidato = findViewById<RadioButton>(idSelecionado).text.toString()

            if (tipo == "estimulada") {
                val finalizar = Intent(this, FinalizarActivity::class.java)
                finalizar.putExtra("espontanea", espontanea)
                finalizar.putExtra("estimulada", candidato)
                finalizar.putExtra("problemas", "")
                startActivity(finalizar)
                finish()
            } else {
                val problemas = Intent(this, ProblemasActivity::class.java)
                problemas.putExtra("espontanea", espontanea)
                problemas.putExtra("estimulada", candidato)
                problemas.putExtra("tipo", tipo)
                startActivity(problemas)
                finish()
            }
        }
    }
}
