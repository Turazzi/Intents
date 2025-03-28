package com.example.ana.intents

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ana.intents.Extras.PARAMETER_EXTRA


class ParameterActivity : AppCompatActivity() {
    private val apb: ActivityParameterBinding by lazy {
        ActivityParameterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(apb.root)
        setSupportActionBar(apb.toolbarIn.toolbar)
        supportActionBar?.subtitle = localClassName // Exibe o nome da classe na barra de título

        // Recebe o valor enviado da MainActivity via Intent
        intent.getStringExtra(PARAMETER_EXTRA)?.let {
            apb.parameterEt.setText(it)
        }

        // Botão para retornar o valor atualizado para a MainActivity
        apb.returnAndCloseBt.setOnClickListener {
            Intent().apply {
                putExtra(PARAMETER_EXTRA, apb.parameterEt.text.toString())
                setResult(RESULT_OK, this) // Define o resultado da Activity antes de fechá-la
            }
            finish() // Encerra a Activity e retorna para a anterior
        }
    }
}