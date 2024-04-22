package com.appsmoviles.proyectomoviles

import android.content.Context
import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.appsmoviles.proyectomoviles.daos.RecetaDAO

import com.appsmoviles.proyectomoviles.databinding.ActivityMainBinding
import com.appsmoviles.proyectomoviles.db.AppDatabase
import com.appsmoviles.proyectomoviles.presentacion.CardReceta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var recetaDao: RecetaDAO
    private var limiteRecetasPaginado: Int = 5
    private var numeroPaginado: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // deleteDatabase("db_recetas")

        val database = AppDatabase.getInstance(this)
        recetaDao = database.recetaDAO()
        cargarRecetas()
        actualizarBotonesPaginado()
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
        recetaDao.obtenerRecetasPaginadas(limiteRecetasPaginado, numeroPaginado)
            .onEach { recipes ->
                CardReceta(
                    this,
                    recipes,
                    listOf(CardReceta.BindingWrapper(mainBinding = binding))
                ).crearCardsRecetas()
            }.launchIn(lifecycleScope)
    }

    private fun actualizarBotonesPaginado() {
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
    }
}