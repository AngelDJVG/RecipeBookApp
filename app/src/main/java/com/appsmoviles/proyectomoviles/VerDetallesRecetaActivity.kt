package com.appsmoviles.proyectomoviles

import com.bumptech.glide.Glide
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.appsmoviles.proyectomoviles.databinding.VerDetallesRecetaBinding
import com.appsmoviles.proyectomoviles.dominio.Receta
import com.appsmoviles.proyectomoviles.utilidades.ManejadorJson

class VerDetallesRecetaActivity : AppCompatActivity() {
    private lateinit var binding: VerDetallesRecetaBinding
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VerDetallesRecetaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        asignarListenerABotones()
        val receta = intent.getParcelableExtra<Receta>("receta")

        if (receta != null) {
            desplegarDatosReceta(receta)
        } else {
            runOnUiThread {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Error")
                builder.setMessage("Ha ocurrido un error. Por favor, inténtalo de nuevo más tarde.")
                builder.setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }
                val dialog = builder.create()
                dialog.show()
            }
        }

    }

    private fun desplegarDatosReceta(receta: Receta){
        val tiempoTotal = sumarTiempos(receta.tiempoPreparacion, receta.tiempoCocinado)
        val jsonIngredientes = receta.listaIngredientes
        val listaIngredientes = manejarListaIngredientes(jsonIngredientes)

        println("DESPUES DE MANEJARLISTAINGREDIENTES $listaIngredientes")


        if (!receta.imagen.isNullOrEmpty()) {
            val imageView = findViewById<ImageView>(R.id.imagen_receta)
            Glide.with(this).load(Uri.parse(receta.imagen)).into(imageView)
        }

        findViewById<TextView>(R.id.nombre_receta).text = receta.titulo
        findViewById<TextView>(R.id.tiempo_preparacion).text = "Preparación: ${receta.tiempoPreparacion}"
        findViewById<TextView>(R.id.tiempo_cocinado).text = "Cocinado: ${receta.tiempoCocinado}"
        findViewById<TextView>(R.id.tiempo_total).text = "Total: $tiempoTotal"
        findViewById<TextView>(R.id.lista_ingredientes).text = listaIngredientes
        findViewById<TextView>(R.id.pasos_preparacion).text = receta.preparacion
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
        val tiempo1Parts = tiempo1.split(":")
        val tiempo2Parts = tiempo2.split(":")

        if (tiempo1Parts.size != 2 || tiempo2Parts.size != 2) {
            return "Formato de tiempo incorrecto"
        }

        val (horas1, minutos1) = tiempo1Parts.map { it.toInt() }
        val (horas2, minutos2) = tiempo2Parts.map { it.toInt() }

        val horasTotales = horas1 + horas2
        val minutosTotales = minutos1 + minutos2

        val horasExtra = minutosTotales / 60
        val minutosRestantes = minutosTotales % 60

        return String.format("%02d:%02d", horasTotales + horasExtra, minutosRestantes)
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
}