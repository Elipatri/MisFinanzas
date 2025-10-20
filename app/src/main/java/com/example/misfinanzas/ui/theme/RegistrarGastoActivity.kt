package com.example.misfinanzas.ui.theme

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast // Reemplazar con Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class RegistrarGastoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var selectedCategoryId: Int? = null
    private var selectedCategoryLimit: Double = 0.0
    private lateinit var tietMonto: TextInputEditText
    private lateinit var tietFecha: TextInputEditText
    private lateinit var tietDescripcion: TextInputEditText
    private lateinit var categorySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_gasto)

        dbHelper = DatabaseHelper(this)

        tietMonto = findViewById(R.id.tiet_monto)
        tietFecha = findViewById(R.id.tiet_fecha)
        tietDescripcion = findViewById(R.id.tiet_descripcion) // La descripción es opcional [cite: 13, 118]
        categorySpinner = findViewById(R.id.spinner_categoria)

        setupCategorySpinner()
        setupDateField()

        findViewById<Button>(R.id.btn_guardar_gasto).setOnClickListener { saveGasto() }
    }

    private fun setupDateField() {
        // Por defecto: fecha actual [cite: 25, 132]
        tietFecha.setText(SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()))
        tietFecha.setOnClickListener { showDatePicker() } // Mostrar DatePickerDialog [cite: 23, 130]
        tietFecha.keyListener = null // Deshabilita el teclado
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        DatePickerDialog(this, { _, y, m, d ->
            // Formato: día/mes/año [cite: 24, 131]
            val date = String.format("%02d/%02d/%d", d, m + 1, y)
            tietFecha.setText(date)
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun setupCategorySpinner() {
        val categories = dbHelper.getAllCategories() // Obtiene todas las categorías [cite: 134]
        val adapter = CategoriaSpinnerAdapter(this, categories)
        categorySpinner.adapter = adapter

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = parent.getItemAtPosition(position) as Categoria
                selectedCategoryId = selectedCategory.id
                selectedCategoryLimit = selectedCategory.limiteMensual
            }
            override fun onNothingSelected(parent: AdapterView<*>) { /* ... */ }
        }
    }

    private fun saveGasto() {
        val monto = tietMonto.text.toString().toDoubleOrNull()
        val fecha = tietFecha.text.toString()

        // 1. Validación: Monto > 0 (usar AlertDialog) [cite: 11, 116]
        if (monto == null || monto <= 0) {
            showAlertDialog("Error de Monto", "Verificar que el monto sea mayor a $0.00.")
            return
        }

        // 2. Validación: Categoría seleccionada (usar AlertDialog) [cite: 12, 117]
        if (selectedCategoryId == null) {
            showAlertDialog("Error de Categoría", "Validar que se haya seleccionado una categoría.")
            return
        }

        // 3. Guardar y Validar Límite
        val descripcion = tietDescripcion.text.toString().trim().ifEmpty { null }
        val newGastoId = dbHelper.insertGasto(monto, descripcion, fecha, selectedCategoryId!!)

        if (newGastoId > 0) {
            // Validar Límite Mensual [cite: 19, 124]
            checkMonthlyLimit(selectedCategoryId!!, monto, fecha, selectedCategoryLimit)

            // Mostrar Snackbar de ÉXITO (color verde) [cite: 16, 120, 121]
            showSnackbar("✓ Gasto guardado correctamente", R.color.green_success)

            // Limpiar los campos [cite: 18, 123]
            clearFields()

            // Navegar automáticamente a la Pantalla 2 [cite: 17, 122]
            startActivity(Intent(this, MisGastosActivity::class.java))
            finish()
        }
    }

    private fun checkMonthlyLimit(id: Int, nuevoMonto: Double, fecha: String, limite: Double) {
        val monthYear = fecha.substring(3) // Extraer MM/AAAA
        val totalMes = dbHelper.getTotalMonthlyExpense(id, monthYear)

        if (totalMes + nuevoMonto > limite) {
            // Mostrar Snackbar de ADVERTENCIA (color naranja) [cite: 21, 126, 127]
            val catNombre = dbHelper.getCategoryName(id)
            val msg = "⚠ Has excedido el límite de [$catNombre]: \$%.2f de \$%.2f".format(totalMes + nuevoMonto, limite)
            showSnackbar(msg, R.color.orange_warning)
        }
    }

    // Funciones helper: showAlertDialog, showSnackbar, clearFields
    private fun showAlertDialog(title: String, message: String) { /* ... */ }
    private fun showSnackbar(message: String, colorResId: Int) { /* ... */ }
    private fun clearFields() { /* ... */ }
}