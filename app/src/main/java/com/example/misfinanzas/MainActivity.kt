package com.mis_finanzas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import com.mis_finanzas.db.DatabaseHelper
import com.mis_finanzas.ui.theme.MisFinanzasTheme
import com.mis_finanzas.ui.screens.MisGastosScreen // Pantalla 2
import com.mis_finanzas.viewmodel.GastoViewModel
import com.mis_finanzas.viewmodel.GastoViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Inicializar DatabaseHelper
        val dbHelper = DatabaseHelper(applicationContext)

        // 2. Crear el Factory para inyectar la dependencia del Helper/Repository al ViewModel
        // Se asume que GastoViewModelFactory usa dbHelper para crear el GastoRepository.
        val factory = GastoViewModelFactory(dbHelper)

        // 3. Obtener el GastoViewModel
        val viewModel = ViewModelProvider(this, factory)[GastoViewModel::class.java]

        setContent {
            MisFinanzasTheme { // Usa el tema de tu aplicación
                // Surface (similar al contenedor principal)
                Surface(color = Color(0xFF1C1E20)) { // Color de fondo oscuro
                    // Cargar la pantalla principal (Pantalla 2: Mis Gastos)
                    MisGastosScreen(viewModel)
                }
            }
        }
    }
}