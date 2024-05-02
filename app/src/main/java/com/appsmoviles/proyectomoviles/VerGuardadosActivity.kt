package com.appsmoviles.proyectomoviles

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.appsmoviles.proyectomoviles.utilidades.VinculadorSensorLuz
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class VerGuardadosActivity : VinculadorSensorLuz(), RecetaGuardadaListener {

    private lateinit var binding: VerGuardadosBinding
    private lateinit var recetaManejador: RecetaManejador
    private var limiteRecetasPaginado: Int = 5
    private var numeroPaginado: Int = 1
    private var textoBusqueda : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = VerGuardadosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recetaManejador = RecetaManejador(this)
        mostrarRecetasGuardadas()
        asignarListenerABotones()
        actualizarBotonesPaginado()
        asignarListenerAEditText()
    }
    private fun asignarListenerAEditText() {
        binding.buscadorEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                textoBusqueda = s.toString()
                cargarRecetas()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No es necesario implementar este método
            }
        })

        // Resto de tu código...
    }
    private fun cargarRecetas() {
        val textoBusquedaTrimmed = textoBusqueda.trim()
        if (textoBusquedaTrimmed.isEmpty()) {
            mostrarRecetasGuardadas()
        } else {
            mostrarRecetasBusqueda(textoBusquedaTrimmed)
        }
    }

    private fun asignarListenerABotones() {
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
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
        numeroPaginado += 1
        mostrarRecetasGuardadas()
        actualizarBotonesPaginado()
    }

    private fun retrocederPagina() {
        if (numeroPaginado > 1) {
            numeroPaginado -= 1
            if (numeroPaginado < 1) {
                numeroPaginado = 1
            }
            mostrarRecetasGuardadas()
            actualizarBotonesPaginado()
        }
    }
    private fun mostrarRecetasBusqueda(Busqueda: String) {
        val recetasGuardadas = recetaManejador.buscarRecetas(Busqueda)

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
    private fun mostrarRecetasGuardadas() {
        val recetasGuardadas = recetaManejador.getRecetasGuardadasPaginado(limiteRecetasPaginado, numeroPaginado)

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

    private fun cargarRecetasPreferences() {
        //CARGAR RECETAS DEL SHARED
    }

    private fun actualizarBotonesPaginado() {
        lifecycleScope.launch(Dispatchers.IO) {
            val totalRecetas = recetaManejador.getRecetasGuardadas().size


            val paginaActual = numeroPaginado
            var paginasTotales = (totalRecetas + limiteRecetasPaginado - 1) / limiteRecetasPaginado

            val hayRecetasAnteriores = paginaActual > 1
            val hayRecetasSiguientes = paginaActual < paginasTotales

            withContext(Dispatchers.Main) {
                binding.btnAnteriorPaginado.isEnabled = hayRecetasAnteriores
                binding.btnSiguientePaginado.isEnabled = hayRecetasSiguientes
                if(paginasTotales == 0) paginasTotales = 1
                binding.textoPaginado.text = "$paginaActual/$paginasTotales"
            }
        }
    }
}