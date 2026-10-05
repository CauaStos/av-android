package com.av.pesquisa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Toast

class ProblemasActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problemas)

        val grupoProblemas = findViewById<LinearLayout>(R.id.grupoProblemas)
        val botaoAvancar = findViewById<Button>(R.id.botaoAvancar)

        val espontanea = intent.getStringExtra("espontanea") ?: ""
        val estimulada = intent.getStringExtra("estimulada") ?: ""
        val tipo = intent.getStringExtra("tipo") ?: "ordem"

        botaoAvancar.setOnClickListener {
            val selecionados = ArrayList<String>()
            for (i in 0 until grupoProblemas.childCount) {
                val filho = grupoProblemas.getChildAt(i)
                if (filho is CheckBox && filho.isChecked) {
                    selecionados.add(filho.text.toString())
                }
            }

            if (selecionados.size != 3) {
                Toast.makeText(this, "Marque exatamente 3 problemas (marcados: ${selecionados.size})", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val problemas = selecionados.joinToString(",")

            val intent = Intent(this, FinalizarActivity::class.java)
            intent.putExtra("espontanea", espontanea)
            intent.putExtra("estimulada", estimulada)
            intent.putExtra("problemas", problemas)
            startActivity(intent)
            finish()
        }
    }
}
