package com.appsmoviles.proyectomoviles.utilidades

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class VinculadorSensorLuz : AppCompatActivity() {
    private lateinit var manejadorSensorLuz: ManejadorSensorLuz

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        manejadorSensorLuz = ManejadorSensorLuz(this)
    }

    public override fun onResume() {
        super.onResume()
        manejadorSensorLuz.register()
    }

    public override fun onPause() {
        super.onPause()
        manejadorSensorLuz.unregister()
    }
}
