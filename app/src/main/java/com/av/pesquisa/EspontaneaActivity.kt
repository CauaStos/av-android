package com.av.pesquisa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast

class EspontaneaActivity : Activity() {

    private var tipoPesquisa = "ordem"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_espontanea)

        val editResposta = findViewById<EditText>(R.id.editResposta)
        val botaoAvancar = findViewById<Button>(R.id.botaoAvancar)
        val botaoTipo = findViewById<Button>(R.id.botaoTipo)

        botaoTipo.setOnClickListener { escolherTipo(botaoTipo) }

        botaoAvancar.setOnClickListener {
            val resposta = editResposta.text.toString().trim()

            if (resposta.isEmpty()) {
                Toast.makeText(this, "Digite sua resposta", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (tipoPesquisa == "espontanea") {
                val intent = Intent(this, FinalizarActivity::class.java)
                intent.putExtra("espontanea", resposta)
                intent.putExtra("estimulada", "")
                intent.putExtra("problemas", "")
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, EstimuladaActivity::class.java)
                intent.putExtra("espontanea", resposta)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun escolherTipo(ancora: Button) {
        val menu = PopupMenu(this, ancora)
        menu.menu.add("Seguir a ordem (completa)")
        menu.menu.add("Somente espontânea")
        menu.menu.add("Somente estimulada")
        menu.menu.add("Somente problemas")
        menu.setOnMenuItemClickListener { item ->
            when (item.title) {
                "Seguir a ordem (completa)" -> {
                    tipoPesquisa = "ordem"
                    Toast.makeText(this, "Fluxo completo: espontânea, estimulada e problemas", Toast.LENGTH_SHORT).show()
                }
                "Somente espontânea" -> {
                    tipoPesquisa = "espontanea"
                    Toast.makeText(this, "Responda a espontânea e finalize", Toast.LENGTH_SHORT).show()
                }
                "Somente estimulada" -> {
                    val intent = Intent(this, EstimuladaActivity::class.java)
                    intent.putExtra("tipo", "estimulada")
                    startActivity(intent)
                    finish()
                }
                "Somente problemas" -> {
                    val intent = Intent(this, ProblemasActivity::class.java)
                    intent.putExtra("tipo", "problemas")
                    startActivity(intent)
                    finish()
                }
            }
            true
        }
        menu.show()
    }
}
