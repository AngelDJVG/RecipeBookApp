package com.appsmoviles.proyectomoviles.utilidades

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.provider.Settings
import android.util.Log


class ManejadorSensorLuz(private val context: Context) : SensorEventListener {
    private val sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val lightSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    private val TAG = "ManejadorSensorLuz"

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val valorLuz = it.values[0]
            Log.d(TAG, "Valor de luz: $valorLuz")

            val brilloActual = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
            val maximoBrillo = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE)
            val valorMuchaLuz = 20
            val valorPocaLuz = 5

            if (valorLuz > valorMuchaLuz) {
                Log.d(TAG, "Subiendo brillo")
                Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC)
            }
            else if (valorLuz < valorPocaLuz) {
                Log.d(TAG, "Bajando brillo")
                Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL)
                Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 0) // Ajusta este valor según sea necesario
            } else {
                Log.d(TAG, "No hace nada")
            }
        }
    }

    fun register() {
        lightSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun unregister() {
        sensorManager.unregisterListener(this)
    }
}