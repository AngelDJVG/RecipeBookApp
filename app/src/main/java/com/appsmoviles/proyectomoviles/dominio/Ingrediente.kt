package com.appsmoviles.proyectomoviles.dominio


import com.appsmoviles.proyectomoviles.enums.Unidad


data class Ingrediente(
    val nombre: String,
    val cantidad: Double,
    val unidad: Unidad,
)