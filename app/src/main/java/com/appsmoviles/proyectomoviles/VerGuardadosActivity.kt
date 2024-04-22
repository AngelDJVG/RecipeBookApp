package com.appsmoviles.proyectomoviles

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.appsmoviles.proyectomoviles.databinding.VerGuardadosBinding
import com.appsmoviles.proyectomoviles.dominio.Receta
import com.appsmoviles.proyectomoviles.presentacion.CardReceta
import com.appsmoviles.proyectomoviles.utilidades.RecetaGuardadaListener
import com.appsmoviles.proyectomoviles.utilidades.RecetaManejador
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class VerGuardadosActivity : AppCompatActivity(), RecetaGuardadaListener {

    private lateinit var binding: VerGuardadosBinding
    private lateinit var recetaManejador: RecetaManejador
    private var limiteRecetasPaginado: Int = 5
    private var numeroPaginado: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VerGuardadosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recetaManejador = RecetaManejador(this)
        mostrarRecetasGuardadas()
        asignarListenerABotones()

    }

    private fun asignarListenerABotones() {
        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AgregarRecetaActivity::class.java)
            startActivity(intent)
        }
        binding.btnSaved.setOnClickListener {
            val intent = Intent(this, VerGuardadosActivity::class.java)
            startActivity(intent)
        }

        binding.btnAnteriorPaginado.setOnClickListener {
            retrocederPagina()
        }

        binding.btnSiguientePaginado.setOnClickListener {
            avanzarPagina()

        }
    }

    private fun avanzarPagina() {
        numeroPaginado += 5
        cargarRecetasPreferences()
        actualizarBotonesPaginado()
    }

    private fun retrocederPagina() {
        if (numeroPaginado > 0) {
            numeroPaginado -= 5
            if (numeroPaginado < 0) {
                numeroPaginado = 0
            }
            cargarRecetasPreferences()
            actualizarBotonesPaginado()
        }
    }

    private fun mostrarRecetasGuardadas() {
        val recetasGuardadas = recetaManejador.getRecetasGuardadas()

        val cardReceta = CardReceta(this, recetasGuardadas.toList(), listOf(CardReceta.BindingWrapper(verGuardadosBinding = binding)),this)
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

    private fun cargarRecetasPreferences() {
        //CARGAR RECETAS DEL SHARED
    }

    private fun actualizarBotonesPaginado() {
        //METER LO DEL SHARED

        /*

        lifecycleScope.launch(Dispatchers.IO) {
            val totalRecetas = recetaDao.obtenerNumeroTotalRecetas().first() ?: 0
            val hayRecetasAnteriores = numeroPaginado > 0
            val hayRecetasSiguientes = numeroPaginado + limiteRecetasPaginado < totalRecetas

            val paginaActual = numeroPaginado / limiteRecetasPaginado + 1
            val paginasTotales = (totalRecetas + limiteRecetasPaginado - 1) / limiteRecetasPaginado

            withContext(Dispatchers.Main) {
                binding.btnAnteriorPaginado.isEnabled = hayRecetasAnteriores
                binding.btnSiguientePaginado.isEnabled = hayRecetasSiguientes

                binding.textoPaginado.text = "$paginaActual/$paginasTotales"
            }
        }
         */

    }
}