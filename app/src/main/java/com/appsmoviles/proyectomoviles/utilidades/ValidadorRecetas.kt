import android.net.Uri
import com.appsmoviles.proyectomoviles.R
import com.appsmoviles.proyectomoviles.dominio.Ingrediente
import com.appsmoviles.proyectomoviles.enums.TipoReceta

class ValidadorRecetas() {

    fun validarTitulo(titulo: String): String? {
        return if (titulo.isEmpty()) {
            "El título de la receta no puede estar vacío."
        } else {
            null
        }
    }

    fun validarTipo(tipo: TipoReceta): String? {
        return if (tipo.toString().isNullOrEmpty()) {
            "Debe seleccionar un tipo de receta."
        } else {
            null
        }
    }

    fun validarPreparacion(preparacion: String): String? {
        return if (preparacion.isEmpty()) {
            "La descripción de la preparación no puede estar vacía."
        } else {
            null
        }
    }

    fun validarTiempoPreparacion(tiempo: String, unidad: String): String? {
        return if (tiempo.isEmpty()) {
            "El tiempo de preparación de no puede estar vacío."
        } else if (!tiempo.matches(Regex("\\d+"))) {
            "El tiempo de preparación debe ser un número válido."
        } else if (unidad.isEmpty()) {
            "Debe seleccionar una unidad de tiempo."
        } else {
            null
        }
    }

    fun validarTiempoCocinado(tiempo: String, unidad: String): String? {
        return if (tiempo.isEmpty()) {
            "El tiempo de cocinado no puede estar vacío."
        } else if (!tiempo.matches(Regex("\\d+"))) {
            "El tiempo de cocinado debe ser un número válido."
        } else if (unidad.isEmpty()) {
            "Debe seleccionar una unidad de tiempo."
        } else {
            null
        }
    }

    fun validarImagen(imagenURI: String?): String? {
        return if (imagenURI == "null" || imagenURI.isNullOrEmpty() ) {
            "Debes elegir una imagen."
        } else {
            null
        }
    }

    fun validarIngredientes(ingredientes: List<Ingrediente>): String? {
        return if (ingredientes.isEmpty()) {
            "Debe haber minimo un ingrediente"

        } else {
            null
        }
        println("POR QUE VVRG" + ingredientes.isEmpty())
    }

    fun validarTXTNombre(preparacion: String): String? {
        return if (preparacion.isEmpty()) {
            "El nombre del ingrediente no puede estar vacío."
        } else {
            null
        }
    }

    fun validarTXTCantidad(preparacion: String, unidad: String): String? {
        return if (preparacion.isEmpty()) {
            "La cantidad del ingrediente no puede estar vacío."
        } else if (!preparacion.matches(Regex("\\d+"))) {
            "La cantidad debe de ser un número válido."
        } else if (unidad.isEmpty()) {
            "Debe seleccionar una unidad de medida."
        }  else {
            null
        }
    }
}
