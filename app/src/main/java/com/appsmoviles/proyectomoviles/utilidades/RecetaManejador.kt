package com.appsmoviles.proyectomoviles.utilidades

import android.content.Context
import com.appsmoviles.proyectomoviles.dominio.Receta
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RecetaManejador(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("Recetas", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun isRecetaGuardada(receta: Receta): Boolean {
        val recetasGuardadas = getRecetasGuardadas()
        return recetasGuardadas.any { it.titulo == receta.titulo }
    }

    fun toggleGuardarReceta(receta: Receta) {
        val editor = sharedPreferences.edit()
        val recetasJson = sharedPreferences.getString("recetas_guardadas", "")
        val recetasGuardadas: MutableList<Receta> = if (!recetasJson.isNullOrEmpty()) {
            gson.fromJson(recetasJson, object : TypeToken<List<Receta>>() {}.type)
        } else {
            mutableListOf()
        }

        val index = recetasGuardadas.indexOfFirst { it.titulo == receta.titulo }
        if (index != -1) {
            recetasGuardadas.removeAt(index)
        } else {
            recetasGuardadas.add(receta)
        }

        val recetasJsonUpdated = gson.toJson(recetasGuardadas)
        editor.putString("recetas_guardadas", recetasJsonUpdated)
        editor.apply()
    }

    private fun saveRecetasGuardadas(recetas: Set<Receta>) {
        val editor = sharedPreferences.edit()
        val json = gson.toJson(recetas.toList())
        editor.putString("recetas_guardadas", json)
        editor.apply()
    }

    fun getRecetasGuardadas(): Set<Receta> {
        val json = sharedPreferences.getString("recetas_guardadas", null)
        return if (json != null) {
            gson.fromJson(json, object : TypeToken<Set<Receta>>() {}.type)
        } else {
            emptySet()
        }
    }
}