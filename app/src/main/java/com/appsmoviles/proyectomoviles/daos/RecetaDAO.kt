
package com.appsmoviles.proyectomoviles.daos
import androidx.room.Dao
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

    @Query("DELETE FROM Receta")
    fun borrarTodasLasRecetas()

    @Query("SELECT * FROM Receta")
    fun obtenerTodasLasRecetas(): Flow<List<Receta>>

    @Query("SELECT * FROM Receta WHERE id = :id")
    fun obtenerRecetaPorId(id: Int): Receta?

    @Query("SELECT * FROM Receta WHERE titulo LIKE '%' || :titulo || '%' LIMIT :limit OFFSET :offset")
    fun buscarRecetasPaginadasPorNombre(titulo: String, limit: Int, offset: Int): Flow<List<Receta>>

    @Query("SELECT * FROM Receta ORDER BY id DESC LIMIT :limit OFFSET :offset")
    fun obtenerRecetasPaginadasNuevas(limit: Int, offset: Int): Flow<List<Receta>>
    @Query("SELECT * FROM Receta ORDER BY id LIMIT :limit OFFSET :offset")
    fun obtenerRecetasPaginadasViejas(limit: Int, offset: Int): Flow<List<Receta>>
    @Query("SELECT COUNT(*) FROM Receta")
    fun obtenerNumeroTotalRecetas(): Flow<Int>
    @Query("SELECT * FROM Receta ORDER BY LOWER(titulo) LIMIT :limit OFFSET :offset")
    fun obtenerRecetasPaginadasTitulo(limit: Int, offset: Int): Flow<List<Receta>>


}