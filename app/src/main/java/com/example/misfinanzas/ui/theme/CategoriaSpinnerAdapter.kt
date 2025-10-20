package com.example.misfinanzas.ui.theme


class CategoriaSpinnerAdapter(context: Context, private val categorias: List<Categoria>) :
    ArrayAdapter<Categoria>(context, 0, categorias) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Inflar layout customizado para el ítem de categoría
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_categoria_dropdown, parent, false)
        val categoria = getItem(position)!!

        // Asignación de datos a vistas (asumiendo IDs en item_categoria_dropdown.xml)
        val icono = view.findViewById<ImageView>(R.id.img_icono_categoria)
        val nombre = view.findViewById<TextView>(R.id.tv_nombre_categoria)
        val limite = view.findViewById<TextView>(R.id.tv_limite_mensual)

        icono.setImageResource(categoria.iconoResId) // Ícono
        nombre.text = categoria.nombre // Nombre
        limite.text = "$%.2f".format(categoria.limiteMensual) // Límite Mensual

        return view
    }
}