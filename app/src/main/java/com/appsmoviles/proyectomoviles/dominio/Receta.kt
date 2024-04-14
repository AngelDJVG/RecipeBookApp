package com.appsmoviles.proyectomoviles.dominio
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.appsmoviles.proyectomoviles.enums.TipoReceta

@Entity
data class Receta(
    val titulo: String,
    val tipo: TipoReceta,
    val preparacion: String,
    val tiempoPreparacion: String,
    val tiempoCocinado: String,
    val imagen: String?,
    val listaIngredientes: String,

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
