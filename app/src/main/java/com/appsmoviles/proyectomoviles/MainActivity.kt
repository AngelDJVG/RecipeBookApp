package com.appsmoviles.proyectomoviles

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.appsmoviles.proyectomoviles.daos.RecetaDAO

import com.appsmoviles.proyectomoviles.databinding.ActivityMainBinding
import com.appsmoviles.proyectomoviles.db.AppDatabase
import com.appsmoviles.proyectomoviles.dominio.Receta
import com.appsmoviles.proyectomoviles.presentacion.CardReceta
import com.appsmoviles.proyectomoviles.utilidades.ManejadorJson
import com.appsmoviles.proyectomoviles.utilidades.RecetaManejador
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.appsmoviles.proyectomoviles.utilidades.VinculadorSensorLuz

class MainActivity : VinculadorSensorLuz() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var recetaDao: RecetaDAO
    private var limiteRecetasPaginado: Int = 5
    private var numeroPaginado: Int = 0
    private var textoBusqueda: String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //val recetaManejador = RecetaManejador(this)
        //recetaManejador.borrarTodosLosRegistros()
        //deleteDatabase("db_recetas")


        val database = AppDatabase.getInstance(this)
        recetaDao = database.recetaDAO()
        cargarRecetas()
        actualizarBotonesPaginado()
        asignarListenerAEditText()
        asignarListenerABotones()
        otorgarPermisos()
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

        binding.btnEliminarRecetas.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    recetaDao.borrarTodasLasRecetas()
                    cargarRecetas()
                    numeroPaginado = 0
                    actualizarBotonesPaginado()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

        }
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

    private fun avanzarPagina() {
        numeroPaginado += 5
        cargarRecetas()
        actualizarBotonesPaginado()
    }

    private fun retrocederPagina() {
        if (numeroPaginado > 0) {
            numeroPaginado -= 5
            if (numeroPaginado < 0) {
                numeroPaginado = 0
            }
            cargarRecetas()
            actualizarBotonesPaginado()
        }
    }

    private fun cargarRecetas() {
        val textoBusquedaTrimmed = textoBusqueda.trim()
        if (textoBusquedaTrimmed.isEmpty()) {
            cargarTodasLasRecetasPaginadas()
        } else {
            cargarRecetasFiltradasPorNombre(textoBusquedaTrimmed)
        }
    }

    private fun cargarTodasLasRecetasPaginadas() {
        recetaDao.obtenerRecetasPaginadas(limiteRecetasPaginado, numeroPaginado)
            .onEach { recipes ->
                mostrarRecetas(recipes)
            }.launchIn(lifecycleScope)
    }

    private fun cargarRecetasFiltradasPorNombre(nombre: String) {
        recetaDao.buscarRecetasPaginadasPorNombre(nombre, limiteRecetasPaginado, numeroPaginado)
            .onEach { recipes ->
                mostrarRecetas(recipes)
            }.launchIn(lifecycleScope)
    }

    private fun mostrarRecetas(recipes: List<Receta>) {
        CardReceta(
            this,
            recipes,
            listOf(CardReceta.BindingWrapper(mainBinding = binding))
        ).crearCardsRecetas()
    }

    private fun actualizarBotonesPaginado() {
        lifecycleScope.launch(Dispatchers.IO) {
            val totalRecetas = recetaDao.obtenerNumeroTotalRecetas().first() ?: 0
            val hayRecetasAnteriores = numeroPaginado > 0
            val hayRecetasSiguientes = numeroPaginado + limiteRecetasPaginado < totalRecetas

            val paginaActual = numeroPaginado / limiteRecetasPaginado + 1
            var paginasTotales = (totalRecetas + limiteRecetasPaginado - 1) / limiteRecetasPaginado

            withContext(Dispatchers.Main) {
                binding.btnAnteriorPaginado.isEnabled = hayRecetasAnteriores
                binding.btnSiguientePaginado.isEnabled = hayRecetasSiguientes
                if(paginasTotales == 0) paginasTotales = 1
                binding.textoPaginado.text = "$paginaActual/$paginasTotales"
            }
        }
    }
    private fun otorgarPermisos(){
        if (Settings.System.canWrite(this)) {
            // Tu código para modificar la configuración aquí
        } else {
            // Iniciar la actividad para que el usuario otorgue permiso
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:" + packageName)
            startActivity(intent)
        }
    }

}