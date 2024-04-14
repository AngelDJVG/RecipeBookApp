package com.appsmoviles.proyectomoviles.utilidades

import com.appsmoviles.proyectomoviles.dominio.Ingrediente
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

object ManejadorJson {
    fun convertirListaIngredientesAJson(listaIngredientes: List<Ingrediente>): String {
        val gson = Gson()
        return gson.toJson(listaIngredientes)
    }

    fun obtenerListaIngredientesDesdeJson(json: String): List<Ingrediente> {
        val gson = Gson()
        val tipoListaIngredientes = object : TypeToken<List<Ingrediente>>() {}.type
        return gson.fromJson(json, tipoListaIngredientes)
    }
}