package com.appsmoviles.proyectomoviles


import ValidadorRecetas
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.appsmoviles.proyectomoviles.daos.RecetaDAO
import com.appsmoviles.proyectomoviles.databinding.AgregarRecetaBinding
import com.appsmoviles.proyectomoviles.db.AppDatabase
import com.appsmoviles.proyectomoviles.dominio.Ingrediente
import com.appsmoviles.proyectomoviles.dominio.Receta
import com.appsmoviles.proyectomoviles.enums.TipoReceta
import com.appsmoviles.proyectomoviles.enums.Unidad
import com.appsmoviles.proyectomoviles.utilidades.ManejadorJson
import com.appsmoviles.proyectomoviles.utilidades.VinculadorSensorLuz
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AgregarRecetaActivity : VinculadorSensorLuz() {
    private lateinit var binding: AgregarRecetaBinding
    private lateinit var recetaDAO: RecetaDAO
    private val listaIngredientes = mutableListOf<Ingrediente>()
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AgregarRecetaBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bd = AppDatabase.getInstance(applicationContext)
        recetaDAO = bd.recetaDAO()
        asignarListenerABotones()
    }


    private fun asignarListenerABotones() {
        binding.btnRegresar.setOnClickListener {
            finish()
        }

        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnSaved.setOnClickListener {
            val intent = Intent(this, VerGuardadosActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnSaveRecipe.setOnClickListener {
            guardarReceta()
        }

        binding.btnAddIngrediente.setOnClickListener {
            agregarIngrediente()
            limpiarFormIngredientes()
        }

        binding.btnSeleccionarImagen.setOnClickListener {
            abrirGaleria()
        }
    }

    private fun limpiarFormIngredientes(){
        binding.txtNombreIngrediente.text.clear()
        binding.txtCantidadIngredientes.text.clear()
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria()
            } else {

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.data

            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(imageUri!!, takeFlags)

            binding.imageViewReceta.setImageURI(imageUri)
            binding.imageViewReceta.scaleType= ImageView.ScaleType.FIT_XY
        }
    }

    private fun guardarReceta() {
        val validador = ValidadorRecetas()
        val msgErrores = mutableListOf<String?>()

        val titulo = binding.txtEditTitle.text.toString()
        validador.validarTitulo(titulo)?.let { msgErrores.add(it) }

        val tipo = obtenerTipoReceta()
        validador.validarTipo(tipo)?.let { msgErrores.add(it) }


        validador.validarIngredientes(listaIngredientes)?.let{msgErrores.add(it)}


        val preparacion = binding.txtEditPreparation.text.toString()
        validador.validarPreparacion(preparacion)?.let { msgErrores.add(it) }

        val tiempoPreparacion = binding.textTimePreparation.text.toString()
        val unidadPreparacion = binding.spinnerUnidadPreparacion.selectedItem.toString()
        validador.validarTiempoPreparacion(tiempoPreparacion, unidadPreparacion)?.let { msgErrores.add(it) }

        val totalPreparacion = "$tiempoPreparacion $unidadPreparacion"

        val tiempoCocinado = binding.textTimeCook.text.toString()
        val unidadCocinado = binding.spinnerUnidadCocinado.selectedItem.toString()
        validador.validarTiempoCocinado(tiempoCocinado, unidadCocinado)?.let { msgErrores.add(it) }

        val totalCocinado = "$tiempoCocinado $unidadCocinado"

        val imagen = imageUri.toString()
        println(imagen)
        validador.validarImagen(imagen)?.let { msgErrores.add(it) }

        if (msgErrores.isNotEmpty()) {
            mostrarMensajeFaltaDatos(msgErrores)
            return
        }
        var ingredienteJSON = ManejadorJson.convertirListaIngredientesAJson(listaIngredientes)
        val recetaAGuardar = extraerDatos(titulo, tipo, preparacion, totalPreparacion, totalCocinado,imagen, ingredienteJSON)
        agregarReceta(recetaAGuardar)
    }

    private fun extraerDatos(
        titulo: String,
        tipo: TipoReceta,
        preparacion: String,
        tiempoPreparacion: String,
        tiempoCocinado: String,
        imagen: String?,
        ingredientes: String
    ): Receta {

        return Receta(
            titulo = titulo,
            tipo = tipo,
            preparacion = preparacion,
            tiempoPreparacion = tiempoPreparacion,
            tiempoCocinado = tiempoCocinado,
            imagen = imagen,
            listaIngredientes = ingredientes
        )
    }

    private fun agregarReceta(nuevaReceta: Receta){
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                recetaDAO.insertarReceta(nuevaReceta)
                mostrarMensajeExito()
            } catch (ex: Exception) {
                mostrarMensajeErrorAndBackToMain()
            }
        }
    }


    private fun obtenerTipoReceta(): TipoReceta {
        val spinner = binding.spinnerTipoReceta
        val tipoSeleccionado = spinner.selectedItemPosition
        return TipoReceta.entries[tipoSeleccionado]
    }

    private fun agregarIngrediente() {

        val validador = ValidadorRecetas()
        val msgErrores = mutableListOf<String?>()

        val nombre = binding.txtNombreIngrediente.text.toString()
        validador.validarTXTNombre(nombre)?.let { msgErrores.add(it) }

        val cantidad = binding.txtCantidadIngredientes.text.toString()
        val unidadString = binding.spinnerUnidad.selectedItem.toString()
        val unidad = when (unidadString) {
            "PZ" -> Unidad.PZ
            "KG" -> Unidad.KG
            else -> Unidad.PZ
        }

        validador.validarTXTCantidad(cantidad,unidad.toString())?.let { msgErrores.add(it) }

        if (msgErrores.isNotEmpty()) {
            mostrarMensajeFaltaDatos(msgErrores)
            return
        }
        val nuevoIngrediente = Ingrediente(nombre, cantidad.toDoubleOrNull() ?: 0.0, unidad)
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
            text =
                "${nuevoIngrediente.nombre}: ${nuevoIngrediente.cantidad} ${nuevoIngrediente.unidad}"
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


    private fun mostrarMensajeErrorAndBackToMain() {
        runOnUiThread {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Error")
            builder.setMessage("Ha ocurrido un error. Por favor, inténtalo de nuevo más tarde.")
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                volverAPantallaPrincipal()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun mostrarMensajeExito() {
        runOnUiThread {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Éxito")
            builder.setMessage("¡La receta se ha guardado con éxito!")
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                volverAPantallaPrincipal()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun mostrarMensajeFaltaDatos(msgErrores: List<String?>) {
        runOnUiThread {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Faltan datos")
            val errorMessages = msgErrores.filterNotNull().joinToString("\n")
            builder.setMessage(errorMessages)
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun volverAPantallaPrincipal() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSION = 100
        const val REQUEST_CODE_PICK_IMAGE = 101
    }

}