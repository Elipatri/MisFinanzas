package com.example.misfinanzas.ui.theme

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.mis_finanzas.db.DatabaseHelper
import com.mis_finanzas.model.Gasto
import com.mis_finanzas.ui.adapter.GastoAdapter
import java.util.*

class MisGastosActivity : AppCompatActivity(), GastoAdapter.OnItemClickListener {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GastoAdapter
    private lateinit var tvTotalGeneral: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_gastos) // Replace with your layout file name

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerViewGastos) // Assuming ID
        tvTotalGeneral = findViewById(R.id.tvTotalGeneral) // Assuming ID [cite: 49]

        setupRecyclerView()

        // FAB Setup [cite: 46, 47]
        findViewById<FloatingActionButton>(R.id.fabAddGasto).setOnClickListener {
            // Navigate to Pantalla 1 [cite: 48]
            startActivity(Intent(this, RegistrarGastoActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadGastos() // Reload data when returning from RegistrarGastoActivity [cite: 43]
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = GastoAdapter(emptyList(), this)
        recyclerView.adapter = adapter
    }

    // --- Data Loading and Total Calculation ---
    private fun loadGastos() {
        val gastos = dbHelper.getAllGastos() // Load all expenses [cite: 31]
        adapter.updateList(gastos) // Update list [cite: 43]
        recalculateTotal() // Recalculate total [cite: 44]
    }

    private fun recalculateTotal() {
        val total = dbHelper.getTotalGeneralGastos() // Sum ALL expenses [cite: 50]

        // Format: Total: -$XXX.XX (negrita, rojo) [cite: 51]
        val totalStr = String.format(Locale.getDefault(), "Total: -$%.2f", total)

        // Using Html for bold text (or SpannableString)
        @Suppress("DEPRECATION")
        tvTotalGeneral.text = Html.fromHtml("<b>$totalStr</b>")
        tvTotalGeneral.setTextColor(Color.RED)
    }

    // --- Item Click (Deletion Logic) [cite: 36] ---
    override fun onItemClick(gasto: Gasto) {
        // In a real app, you'd show a BottomSheet here [cite: 37]
        showDeleteConfirmationDialog(gasto)
    }

    private fun showDeleteConfirmationDialog(gasto: Gasto) {
        // Show AlertDialog: "¿Eliminar este gasto de $XX.XX?" [cite: 41]
        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage(String.format(Locale.getDefault(), "¿Eliminar este gasto de $%.2f?", gasto.monto))
            .setPositiveButton("Sí, Eliminar") { _, _ -> // Si confirma [cite: 41]
                deleteGasto(gasto)
            }
            .setNegativeButton("Cancelar", null) // ✕ Cancelar [cite: 39]
            .show()
    }

    private fun deleteGasto(gasto: Gasto) {
        val rowsAffected = dbHelper.deleteGasto(gasto.id) [cite: 42]

        if (rowsAffected > 0) {
            loadGastos() // Actualizar lista y total [cite: 43, 44]

            // Show Snackbar: "🗑 Gasto eliminado" [cite: 45]
            Snackbar.make(recyclerView, "🗑 Gasto eliminado", Snackbar.LENGTH_SHORT).show()
        } else {
            Snackbar.make(recyclerView, "Error al eliminar gasto", Snackbar.LENGTH_SHORT).show()
        }
    }
}