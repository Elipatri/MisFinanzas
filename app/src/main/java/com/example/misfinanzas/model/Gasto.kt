package com.example.misfinanzas.model

// data/model/Gasto.kt
data class Gasto(
    val id: Int? = null,
    val monto: Double,
    val descripcion: String?,
    val fecha: String, // Formato DD/MM/AAAA [cite: 24, 131, 162]
    val idCategoria: Int,
    // Datos de Categoría (para la lista de gastos)
    val nombreCategoria: String,
    val iconoResId: Int
)