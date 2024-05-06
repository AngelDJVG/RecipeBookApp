package com.appsmoviles.proyectomoviles

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter

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
    private var opcion: String = "Nuevas"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val database = AppDatabase.getInstance(this)
        recetaDao = database.recetaDAO()
        actualizarBotonesPaginado()
        asignarListenerAEditText()
        asignarListenerABotones()
        otorgarPermisos()
        configurarComboBoxFiltro()
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
                    cargarRecetas(opcion)
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
                cargarRecetas(opcion)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

    }

    private fun avanzarPagina() {
        numeroPaginado += 5
        cargarRecetas(opcion)
        actualizarBotonesPaginado()
    }

    private fun retrocederPagina() {
        if (numeroPaginado > 0) {
            numeroPaginado -= 5
            if (numeroPaginado < 0) {
                numeroPaginado = 0
            }
            cargarRecetas(opcion)
            actualizarBotonesPaginado()
        }
    }

    private fun cargarRecetas(filtro: String) {
        val textoBusquedaTrimmed = textoBusqueda.trim()
        if (textoBusquedaTrimmed.isEmpty()) {
            cargarTodasLasRecetasPaginadas(filtro)
        } else {
            cargarRecetasFiltradasPorNombre(textoBusquedaTrimmed)
        }
    }

    private fun cargarTodasLasRecetasPaginadas(filtro: String) {
        if(filtro == "Nuevas")
        {
            recetaDao.obtenerRecetasPaginadasNuevas(limiteRecetasPaginado, numeroPaginado)
                .onEach { recipes ->
                    mostrarRecetas(recipes)
                }.launchIn(lifecycleScope)
        }
        if(filtro == "Viejas")
        {
            recetaDao.obtenerRecetasPaginadasViejas(limiteRecetasPaginado, numeroPaginado)
                .onEach { recipes ->
                    mostrarRecetas(recipes)
                }.launchIn(lifecycleScope)
        }
        if(filtro == "Titulo")
        {
            recetaDao.obtenerRecetasPaginadasTitulo(limiteRecetasPaginado, numeroPaginado)
                .onEach { recipes ->
                    mostrarRecetas(recipes)
                }.launchIn(lifecycleScope)
        }
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
        } else {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:" + packageName)
            startActivity(intent)
        }
    }
    private fun configurarComboBoxFiltro() {
        val opcionesFiltro = resources.getStringArray(R.array.filtros)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opcionesFiltro)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.filtroSpinner.adapter = adapter

        binding.filtroSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val opcionSeleccionada = opcionesFiltro[position]
                opcion = opcionSeleccionada
                when (opcionSeleccionada) {
                    "Nuevas" -> {
                        cargarRecetas("Nuevas")
                    }
                    "Viejas" -> {
                        cargarRecetas("Viejas")
                    }
                    "Titulo" -> {
                        cargarRecetas("Titulo")
                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }


}