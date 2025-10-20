package com.example.misfinanzas.ui.theme

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.mis_finanzas.db.DatabaseHelper
import com.mis_finanzas.model.Categoria
import com.mis_finanzas.model.Gasto
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class RegistrarGastoActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etMonto: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etFecha: EditText
    private lateinit var spCategoria: Spinner
    private var selectedCategoria: Categoria? = null

    // Date format for UI (día/mes/año) [cite: 24]
    private val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_gasto) // Replace with your layout file name

        dbHelper = DatabaseHelper(this)
        etMonto = findViewById(R.id.etMonto) // Assuming ID etMonto
        etDescripcion = findViewById(R.id.etDescripcion) // Assuming ID etDescripcion
        etFecha = findViewById(R.id.etFecha) // Assuming ID etFecha
        spCategoria = findViewById(R.id.spCategoria) // Assuming ID spCategoria

        // Date Picker Setup [cite: 22]
        etFecha.setText(displayDateFormat.format(Date())) // Default to current date [cite: 25]
        etFecha.setOnClickListener { showDatePickerDialog() }

        loadCategoriasSpinner()

        findViewById<Button>(R.id.btnGuardarGasto).setOnClickListener {
            guardarGasto()
        }
    }

    // --- Category Dropdown Setup [cite: 26, 27] ---
    private fun loadCategoriasSpinner() {
        val categorias = DatabaseHelper.PREDEFINED_CATEGORIES

        // Adapter shows: ícono + nombre + límite mensual [cite: 28]
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categorias
        )
        spCategoria.adapter = adapter

        spCategoria.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedCategoria = parent.getItemAtPosition(position) as Categoria
            }
            override fun onNothingSelected(parent: AdapterView<*>) { /* Do nothing */ }
        }
    }

    // --- Date Picker Dialog [cite: 23] ---
    private fun showDatePickerDialog() {
        val c = Calendar.getInstance()
        try {
            c.time = displayDateFormat.parse(etFecha.text.toString()) ?: Date()
        } catch (e: ParseException) { /* If parsing fails, use current date */ }

        DatePickerDialog(this, { _, year, month, dayOfMonth ->
            val newDate = Calendar.getInstance()
            newDate.set(year, month, dayOfMonth)
            etFecha.setText(displayDateFormat.format(newDate.time)) // Format día/mes/año [cite: 24]
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
    }

    // --- Save Logic [cite: 9] ---
    private fun guardarGasto() {
        val montoStr = etMonto.text.toString()
        val descripcion = etDescripcion.text.toString().trim()
        val fecha: Date

        // 1. Validation: Monto > 0 [cite: 10, 11]
        val monto = montoStr.toDoubleOrNull()
        if (monto == null || monto <= 0) {
            showAlert("Error de Monto", "Verificar que el monto sea mayor a 0.")
            return
        }

        // 2. Validation: Categoría selected [cite: 10, 12]
        val categoria = selectedCategoria
        if (categoria == null) {
            showAlert("Error de Categoría", "Validar que se haya seleccionado una categoría.")
            return
        }

        try {
            fecha = displayDateFormat.parse(etFecha.text.toString()) ?: Date()
        } catch (e: ParseException) {
            showAlert("Error de Fecha", "El formato de la fecha no es válido.")
            return
        }

        // Action: Guardar gasto [cite: 14, 15]
        val nuevoGasto = Gasto(
            monto = monto,
            descripcion = descripcion,
            fecha = fecha,
            categoriaNombre = categoria.nombre
        )
        val newId = dbHelper.insertGasto(nuevoGasto)

        if (newId > 0) {
            // Show Success Snackbar [cite: 16]
            showSnackbar("✓ Gasto guardado correctamente", Color.parseColor("#4CAF50")) // Green color

            // Check Monthly Limit [cite: 19]
            checkLimiteMensual(categoria)

            // Clean fields [cite: 18]
            etMonto.setText("")
            etDescripcion.setText("")
            etFecha.setText(displayDateFormat.format(Date())) // Reset to current date
            spCategoria.setSelection(0) // Reset spinner

            // Navigate to Pantalla 2 [cite: 17]
            startActivity(Intent(this, MisGastosActivity::class.java))
            finish()
        } else {
            showSnackbar("X Error al guardar gasto", Color.RED)
        }
    }

    // --- Limit Check (Validación de Límite Mensual) [cite: 19, 20] ---
    private fun checkLimiteMensual(categoria: Categoria) {
        val totalMes = dbHelper.getTotalGastoCategoriaMesActual(categoria.nombre)

        if (totalMes > categoria.limiteMensual) {
            // Show Warning Snackbar [cite: 21]
            val warningMsg = "⚠ Has excedido el límite de ${categoria.nombre}: " +
                    "\$${String.format("%.2f", totalMes)} de " +
                    "\$${String.format("%.2f", categoria.limiteMensual)}"
            showSnackbar(warningMsg, Color.parseColor("#FF9800")) // Orange color
        }
    }

    // --- Utility Functions (AlertDialog and Snackbar) ---
    private fun showAlert(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private fun showSnackbar(message: String, color: Int) {
        // Use android.R.id.content as a view reference
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).apply {
            view.setBackgroundColor(color)
            show()
        }
    }
}