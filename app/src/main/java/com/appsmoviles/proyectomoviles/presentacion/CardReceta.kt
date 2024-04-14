package com.appsmoviles.proyectomoviles.presentacion

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.appsmoviles.proyectomoviles.R

import com.appsmoviles.proyectomoviles.databinding.ActivityMainBinding
import com.appsmoviles.proyectomoviles.dominio.Receta
import kotlin.math.roundToInt


class CardReceta(private val context: Context, private val recetas: List<Receta>, private val binding: ActivityMainBinding) {


    fun crearCardsRecetas() {
        for (receta in recetas) {
            val card = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = 16.dp
                }
                setPadding(16.dp, 16.dp, 16.dp, 16.dp)
                setBackgroundResource(R.drawable.edittext_bg_rounded)
            }


            val textLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3f)
            }
            val titulo = TextView(context).apply {
                text = receta.titulo
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
                layoutParams = LinearLayout.LayoutParams(150.dp, 100.dp)

            }

            val imagen = ImageView(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.FIT_XY
                setImageDrawable(ContextCompat.getDrawable(context, R.drawable.imagen_receta))
            }

            val btnGuardar = ImageButton(context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    47.dp,
                    55.dp,
                    Gravity.TOP or Gravity.END
                ).apply {
                    leftMargin = 5.dp
                    topMargin = 5.dp
                }
                background = null
                scaleType = ImageView.ScaleType.FIT_XY
                setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_guardado_relleno))
                setOnClickListener {
                    //poner algo para guardar aquí
                }
            }

            frameLayoutContainer.addView(imagen)
            frameLayoutContainer.addView(btnGuardar)
            card.addView(textLayout)
            card.addView(frameLayoutContainer)

            binding.layoutContenedorCards.addView(card)
        }
    }

    fun Int.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics
        ).roundToInt()
    }

    val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

}