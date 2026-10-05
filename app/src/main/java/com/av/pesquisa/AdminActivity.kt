package com.av.pesquisa

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class AdminActivity : Activity() {

    private val candidatos = listOf(
        "Lula Molusco" to R.drawable.cand_lula_molusco,
        "Niko (OneShot)" to R.drawable.cand_niko,
        "V1 (ULTRAKILL)" to R.drawable.cand_v1,
        "Anakin Skywalker" to R.drawable.cand_anakin,
        "LulaDroid" to R.drawable.cand_luladroid
    )

    private val opcoesVoto = listOf(
        "Voto branco" to R.drawable.cand_branco,
        "Voto nulo" to R.drawable.cand_nulo
    )

    private val problemas = listOf(
        "Saúde",
        "Educação",
        "Segurança",
        "Desemprego",
        "Moradia",
        "Saneamento",
        "Transporte",
        "Meio ambiente",
        "Drogas",
        "Corrupção"
    )

    private val linhasBarras = ArrayList<Pair<String, LinearLayout>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val banco = BancoHelper(this)
        val total = banco.contarPesquisas()

        val textoResumo = findViewById<TextView>(R.id.textoResumo)
        textoResumo.text = "Total de entrevistas: $total"

        if (total > 0) {
            val lider = candidatos
                .map { (nome, _) -> nome to banco.contarVotos(nome) }
                .maxByOrNull { it.second }
            if (lider != null && lider.second > 0) {
                val percentualLider = lider.second * 100 / total
                textoResumo.text = "Total de entrevistas: $total\n" +
                    "Líder: ${lider.first} com $percentualLider%"
            }
        }

        val editBusca = findViewById<EditText>(R.id.editBusca)
        editBusca.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(texto: Editable) {
                filtrarBarras(texto.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
        })

        val caixaCandidatos = findViewById<LinearLayout>(R.id.caixaCandidatos)
        for ((opcao, foto) in candidatos + opcoesVoto) {
            val votos = banco.contarVotos(opcao)
            val porcentagem = if (total > 0) votos * 100 / total else 0
            val linha = criarBarra(opcao, votos, porcentagem, foto)
            caixaCandidatos.addView(linha)
            linhasBarras.add(opcao to linha)
        }

        val caixaProblemas = findViewById<LinearLayout>(R.id.caixaProblemas)
        for (problema in problemas) {
            val citacoes = banco.contarProblema(problema)
            val porcentagem = if (total > 0) citacoes * 100 / total else 0
            val linha = criarBarra(problema, citacoes, porcentagem)
            caixaProblemas.addView(linha)
            linhasBarras.add(problema to linha)
        }

        configurarTabs()

        val listaEntrevistados = findViewById<ListView>(R.id.listaEntrevistados)
        val entrevistados = banco.listarEntrevistados()
        val linhasLista = if (entrevistados.isEmpty()) {
            listOf("Nenhuma entrevista registrada")
        } else {
            entrevistados
        }
        listaEntrevistados.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            linhasLista
        )

        val botaoLimpar = findViewById<Button>(R.id.botaoLimpar)
        botaoLimpar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Limpar dados")
                .setMessage("Apagar todas as pesquisas registradas?")
                .setPositiveButton("Apagar") { _, _ ->
                    banco.limparDados()
                    Toast.makeText(this, "Dados apagados", Toast.LENGTH_SHORT).show()
                    recreate()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        val botaoSair = findViewById<Button>(R.id.botaoSair)
        botaoSair.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finishAffinity()
        }
    }

    private fun configurarTabs() {
        val grupoTabs = findViewById<RadioGroup>(R.id.grupoTabs)
        val telaEstimulada = findViewById<View>(R.id.telaEstimulada)
        val telaProblemas = findViewById<View>(R.id.telaProblemas)
        val telaEntrevistados = findViewById<View>(R.id.telaEntrevistados)

        grupoTabs.setOnCheckedChangeListener { _, abaSelecionada ->
            telaEstimulada.visibility =
                if (abaSelecionada == R.id.tabEstimulada) View.VISIBLE else View.GONE
            telaProblemas.visibility =
                if (abaSelecionada == R.id.tabProblemas) View.VISIBLE else View.GONE
            telaEntrevistados.visibility =
                if (abaSelecionada == R.id.tabEntrevistados) View.VISIBLE else View.GONE
        }
    }

    private fun filtrarBarras(busca: String) {
        for ((rotulo, linha) in linhasBarras) {
            if (rotulo.lowercase().contains(busca.trim().lowercase())) {
                linha.visibility = View.VISIBLE
            } else {
                linha.visibility = View.GONE
            }
        }
    }

    private fun criarTexto(conteudo: String): TextView {
        val densidade = resources.displayMetrics.density
        val texto = TextView(this)
        texto.text = conteudo
        texto.textSize = 14f
        texto.setLineSpacing(4f * densidade, 1f)
        texto.setPadding(
            (12 * densidade).toInt(),
            (8 * densidade).toInt(),
            (12 * densidade).toInt(),
            (8 * densidade).toInt()
        )
        texto.setBackgroundColor(0xFFF0F0F0.toInt())
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, (8 * densidade).toInt())
        texto.layoutParams = params
        return texto
    }

    private fun criarBarra(rotulo: String, valor: Int, porcentagem: Int, foto: Int? = null): LinearLayout {
        val densidade = resources.displayMetrics.density
        val alturaBarra = (20 * densidade).toInt()

        val linha = LinearLayout(this)
        linha.orientation = LinearLayout.HORIZONTAL
        linha.gravity = Gravity.CENTER_VERTICAL
        linha.setPadding(0, (6 * densidade).toInt(), 0, (6 * densidade).toInt())

        if (foto != null) {
            val miniatura = ImageView(this)
            miniatura.setImageResource(foto)
            miniatura.layoutParams = LinearLayout.LayoutParams(
                (32 * densidade).toInt(),
                (32 * densidade).toInt()
            ).apply { setMargins(0, 0, (6 * densidade).toInt(), 0) }
            linha.addView(miniatura)
        }

        val textoRotulo = TextView(this)
        textoRotulo.text = "$rotulo ($valor)"
        textoRotulo.layoutParams = LinearLayout.LayoutParams(
            (130 * densidade).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val barra = View(this)
        barra.setBackgroundColor(getColor(android.R.color.holo_blue_light))
        barra.layoutParams = LinearLayout.LayoutParams(0, alturaBarra, porcentagem.toFloat())

        val resto = View(this)
        resto.setBackgroundColor(getColor(android.R.color.darker_gray))
        resto.layoutParams = LinearLayout.LayoutParams(0, alturaBarra, (100 - porcentagem).toFloat())

        val textoValor = TextView(this)
        textoValor.text = "$porcentagem%"
        textoValor.setPadding((6 * densidade).toInt(), 0, 0, 0)
        textoValor.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        linha.addView(textoRotulo)
        linha.addView(barra)
        linha.addView(resto)
        linha.addView(textoValor)
        return linha
    }
}
