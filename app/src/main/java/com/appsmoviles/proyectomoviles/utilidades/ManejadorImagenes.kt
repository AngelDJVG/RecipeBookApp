package com.appsmoviles.proyectomoviles.utilidades

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.appsmoviles.proyectomoviles.R
import com.bumptech.glide.Glide

class ManejadorImagenes(private val context: Context) {

    fun cargarImagenDesdeUri(uriString: String?, imageView: ImageView) {
        uriString?.let { uriString ->
            val uri = Uri.parse(uriString)
            try {
                Glide.with(context)
                    .load(uri)
                    .error(R.drawable.imagen_receta)
                    .into(imageView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}