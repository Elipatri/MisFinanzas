package com.example.misfinanzas.data
// data/db/DatabaseHelper.kt
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "MisFinanzasDB", null, 1) {

    // Nombres de tablas y columnas
    companion object {
        private const val TABLE_CATEGORIA = "Categoria"
        private const val TABLE_GASTO = "Gasto"
        // ... (Constantes de columnas: CAT_ID, CAT_NOMBRE, GAS_MONTO, GAS_FECHA, etc.)
        // Asumiendo que tenemos los ID de drawable mock:
        val MOCK_ICON = mapOf("Alimentación" to R.drawable.ic_food, "Transporte" to R.drawable.ic_bus)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear tabla Categoria
        db.execSQL("CREATE TABLE $TABLE_CATEGORIA (id_categoria INTEGER PRIMARY KEY, nombre TEXT, iconoResId INTEGER, colorHex TEXT, limiteMensual REAL)")
        // Crear tabla Gasto
        db.execSQL("CREATE TABLE $TABLE_GASTO (id_gasto INTEGER PRIMARY KEY, monto REAL, descripcion TEXT, fecha TEXT, id_categoria INTEGER, FOREIGN KEY(id_categoria) REFERENCES $TABLE_CATEGORIA(id_categoria))")

        // Insertar Categorías Predefinidas
        insertCategory(db, "Alimentación", MOCK_ICON["Alimentación"]!!, "#4CAF50", 800.00) // Verde
        insertCategory(db, "Transporte", MOCK_ICON["Transporte"]!!, "#2196F3", 300.00) // Azul
        insertCategory(db, "Entretenimiento", R.drawable.ic_entertainment, "#9C27B0", 200.00) // Púrpura
        insertCategory(db, "Vivienda", R.drawable.ic_home, "#F44336", 1500.00) // Rojo
        insertCategory(db, "Salud", R.drawable.ic_health, "#F44336", 400.00) // Rojo
        insertCategory(db, "Café/Bebidas", R.drawable.ic_coffee, "#795548", 150.00) // Café
        insertCategory(db, "Compras", R.drawable.ic_cart, "#FF9800", 500.00) // Naranja
        insertCategory(db, "Otros", R.drawable.ic_box, "#9E9E9E", 300.00) // Gris
    }

    private fun insertCategory(db: SQLiteDatabase, nombre: String, icon: Int, color: String, limite: Double) {
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("iconoResId", icon)
            put("colorHex", color)
            put("limiteMensual", limite)
        }
        db.insert(TABLE_CATEGORIA, null, values)
    }

    // --- Pantalla 1: Registro ---

    fun getAllCategories(): List<Categoria> { /* ... */ }
    fun getCategoryName(id: Int): String { /* ... */ return "Nombre Cat" }
    fun getCategoryLimit(id: Int): Double { /* ... */ return 0.0 }

    fun insertGasto(monto: Double, descripcion: String?, fecha: String, idCategoria: Int): Long {
        // Guardar el gasto en SQLite [cite: 15, 119]
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("monto", monto)
            put("descripcion", descripcion)
            put("fecha", fecha)
            put("id_categoria", idCategoria)
        }
        return db.insert(TABLE_GASTO, null, values)
    }

    fun getTotalMonthlyExpense(categoryId: Int, monthYear: String): Double {
        // Calcular el total del mes actual de esa categoría [cite: 20, 125]
        val db = this.readableDatabase
        // SUBSTR(fecha, 4) extrae 'MM/AAAA' de 'DD/MM/AAAA'
        val query = "SELECT SUM(monto) FROM $TABLE_GASTO WHERE id_categoria = ? AND SUBSTR(fecha, 4) = ?"
        val cursor = db.rawQuery(query, arrayOf(categoryId.toString(), monthYear))
        var total = 0.0
        if (cursor.moveToFirst()) total = cursor.getDouble(0)
        cursor.close()
        return total
    }

    // --- Pantalla 2: Consulta ---

    fun getAllExpenses(): List<Gasto> {
        // Consulta con JOIN a Categoria, ordenada por fecha DESC [cite: 31, 32, 157, 158]
        // Se requiere una conversión manual de la fecha (DD/MM/AAAA) a un formato comparable para el ordenamiento
        return emptyList() /* ... */
    }

    fun deleteGasto(id: Int): Int {
        // Eliminar el gasto de la base de datos [cite: 42, 171]
        val db = this.writableDatabase
        return db.delete(TABLE_GASTO, "id_gasto = ?", arrayOf(id.toString()))
    }

    fun getTotalGeneral(): Double {
        // Sumar TODOS los gastos [cite: 50, 183]
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT SUM(monto) FROM $TABLE_GASTO", null)
        var total = 0.0
        if (cursor.moveToFirst()) total = cursor.getDouble(0)
        cursor.close()
        return total
    }
}
