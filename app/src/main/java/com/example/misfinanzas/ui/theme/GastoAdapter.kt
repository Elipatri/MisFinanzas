package com.example.misfinanzas.ui.theme

// ui/adapters/GastoAdapter.kt
class GastoAdapter(private val gastos: List<Gasto>, private val onClick: (Gasto) -> Unit) :
    RecyclerView.Adapter<GastoAdapter.GastoViewHolder>() {

    class GastoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgIcono: ImageView = view.findViewById(R.id.img_icono)
        val tvDescripcion: TextView = view.findViewById(R.id.tv_descripcion)
        val tvFecha: TextView = view.findViewById(R.id.tv_fecha)
        val tvMonto: TextView = view.findViewById(R.id.tv_monto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gasto, parent, false)
        return GastoViewHolder(view)
    }

    override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {
        val gasto = gastos[position]
        val context = holder.itemView.context

        holder.imgIcono.setImageResource(gasto.iconoResId) // Ícono de la categoría

        // Descripción (o nombre de categoría si está vacío) [cite: 34, 161]
        holder.tvDescripcion.text = gasto.descripcion ?: gasto.nombreCategoria

        holder.tvFecha.text = gasto.fecha // Fecha en formato día/mes/año [cite: 35, 162]

        // Monto negativo en rojo: -$XX.XX [cite: 36, 163]
        holder.tvMonto.text = String.format(Locale.US, "-\$%.2f", gasto.monto)
        holder.tvMonto.setTextColor(context.getColor(android.R.color.holo_red_dark))

        holder.itemView.setOnClickListener { onClick(gasto) } // Manejar el clic para mostrar BottomSheet
    }

    override fun getItemCount() = gastos.size
}