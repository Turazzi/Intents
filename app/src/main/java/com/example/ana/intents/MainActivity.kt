package com.example.ana.intents


import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.Intent.*
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.ana.intents.Extras.PARAMETER_EXTRA
import com.example.ana.intents.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    // Activity Result Launchers para lidar com diferentes intents de retorno
    private lateinit var parameterArl: ActivityResultLauncher<Intent>
    private lateinit var cppArl: ActivityResultLauncher<String>
    private lateinit var pickImageArl: ActivityResultLauncher<Intent>

    // Binding para acessar os elementos da interface de forma mais eficiente
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        setSupportActionBar(amb.toolbarIn.toolbar)
        supportActionBar?.subtitle = localClassName // Exibe o nome da classe na barra de título

        // Configura o botão para abrir outra Activity e passar um parâmetro
        amb.parameterBt.setOnClickListener {
            Intent("OPEN_PARAMETER_ACTIVITY_ACTION").let { intent ->
                intent.putExtra(PARAMETER_EXTRA, amb.parameterTv.text.toString())
                parameterArl.launch(intent)
            }
        }

        // Recebe o resultado da ParameterActivity
        parameterArl =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    result.data?.getStringExtra(PARAMETER_EXTRA).let {
                        amb.parameterTv.text = it
                    }
                }
            }

        // Solicitação de permissão para chamadas telefônicas
        cppArl =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
                if (permissionGranted) {
                    callPhone(true) // Chama o número se a permissão foi concedida
                } else {
                    Toast.makeText(this, "Permissão necessária para realizar chamadas!", Toast.LENGTH_SHORT).show()
                }
            }

        // Obtém uma imagem da galeria e exibe em outro app
        pickImageArl = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                startActivity(Intent(ACTION_VIEW, result.data?.data))
            }
        }
    }

    // Cria o menu de opções
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    // Trata os cliques nos itens do menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.open_activity_mi -> {
                Toast.makeText(this, "Você clicou em abrir Activity", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.view_mi -> {
                startActivity(browserIntent()) // Abre um navegador
                true
            }

            R.id.call_mi -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(CALL_PHONE) == PERMISSION_GRANTED) {
                        callPhone(true)
                    } else {
                        cppArl.launch(CALL_PHONE) // Solicita permissão se necessário
                    }
                } else {
                    callPhone(true) // Chama direto em versões antigas
                }
                true
            }

            R.id.dial_mi -> {
                callPhone(false) // Apenas abre o discador
                true
            }

            R.id.pick_mi -> {
                // Permite selecionar uma imagem da galeria
                val imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
                val pickImageIntent = Intent(ACTION_PICK).apply {
                    setDataAndType(Uri.parse(imageDir), "image/*")
                }
                pickImageArl.launch(pickImageIntent)
                true
            }

            R.id.chooser_mi -> {
                // Exibe um seletor para escolher o navegador
                val chooserIntent = Intent(ACTION_CHOOSER).apply {
                    putExtra(EXTRA_TITLE, "Escolha seu navegador preferido")
                    putExtra(EXTRA_INTENT, browserIntent())
                }
                startActivity(chooserIntent)
                true
            }

            else -> false
        }
    }

    /**
     * Método para realizar chamadas telefônicas ou apenas abrir o discador.
     */
    private fun callPhone(call: Boolean) {
        val number = "tel:${amb.parameterTv.text}"
        val callIntent = Intent(if (call) ACTION_CALL else ACTION_DIAL).apply {
            data = Uri.parse(number)
        }
        startActivity(callIntent)
    }

    /**
     * Método para abrir uma URL no navegador.
     */
    private fun browserIntent(): Intent {
        val url = Uri.parse(amb.parameterTv.text.toString())
        return Intent(ACTION_VIEW, url)
    }
}