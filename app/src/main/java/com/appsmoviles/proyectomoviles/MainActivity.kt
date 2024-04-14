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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var recetaDao: RecetaDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

           // deleteDatabase("db_recetas")

        val database = AppDatabase.getInstance(this)
        recetaDao = database.recetaDAO()

        // Observa las recetas de la base de datos y crea una tarjeta para cada una
        recetaDao.obtenerTodasLasRecetas().onEach { recipes ->
            CardReceta(this, recipes, binding).crearCardsRecetas()
        }.launchIn(lifecycleScope)

        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AgregarRecetaActivity::class.java)
            startActivity(intent)
        }
    }
}