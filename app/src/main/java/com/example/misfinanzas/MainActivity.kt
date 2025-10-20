package com.example.misfinanzas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

// Asumiendo que esta es la actividad de lanzamiento (LAUNCHER) definida en el AndroidManifest.
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // La aplicación debe mostrar la Pantalla 2 (Mis Gastos) como punto de entrada.

        // 1. Crear el Intent para navegar a MisGastosActivity.
        val intent = Intent(this, MisGastosActivity::class.java)

        // 2. Iniciar la actividad de destino.
        startActivity(intent)

        // 3. Finalizar MainActivity. Esto evita que el usuario pueda volver a una pantalla de inicio vacía
        //    al presionar el botón 'Atrás' desde MisGastosActivity.
        finish()

        // Nota: No es necesario llamar a setContentView() ya que la actividad se finaliza inmediatamente.
    }
}