package com.av.pesquisa

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editLogin = findViewById<EditText>(R.id.editLogin)
        val editSenha = findViewById<EditText>(R.id.editSenha)
        val botaoEntrar = findViewById<Button>(R.id.botaoEntrar)

        botaoEntrar.setOnClickListener {
            val login = editLogin.text.toString().trim()
            val senha = editSenha.text.toString().trim()

            if (login.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha login e senha", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val banco = BancoHelper(this)
            val papel = banco.validarLogin(login, senha)

            if (papel == null) {
                Toast.makeText(this, "Login ou senha inválidos", Toast.LENGTH_SHORT).show()
            } else if (papel == "admin") {
                startActivity(Intent(this, AdminActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, EspontaneaActivity::class.java))
                finish()
            }
        }
    }
}
