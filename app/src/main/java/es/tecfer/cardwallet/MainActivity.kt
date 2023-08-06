package es.tecfer.cardwallet

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.SeekBar
import androidx.annotation.RequiresApi

class MainActivity : Activity() {

    private lateinit var brightnessSeekBar: SeekBar

    private val REQUEST_CODE_WRITE_SETTINGS = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        brightnessSeekBar = findViewById(R.id.brightnessSeekBar)
        brightnessSeekBar.max = 255


        // Configurar el listener para la SeekBar
        brightnessSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // Cambiar el brillo de la pantalla al mover la barra de desplazamiento
                setBrightness(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        // Verificar y solicitar el permiso en tiempo de ejecución si es necesario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.System.canWrite(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS)
        }
    }

    // Método para ajustar el brillo
    private fun setBrightness(brightnessValue: Int) {
        // Asegúrate de que el valor del brillo esté en el rango [0, 255]
        val brightness = Math.max(0, Math.min(brightnessValue, 255))

        // Actualiza el brillo en el sistema
        val cResolver: ContentResolver = contentResolver
        Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness)

        // Actualiza el brillo en tiempo real
        val layoutParams = window.attributes
        layoutParams.screenBrightness = brightness / 255.0f
        window.attributes = layoutParams

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (Settings.System.canWrite(this)) {
                // El permiso ha sido otorgado, ahora puedes cambiar el brillo
                // Si la barra ya se encuentra en un valor específico, ajustar el brillo en función de ese valor
                val currentBrightness = brightnessSeekBar.progress
                setBrightness(currentBrightness)
            } else {
                // El permiso no fue otorgado, puedes mostrar un mensaje o tomar alguna acción
            }
        }
    }
}
