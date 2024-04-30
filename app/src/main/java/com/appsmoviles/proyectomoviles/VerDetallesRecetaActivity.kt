package com.appsmoviles.proyectomoviles

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.appsmoviles.proyectomoviles.daos.RecetaDAO
import com.appsmoviles.proyectomoviles.databinding.VerDetallesRecetaBinding
import com.appsmoviles.proyectomoviles.db.AppDatabase
import com.appsmoviles.proyectomoviles.dominio.Receta
import com.appsmoviles.proyectomoviles.utilidades.ManejadorImagenes
import com.appsmoviles.proyectomoviles.utilidades.ManejadorJson
import com.appsmoviles.proyectomoviles.utilidades.VinculadorSensorLuz
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VerDetallesRecetaActivity : VinculadorSensorLuz() {
    private lateinit var binding: VerDetallesRecetaBinding
    private lateinit var recetaDao: RecetaDAO
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VerDetallesRecetaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val database = AppDatabase.getInstance(this)
        recetaDao = database.recetaDAO()

        asignarListenerABotones()
        val recetaId = intent.getIntExtra("recetaId", -1)

        lifecycleScope.launch {
            val receta = withContext(Dispatchers.IO) {
                val database = AppDatabase.getInstance(applicationContext)
                val recetaDao = database.recetaDAO()
                recetaDao.obtenerRecetaPorId(recetaId)
            }

            if (receta != null) {
                desplegarDatosReceta(receta)
            } else {
                showErrorDialog()
            }
        }

    }

    private fun desplegarDatosReceta(receta: Receta){
        val tiempoTotal = sumarTiempos(receta.tiempoPreparacion, receta.tiempoCocinado)
        val jsonIngredientes = receta.listaIngredientes
        val listaIngredientes = manejarListaIngredientes(jsonIngredientes)

        val manejadorImagenes = ManejadorImagenes(this)
        receta.imagen?.let { uriString ->
            manejadorImagenes.cargarImagenDesdeUri(uriString, binding.imagenReceta)
        }

        binding.nombreReceta.text = receta.titulo
        binding.tiempoPreparacion.text = "Preparación: ${receta.tiempoPreparacion}"
        binding.tiempoCocinado.text = "Cocinado: ${receta.tiempoCocinado}"
        binding.tiempoTotal.text = "Total: $tiempoTotal"
        binding.listaIngredientes.text = listaIngredientes
        binding.pasosPreparacion.text = receta.preparacion
    }

    private fun asignarListenerABotones(){
        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AgregarRecetaActivity::class.java)
            startActivity(intent)
        }
        binding.btnSaved.setOnClickListener {
            val intent = Intent(this, VerGuardadosActivity::class.java)
            startActivity(intent)
        }
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.btnRegresar.setOnClickListener {
            finish()
        }
    }



    private fun sumarTiempos(tiempo1: String, tiempo2: String): String {

        fun convertirASegundos(tiempo: String): Int {
            try {
                val partes = tiempo.trim().split(" ")
                if (partes.size != 2 || partes[0].toIntOrNull() == null) {
                    throw IllegalArgumentException("Formato de tiempo no válido: $tiempo")
                }
                val numero = partes[0].toInt()
                val unidad = partes[1]
                return when (unidad) {
                    "segundo", "segundos" -> numero
                    "minuto", "minutos" -> numero * 60
                    "hora", "horas" -> numero * 3600
                    else -> throw IllegalArgumentException("Unidad de tiempo no válida: $unidad")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return -1 // Valor predeterminado en caso de error
            }
        }

        val totalSegundos = convertirASegundos(tiempo1) + convertirASegundos(tiempo2)

        val horas = totalSegundos / 3600
        val minutos = (totalSegundos % 3600) / 60
        val segundos = totalSegundos % 60

        return buildString {
            if (horas > 0) {
                append("$horas hora")
                if (horas != 1) append("s")
                append(" ")
            }
            if (minutos > 0) {
                append("$minutos minuto")
                if (minutos != 1) append("s")
                append(" ")
            }
            if (segundos > 0) {
                append("$segundos segundo")
                if (segundos != 1) append("s")
            }
        }
    }

    private fun manejarListaIngredientes(jsonIngredientes: String): String{
        println("JSON: $jsonIngredientes")
        val listaIngredientes = ManejadorJson.obtenerListaIngredientesDesdeJson(jsonIngredientes)
        println("JSON A STRING: $listaIngredientes")

        val textoIngredientes = listaIngredientes.joinToString("\n") {
            val cantidadString = if (it.cantidad % 1.0 == 0.0) {
                it.cantidad.toInt().toString()
            } else {
                it.cantidad.toString()
            }
            "$cantidadString ${it.unidad} ${it.nombre} "
        }

        return textoIngredientes
    }

    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Ha ocurrido un error. Por favor, inténtalo de nuevo más tarde.")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
            finish()
        }
        val dialog = builder.create()
        dialog.show()
    }
    companion object {
        public const val REQUEST_CODE_PERMISSION = 100
        public const val REQUEST_CODE_PICK_IMAGE = 101
    }
}
