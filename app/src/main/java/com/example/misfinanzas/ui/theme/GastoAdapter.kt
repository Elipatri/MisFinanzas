package com.example.misfinanzas.ui.theme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mis_finanzas.db.DatabaseHelper
import com.mis_finanzas.model.Gasto
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Color

class GastoAdapter(
    private var gastosList: List<Gasto>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<GastoAdapter.GastoViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(gasto: Gasto)
    }

    // Date format for UI (día/mes/año) [cite: 35]
    private val displayDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    // A helper function to find the category details (icon)
    private fun findCategoryIcon(categoryName: String): String {
        return DatabaseHelper.PREDEFINED_CATEGORIES.firstOrNull { it.nombre == categoryName }?.icono ?: "❓" [cite: 33]
    }

    // --- ViewHolder ---
    inner class GastoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Replace with your actual IDs in item_gasto.xml
        val tvIcono: TextView = itemView.findViewById(R.id.tvIcono)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFecha)
        val tvMonto: TextView = itemView.findViewById(R.id.tvMonto)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(gastosList[adapterPosition]) // Set click listener [cite: 36]
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_gasto, parent, false) // Replace with your list item layout
        return GastoViewHolder(view)
    }

    override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {
        val gasto = gastosList[position]

        holder.tvIcono.text = findCategoryIcon(gasto.categoriaNombre) [cite: 33]

        // Description or Category name if empty [cite: 34]
        val mainText = if (gasto.descripcion.isEmpty()) gasto.categoriaNombre else gasto.descripcion
        holder.tvDescripcion.text = mainText

        holder.tvFecha.text = displayDateFormat.format(gasto.fecha) [cite: 35]

        // Monto negativo en rojo: -$XX.XX [cite: 36]
        val montoStr = String.format(Locale.getDefault(), "-$%.2f", gasto.monto)
        holder.tvMonto.text = montoStr
        holder.tvMonto.setTextColor(Color.RED)
    }

    override fun getItemCount(): Int = gastosList.size

    /**
     * Updates the list and notifies the RecyclerView[cite: 43].
     */
    fun updateList(newList: List<Gasto>) {
        gastosList = newList
        notifyDataSetChanged()
    }
}