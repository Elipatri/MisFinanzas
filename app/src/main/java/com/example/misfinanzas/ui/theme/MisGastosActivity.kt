// ui/gastos/MisGastosActivity.kt

// Paquetes estándar de Android
import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Html
import android.view.View // Necesario para layoutInflater y findViewById en showOptionsBottomSheet
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar // Necesario para showSnackbar
import java.util.Locale
import com.yourpackage.misfinanzas.R // Para acceder a IDs como R.id.rv_gastos, R.layout.activity_mis_gastos, etc.
import com.yourpackage.misfinanzas.data.db.DatabaseHelper // Para la capa de persistencia SQLite
import com.yourpackage.misfinanzas.data.model.Gasto // Para el modelo de datos
import com.yourpackage.misfinanzas.ui.adapters.GastoAdapter // Para el adaptador del RecyclerView
import com.yourpackage.misfinanzas.ui.registro.RegistrarGastoActivity // Para la navegación del botón FAB (+)

package com.example.misfinanzas.ui.theme



class MisGastosActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var rvGastos: RecyclerView
    private lateinit var tvTotalGeneral: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_gastos)

        dbHelper = DatabaseHelper(this)
        rvGastos = findViewById(R.id.rv_gastos)
        tvTotalGeneral = findViewById(R.id.tv_total_general)

        // Botón FAB (+), ubicado en la esquina superior derecha [cite: 46, 178]
        findViewById<FloatingActionButton>(R.id.fab_add_gasto).apply {
            // Color verde [cite: 47, 179]
            backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.green_cibertec, null))
            setOnClickListener {
                // Navegar a Pantalla 1 [cite: 48, 180]
                startActivity(Intent(this@MisGastosActivity, RegistrarGastoActivity::class.java))
            }
        }

        rvGastos.layoutManager = LinearLayoutManager(this)
    }

    override fun onResume() {
        super.onResume()
        loadExpenses() // Actualizar la lista al volver de Pantalla 1 o después de eliminar
    }

    private fun loadExpenses() {
        // Mostrar TODOS los gastos, Más recientes primero [cite: 31, 32, 157, 158]
        val gastoList = dbHelper.getAllExpenses()

        // Configurar RecyclerView
        rvGastos.adapter = GastoAdapter(gastoList) { gasto -> showOptionsBottomSheet(gasto) } // Maneja el clic en el ítem [cite: 36, 164]

        updateTotalGeneral()
    }

    private fun showOptionsBottomSheet(gasto: Gasto) {
        // Mostrar BottomSheet con opciones: 🗑 Eliminar y ✕ Cancelar [cite: 37, 38, 39, 165, 166, 167]
        val dialog = BottomSheetDialog(this)
        val sheetView = layoutInflater.inflate(R.layout.bottom_sheet_gasto_options, null)
        dialog.setContentView(sheetView)

        sheetView.findViewById<TextView>(R.id.option_eliminar).setOnClickListener {
            dialog.dismiss()
            showDeleteConfirmationDialog(gasto)
        }
        // ... (Listener para Cancelar)
        dialog.show()
    }

    private fun showDeleteConfirmationDialog(gasto: Gasto) {
        // Mostrar AlertDialog: "¿Eliminar este gasto de $XX.XX?" [cite: 41, 169]
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage("¿Eliminar este gasto de \$%.2f?".format(gasto.monto))
            .setPositiveButton("Eliminar") { _, _ ->
                deleteGasto(gasto)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deleteGasto(gasto: Gasto) {
        if (dbHelper.deleteGasto(gasto.id!!) > 0) {
            loadExpenses() // Actualizar automáticamente la lista y recalcular el total [cite: 43, 44, 52, 172, 174, 185]
            // Mostrar Snackbar: "🗑 Gasto eliminado" [cite: 45, 175, 176]
            showSnackbar("🗑 Gasto eliminado", R.color.gray_deleted)
        }
    }

    private fun updateTotalGeneral() {
        val total = dbHelper.getTotalGeneral()

        // Formato: Total: -$XXX.XX (negrita, rojo) [cite: 51, 184]
        val formattedTotal = String.format(Locale.US, "Total: <b><font color='#F44336'>-\$%.2f</font></b>", total) // #F44336 es Rojo
        tvTotalGeneral.text = Html.fromHtml(formattedTotal, Html.FROM_HTML_MODE_COMPACT)
    }

    private fun showSnackbar(message: String, colorResId: Int) { /* ... */ }
}