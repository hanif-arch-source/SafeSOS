package com.example.safesos.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * Listens to the accelerometer and fires [onShake] when the device is shaken
 * hard enough. This is the sensor requirement for the IBL23305 rubric: it
 * must actually influence app behaviour, not just be displayed — here it
 * navigates straight into the Medical SOS form (see MainActivity).
 */
class ShakeDetector(
    context: Context,
    private val onShake: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastShakeTimeMs = 0L

    companion object {
        // G-force threshold a shake must cross to trigger. Tuned so normal
        // walking/pocket movement doesn't false-trigger, but a deliberate
        // shake does.
        private const val SHAKE_THRESHOLD_G = 2.7f
        private const val DEBOUNCE_MS = 1500L
    }

    /** True if this device actually has an accelerometer to listen to. */
    val isAvailable: Boolean get() = accelerometer != null

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val gX = x / SensorManager.GRAVITY_EARTH
        val gY = y / SensorManager.GRAVITY_EARTH
        val gZ = z / SensorManager.GRAVITY_EARTH
        val gForce = sqrt(gX * gX + gY * gY + gZ * gZ)

        if (gForce > SHAKE_THRESHOLD_G) {
            val now = System.currentTimeMillis()
            if (now - lastShakeTimeMs > DEBOUNCE_MS) {
                lastShakeTimeMs = now
                onShake()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for shake detection.
    }
}
