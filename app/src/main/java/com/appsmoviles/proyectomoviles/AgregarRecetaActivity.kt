package com.appsmoviles.proyectomoviles


import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.appsmoviles.proyectomoviles.daos.RecetaDAO
import com.appsmoviles.proyectomoviles.databinding.AgregarRecetaBinding
import com.appsmoviles.proyectomoviles.db.AppDatabase
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
        }

        binding.btnSeleccionarImagen.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE_PERMISSION
                )
            } else {
                abrirGaleria()
            }
        }
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
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
            // Obtener la URI de la imagen seleccionada
            imageUri = data.data
            // Aquí puedes hacer lo que quieras con la imagen seleccionada, por ejemplo, mostrarla en un ImageView
            binding.imageViewReceta.setImageURI(imageUri)
            binding.imageViewReceta.scaleType= ImageView.ScaleType.FIT_XY
        }
    }

    private fun guardarReceta() {
        val titulo = binding.txtEditTitle.text.toString()
        val tipo = obtenerTipoReceta()
        val preparacion = binding.txtEditPreparation.text.toString()
        val tiempoPreparacion = binding.textTimePreparation.text.toString() +
                if (binding.textTimePreparation.text.toString() == "1") {
                    " " + binding.spinnerUnidadPreparacion.selectedItem.toString().removeSuffix("s")
                } else {
                    " " + binding.spinnerUnidadPreparacion.selectedItem.toString()
                }

        val tiempoCocinado = binding.textTimeCook.text.toString() +
                if (binding.textTimeCook.text.toString() == "1") {
                    " " + binding.spinnerUnidadCocinado.selectedItem.toString().removeSuffix("s")
                } else {
                    " " + binding.spinnerUnidadCocinado.selectedItem.toString()
                }
        var imagen: String? = null

        if (imageUri != null) {
            imagen = imageUri.toString()
        }

        val nuevaReceta = Receta(
            titulo = titulo,
            tipo = tipo,
            preparacion = preparacion,
            tiempoPreparacion = tiempoPreparacion,
            tiempoCocinado = tiempoCocinado,
            imagen = imagen,
            listaIngredientes = ManejadorJson.convertirListaIngredientesAJson(listaIngredientes)
        )
        agregarReceta(nuevaReceta)
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
        val nombre = binding.txtNombreIngrediente.text.toString()
        val cantidad = binding.txtCantidadIngredientes.text.toString().toDoubleOrNull() ?: 0.0
        val unidadString = binding.spinnerUnidad.selectedItem.toString()
        val unidad = when (unidadString) {
            "PZ" -> Unidad.PZ
            "KG" -> Unidad.KG
            else -> Unidad.PZ
        }
        val nuevoIngrediente = Ingrediente(nombre, cantidad, unidad)
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

    private fun mostrarMensajeFaltaDatos() {
        runOnUiThread {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Faltan datos")
            builder.setMessage("Por favor, completa todos los campos antes de continuar.")
            builder.setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
                volverAPantallaPrincipal()
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