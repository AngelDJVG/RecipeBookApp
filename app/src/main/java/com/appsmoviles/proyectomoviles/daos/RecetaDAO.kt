
package com.appsmoviles.proyectomoviles.daos
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.appsmoviles.proyectomoviles.dominio.Receta
import kotlinx.coroutines.flow.Flow
@Dao
interface RecetaDAO {


    @Insert
    fun insertarReceta(receta: Receta)

    @Query("DELETE FROM Receta WHERE id = :id")
    fun borrarReceta(id: Long)

    @Query("SELECT * FROM Receta")
    fun obtenerTodasLasRecetas(): Flow<List<Receta>>

    @Query("SELECT * FROM Receta WHERE id = :id")
    fun obtenerRecetaPorId(id: Long): Flow<Receta?>
}