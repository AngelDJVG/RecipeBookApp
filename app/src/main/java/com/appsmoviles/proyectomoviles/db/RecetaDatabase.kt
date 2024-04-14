package com.appsmoviles.proyectomoviles.db
import androidx.room.Database
import androidx.room.RoomDatabase
import com.appsmoviles.proyectomoviles.daos.RecetaDAO
import com.appsmoviles.proyectomoviles.dominio.Receta


@Database(entities = [Receta::class], version = 1)
abstract class RecetaDatabase: RoomDatabase(){
    abstract val dao: RecetaDAO
}