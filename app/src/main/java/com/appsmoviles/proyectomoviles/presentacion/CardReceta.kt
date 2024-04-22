package com.appsmoviles.proyectomoviles.presentacion

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.appsmoviles.proyectomoviles.MainActivity
import com.appsmoviles.proyectomoviles.R
import com.appsmoviles.proyectomoviles.VerDetallesRecetaActivity
import com.appsmoviles.proyectomoviles.VerGuardadosActivity
import com.appsmoviles.proyectomoviles.databinding.ActivityMainBinding
import com.appsmoviles.proyectomoviles.databinding.VerGuardadosBinding
import com.appsmoviles.proyectomoviles.dominio.Receta
import com.appsmoviles.proyectomoviles.utilidades.RecetaGuardadaListener
import com.appsmoviles.proyectomoviles.utilidades.RecetaManejador
import kotlin.math.roundToInt

class CardReceta(
    private val context: Context,
    private var recetas: List<Receta>,
    private val bindings: List<BindingWrapper> = emptyList(),
    private val listener: RecetaGuardadaListener? = null,


    ) {
    private val recetaManejador = RecetaManejador(context)
    data class BindingWrapper(val mainBinding: ActivityMainBinding? = null, val verGuardadosBinding: VerGuardadosBinding? = null)
    // Interfaz para escuchar cambios en las recetas

    fun crearCardsRecetas() {
        val binding = getAvailableBinding()
        if (binding != null) {
            val layoutContenedorCards = when (binding) {
                is ActivityMainBinding -> binding.layoutContenedorCards
                is VerGuardadosBinding -> binding.layoutContenedorCards
                else -> null
            }

            if (layoutContenedorCards != null) {
                layoutContenedorCards.removeAllViews()
                for (receta in recetas) {
                    val card = LinearLayout(context).apply {
                        orientation = LinearLayout.HORIZONTAL
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            bottomMargin = 16.dpToPx(context)
                        }
                        setPadding(
                            16.dpToPx(context),
                            16.dpToPx(context),
                            16.dpToPx(context),
                            16.dpToPx(context)
                        )
                        setBackgroundResource(R.drawable.edittext_bg_rounded)
                    }

                    val textLayout = LinearLayout(context).apply {
                        orientation = LinearLayout.VERTICAL
                        layoutParams =
                            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3f)
                        setPadding(0, 0, 16, 0)
                    }
                    val titulo = TextView(context).apply {
                        text = receta.titulo
                        setTypeface(null, Typeface.BOLD)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }
                    val descripcion = TextView(context).apply {
                        text = receta.preparacion
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    }
                    textLayout.addView(titulo)
                    textLayout.addView(descripcion)

                    val frameLayoutContainer = FrameLayout(context).apply {
                        layoutParams =
                            LinearLayout.LayoutParams(150.dpToPx(context), 100.dpToPx(context))
                    }

                    val imagen = ImageView(context).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                        scaleType = ImageView.ScaleType.FIT_XY
                        setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.imagen_receta
                            )
                        )
                    }

                    val btnGuardar = ImageButton(context).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            52.dpToPx(context),
                            55.dpToPx(context),
                            Gravity.TOP or Gravity.END
                        ).apply {
                            leftMargin = 5.dpToPx(context)
                            topMargin = 5.dpToPx(context)
                        }
                        background = null
                        scaleType = ImageView.ScaleType.FIT_XY
                        setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                if (recetaManejador.isRecetaGuardada(receta)) R.drawable.ic_guardado_relleno_seleccionado else R.drawable.ic_guardado_relleno
                            )
                        )
                        setOnClickListener {
                            toggleGuardarReceta(receta)

                            when (context) {
                                is VerGuardadosActivity -> {
                                    val recetasMutable = recetas.toMutableList()
                                    recetasMutable.remove(receta)
                                    recetas = recetasMutable.toList()
                                }

                            }
                            crearCardsRecetas()
                        }
                    }

                    frameLayoutContainer.addView(imagen)
                    frameLayoutContainer.addView(btnGuardar)
                    card.addView(textLayout)
                    card.addView(frameLayoutContainer)

                    layoutContenedorCards.addView(card)

                    card.setOnClickListener{
                        val intent = Intent(context, VerDetallesRecetaActivity::class.java)

                        intent.putExtra("receta", receta)

                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    private fun toggleGuardarReceta(receta: Receta) {
        recetaManejador.toggleGuardarReceta(receta)
        listener?.cambioGuardado()
    }

    private fun Int.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics
        ).roundToInt()
    }

    private fun getAvailableBinding(): ViewBinding? {
        bindings.forEach { wrapper ->
            if (wrapper.mainBinding != null && context is MainActivity) {
                return wrapper.mainBinding
            } else if (wrapper.verGuardadosBinding != null && context is VerGuardadosActivity) {
                return wrapper.verGuardadosBinding
            }
        }
        return null
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(context.applicationContext, mensaje, Toast.LENGTH_SHORT).show()
    }

}


