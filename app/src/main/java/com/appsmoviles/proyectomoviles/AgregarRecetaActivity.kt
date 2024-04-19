package com.appsmoviles.proyectomoviles


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

import androidx.appcompat.app.AppCompatActivity
import com.appsmoviles.proyectomoviles.daos.RecetaDAO
import com.appsmoviles.proyectomoviles.db.AppDatabase
import com.appsmoviles.proyectomoviles.databinding.AgregarRecetaBinding
import com.appsmoviles.proyectomoviles.dominio.Ingrediente
import com.appsmoviles.proyectomoviles.dominio.Receta
import com.appsmoviles.proyectomoviles.enums.TipoReceta
import com.appsmoviles.proyectomoviles.enums.Unidad
import com.appsmoviles.proyectomoviles.utilidades.ManejadorJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AgregarRecetaActivity : AppCompatActivity() {
    private lateinit var binding: AgregarRecetaBinding
    private lateinit var recetaDAO: RecetaDAO
    private val listaIngredientes = mutableListOf<Ingrediente>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AgregarRecetaBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bd = AppDatabase.getInstance(applicationContext)
        recetaDAO = bd.recetaDAO()


        binding.btnRegresar.setOnClickListener {
            finish()
        }

        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.btnSaved.setOnClickListener {
            val intent = Intent(this, VerGuardadosActivity::class.java)
            startActivity(intent)
        }
        binding.btnSaveRecipe.setOnClickListener {
            guardarReceta()
        }

        binding.btnAddIngrediente.setOnClickListener {
            agregarIngrediente()
        }
    }

    private fun guardarReceta() {
        val titulo = binding.txtEditTitle.text.toString()
        val tipo = obtenerTipoReceta()
        val preparacion = binding.txtEditPreparation.text.toString()
        val tiempoPreparacion = binding.textTimePreparation.text.toString()
        val tiempoCocinado = binding.textTimeCook.text.toString()
        val imagen: String? = null

        val nuevaReceta = Receta(
            titulo = titulo,
            tipo = tipo,
            preparacion = preparacion,
            tiempoPreparacion = tiempoPreparacion,
            tiempoCocinado = tiempoCocinado,
            imagen = imagen,
            listaIngredientes = ManejadorJson.convertirListaIngredientesAJson(listaIngredientes)
        )
        GlobalScope.launch(Dispatchers.IO) {
            try {
                recetaDAO.insertarReceta(nuevaReceta)
                mostrarMensaje("Receta registrada con éxito")
            } catch (ex: Exception) {
                mostrarMensaje("VALIO MADRE")
            }
        }
    }

    private fun obtenerTipoReceta(): TipoReceta {
        val spinner = binding.spinnerTipoReceta
        val tipoSeleccionado = spinner.selectedItemPosition
        return TipoReceta.entries[tipoSeleccionado]
    }

    private fun agregarIngrediente() {
        val nombre = binding.txtNombreIngrediente.text.toString()
        val cantidad = binding.txtCantidadIngredientes.text.toString().toDoubleOrNull() ?: 0.0
        val unidadString = binding.spinnerUnidad.selectedItem.toString()
        val unidad = when (unidadString) {
            "PZ" -> Unidad.PZ
            "KG" -> Unidad.KG
            else -> Unidad.PZ
        }
        val nuevoIngrediente = Ingrediente(nombre, cantidad, unidad)
        listaIngredientes.add(nuevoIngrediente)
        actualizarLayoutIngredientes(nuevoIngrediente)
    }

    private fun actualizarLayoutIngredientes(nuevoIngrediente: Ingrediente) {
        val linearLayout = LinearLayout(applicationContext).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            orientation = LinearLayout.HORIZONTAL

        }

        val textView = TextView(applicationContext).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
            )
            text = "${nuevoIngrediente.nombre}: ${nuevoIngrediente.cantidad} ${nuevoIngrediente.unidad}"
        }

        val deleteButton = Button(applicationContext).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            text = "X"
            setOnClickListener {
                listaIngredientes.remove(nuevoIngrediente)
                binding.apartadoIngredientes.removeView(linearLayout)
            }
        }

        linearLayout.addView(textView)
        linearLayout.addView(deleteButton)
        listaIngredientes.add(nuevoIngrediente)
        binding.apartadoIngredientes.addView(linearLayout)
    }


    private fun mostrarMensaje(mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage(mensaje)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}