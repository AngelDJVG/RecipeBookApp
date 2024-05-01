import android.content.Context
import android.text.TextUtils
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import com.appsmoviles.proyectomoviles.R
import com.appsmoviles.proyectomoviles.enums.TipoReceta

class ValidadorRecetas(private val activity: AppCompatActivity) {

    fun validateRecipeForm(
        tituloEditText: String,
        tipoRecetaSpinner: TipoReceta,
        preparacionEditText: String,
        tiempoPreparacionEditText: String,
        tiempoCocinadoEditText: String
    ): String {
        val errores = StringBuilder()

        val tituloError = validateTitle(tituloEditText)
        if (tituloError.isNotEmpty()) {
            errores.append(tituloError).append("\n")
        }

        val preparacionError = validatePreparation(preparacionEditText)
        if (preparacionError.isNotEmpty()) {
            errores.append(preparacionError).append("\n")
        }

        val tiempoPreparacionError = validateTime(tiempoPreparacionEditText)
        if (tiempoPreparacionError.isNotEmpty()) {
            errores.append(tiempoPreparacionError).append("\n")
        }

        val tiempoCocinadoError = validateTime(tiempoCocinadoEditText)
        if (tiempoCocinadoError.isNotEmpty()) {
            errores.append(tiempoCocinadoError).append("\n")
        }

        return errores.toString().trim()
    }

    private fun validateTitle(tituloEditText: String): String {
        val titulo = tituloEditText
        return if (titulo.isNotBlank()) {
            ""
        } else {
            activity.getString(R.string.error_title_required)
        }
    }

    private fun validatePreparation(preparacionEditText: String): String {
        val preparacion = preparacionEditText
        return if (preparacion.isNotBlank()) {
            ""
        } else {
            activity.getString(R.string.error_preparation_required)
        }
    }

    private fun validateTime(tiempoEditText: String): String {
        val tiempo = tiempoEditText
        return if (tiempo.isNotBlank()) {
            ""
        } else {
            activity.getString(R.string.error_preparation_time_required)
        }
    }
}
