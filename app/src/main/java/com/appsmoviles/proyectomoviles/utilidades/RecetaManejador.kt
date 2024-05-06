package com.appsmoviles.proyectomoviles.utilidades

import android.content.Context
import com.appsmoviles.proyectomoviles.dominio.Receta
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken



class RecetaManejador(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("Recetas", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val RECETAS_GUARDADAS_KEY = "recetas_guardadas"
    }

    fun isRecetaGuardada(receta: Receta): Boolean {
        val recetasGuardadas = getRecetasGuardadas()
        return recetasGuardadas.any { it.titulo == receta.titulo }
    }

    fun toggleGuardarReceta(receta: Receta) {
        val editor = sharedPreferences.edit()
        val recetasGuardadas = getRecetasGuardadas().toMutableList()

        val index = recetasGuardadas.indexOfFirst { it.titulo == receta.titulo }
        if (index != -1) {
            recetasGuardadas.removeAt(index)
        } else {
            recetasGuardadas.add(receta)
        }

        val recetasJsonUpdated = gson.toJson(recetasGuardadas)
        editor.putString(RECETAS_GUARDADAS_KEY, recetasJsonUpdated)
        editor.apply()
    }

    fun getRecetasGuardadas(): Set<Receta> {
        val json = sharedPreferences.getString(RECETAS_GUARDADAS_KEY, null)
        return json?.let {
            gson.fromJson(it, object : TypeToken<Set<Receta>>() {}.type)
        } ?: emptySet()
    }

    fun getRecetasGuardadasPaginado(itemsPerPage: Int, pageNumber: Int, filtro: String): List<Receta> {
        val json = sharedPreferences.getString(RECETAS_GUARDADAS_KEY, null)
        val recetasGuardadas = json?.let {
            gson.fromJson<List<Receta>>(it, object : TypeToken<List<Receta>>() {}.type)
        } ?: emptyList()

        val recetasFiltradas = when (filtro) {
            "Titulo" -> recetasGuardadas.sortedBy { it.titulo }
            "Viejas" -> recetasGuardadas.sortedWith(
                compareBy(
                    { it.id }
                )
            )
            else -> recetasGuardadas
        }

        val startIndex = (pageNumber - 1) * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, recetasFiltradas.size)
        return recetasFiltradas.subList(startIndex, endIndex)
    }


    fun borrarTodosLosRegistros() {
        val editor = sharedPreferences.edit()
        editor.remove(RECETAS_GUARDADAS_KEY)
        editor.apply()
    }
    fun buscarRecetas(query: String): List<Receta> {
        val recetasGuardadas = getRecetasGuardadas()
        return recetasGuardadas.filter { receta ->
            receta.titulo.contains(query, ignoreCase = true)
        }
    }

}