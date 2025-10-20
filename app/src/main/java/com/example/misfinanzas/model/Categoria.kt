package com.example.misfinanzas.model

data class Categoria(
    val id: Int? = null,
    val nombre: String,
    // Usamos Int para referenciar ID de recursos de Drawable o String para emojis (depende de la implementación)
    val iconoResId: Int,
    val colorHex: String, // Usado para representación visual
    val limiteMensual: Double
)