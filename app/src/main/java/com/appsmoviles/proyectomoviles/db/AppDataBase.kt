package com.appsmoviles.proyectomoviles.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import com.appsmoviles.proyectomoviles.daos.RecetaDAO
import com.appsmoviles.proyectomoviles.dominio.Receta

@Database(entities = [Receta::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recetaDAO(): RecetaDAO

    companion object {
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "db_recetas"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}