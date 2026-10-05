package com.av.pesquisa

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BancoHelper(context: Context) : SQLiteOpenHelper(context, "pesquisa.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE usuarios (" +
                "login TEXT PRIMARY KEY, " +
                "senha TEXT NOT NULL, " +
                "papel TEXT NOT NULL)"
        )
        db.execSQL(
            "CREATE TABLE pesquisas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nome TEXT NOT NULL, " +
                "celular TEXT NOT NULL, " +
                "espontanea TEXT NOT NULL, " +
                "estimulada TEXT NOT NULL, " +
                "problemas TEXT NOT NULL, " +
                "latitude REAL, " +
                "longitude REAL, " +
                "data TEXT)"
        )
        cadastrarUsuario("admin", "admin", "admin", db)
        cadastrarUsuario("entrevistador", "entrevistador", "entrevistador", db)
    }

    override fun onUpgrade(db: SQLiteDatabase, versaoAntiga: Int, versaoNova: Int) {
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        db.execSQL("DROP TABLE IF EXISTS pesquisas")
        onCreate(db)
    }

    private fun cadastrarUsuario(login: String, senha: String, papel: String, db: SQLiteDatabase) {
        val valores = ContentValues()
        valores.put("login", login)
        valores.put("senha", senha)
        valores.put("papel", papel)
        db.insert("usuarios", null, valores)
    }

    fun validarLogin(login: String, senha: String): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT papel FROM usuarios WHERE login = ? AND senha = ?",
            arrayOf(login, senha)
        )
        var papel: String? = null
        if (cursor.moveToFirst()) {
            papel = cursor.getString(0)
        }
        cursor.close()
        return papel
    }

    fun inserirPesquisa(
        nome: String,
        celular: String,
        espontanea: String,
        estimulada: String,
        problemas: String,
        latitude: Double,
        longitude: Double
    ): Boolean {
        val db = writableDatabase
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val valores = ContentValues()
        valores.put("nome", nome)
        valores.put("celular", celular)
        valores.put("espontanea", espontanea)
        valores.put("estimulada", estimulada)
        valores.put("problemas", problemas)
        valores.put("latitude", latitude)
        valores.put("longitude", longitude)
        valores.put("data", formato.format(Date()))
        return db.insert("pesquisas", null, valores) != -1L
    }

    fun contarPesquisas(): Int {
        val cursor = readableDatabase.rawQuery("SELECT COUNT(*) FROM pesquisas", null)
        var total = 0
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0)
        }
        cursor.close()
        return total
    }

    fun contarVotos(candidato: String): Int {
        val cursor = readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM pesquisas WHERE estimulada = ?",
            arrayOf(candidato)
        )
        var total = 0
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0)
        }
        cursor.close()
        return total
    }

    fun contarProblema(problema: String): Int {
        val cursor = readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM pesquisas WHERE problemas LIKE ?",
            arrayOf("%$problema%")
        )
        var total = 0
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0)
        }
        cursor.close()
        return total
    }

    fun listarEntrevistados(): List<String> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT nome, celular, data, latitude, longitude FROM pesquisas ORDER BY id DESC",
            null
        )
        val lista = ArrayList<String>()
        while (cursor.moveToNext()) {
            val nome = cursor.getString(0)
            val celular = cursor.getString(1)
            val data = cursor.getString(2)
            val latitude = cursor.getDouble(3)
            val longitude = cursor.getDouble(4)
            lista.add("Nome: $nome\nCelular: $celular\nData: $data\nLocal: $latitude, $longitude")
        }
        cursor.close()
        return lista
    }

    fun limparDados(): Int {
        val db = writableDatabase
        return db.delete("pesquisas", null, null)
    }
}
