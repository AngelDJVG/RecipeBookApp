package com.appsmoviles.proyectomoviles

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.appsmoviles.proyectomoviles.databinding.VerDetallesRecetaBinding
import com.appsmoviles.proyectomoviles.dominio.Receta

class VerDetallesRecetaActivity : AppCompatActivity() {
    private lateinit var binding: VerDetallesRecetaBinding
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VerDetallesRecetaBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        // Obtener la instancia de Receta enviada desde la actividad anterior de manera segura
        val receta = intent.getParcelableExtra("receta",Receta::class.java)

        // Verificar si la receta no es nula
        if (receta != null) {
            // Calcular el tiempo total sumando el tiempo de preparación y el tiempo cocinado
            val tiempoTotal = sumarTiempos(receta.tiempoPreparacion, receta.tiempoCocinado)

            // Mostrar los datos en los campos correspondientes
            findViewById<ImageView>(R.id.imagen_receta).setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.imagen_receta
                )
            ) // Cambia "imagen_receta" por el nombre del recurso de la imagen
            findViewById<TextView>(R.id.nombre_receta).text = receta.titulo
            findViewById<TextView>(R.id.tiempo_preparacion).text = "Preparación: ${receta.tiempoPreparacion}"
            findViewById<TextView>(R.id.tiempo_cocinado).text = "Cocinado: ${receta.tiempoCocinado}"
            findViewById<TextView>(R.id.tiempo_total).text = "Total: $tiempoTotal"
            findViewById<TextView>(R.id.lista_ingredientes).text = receta.listaIngredientes
            findViewById<TextView>(R.id.pasos_preparacion).text = receta.preparacion
        } else {
            // Manejar el caso en el que no se recibió ninguna receta
            // Por ejemplo, mostrar un mensaje de error o volver a la actividad anterior
            // Aquí puedes agregar tu lógica personalizada
        }

    }

    private fun sumarTiempos(tiempo1: String, tiempo2: String): String {
        val tiempo1Parts = tiempo1.split(":")
        val tiempo2Parts = tiempo2.split(":")

        // Verificar si los tiempos tienen el formato esperado
        if (tiempo1Parts.size != 2 || tiempo2Parts.size != 2) {
            return "Formato de tiempo incorrecto"
        }

        val (horas1, minutos1) = tiempo1Parts.map { it.toInt() }
        val (horas2, minutos2) = tiempo2Parts.map { it.toInt() }

        // Sumar horas y minutos
        val horasTotales = horas1 + horas2
        val minutosTotales = minutos1 + minutos2

        // Ajustar los minutos si superan 60
        val horasExtra = minutosTotales / 60
        val minutosRestantes = minutosTotales % 60

        // Devolver el tiempo total en formato "HH:MM"
        return String.format("%02d:%02d", horasTotales + horasExtra, minutosRestantes)
    }
}