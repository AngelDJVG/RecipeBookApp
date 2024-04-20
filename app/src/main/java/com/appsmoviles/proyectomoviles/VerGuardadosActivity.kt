package com.appsmoviles.proyectomoviles

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.appsmoviles.proyectomoviles.databinding.VerGuardadosBinding
import com.appsmoviles.proyectomoviles.dominio.Receta
import com.appsmoviles.proyectomoviles.presentacion.CardReceta
import com.appsmoviles.proyectomoviles.utilidades.RecetaGuardadaListener
import com.google.gson.Gson
import kotlin.math.roundToInt

class VerGuardadosActivity : AppCompatActivity(), RecetaGuardadaListener {

    private lateinit var binding: VerGuardadosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VerGuardadosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mostrarRecetasGuardadas()
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AgregarRecetaActivity::class.java)
            startActivity(intent)
        }
    }

    private fun mostrarRecetasGuardadas() {
        val sharedPreferences = getSharedPreferences("recetas", Context.MODE_PRIVATE)
        val recetasJson = sharedPreferences.getString("recetas_guardadas", "")
        val gson = Gson()
        val recetasGuardadas: List<Receta> = if (!recetasJson.isNullOrEmpty()) {
            gson.fromJson(recetasJson, Array<Receta>::class.java).toList()
        } else {
            emptyList()
        }

        val cardReceta = CardReceta(this, recetasGuardadas, listOf(CardReceta.BindingWrapper(verGuardadosBinding = binding)),this)
        cardReceta.crearCardsRecetas()

        if (recetasGuardadas.isEmpty()) {
            val noRecetasText = TextView(this).apply {
                text = "No hay recetas guardadas."
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    topMargin = 16.dpToPx(context)
                }
            }
            binding.layoutContenedorCards.addView(noRecetasText)
        }
    }

    private fun Int.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics
        ).roundToInt()
    }

    override fun cambioGuardado() {
        binding.layoutContenedorCards.removeAllViews()
        mostrarRecetasGuardadas()
    }
}