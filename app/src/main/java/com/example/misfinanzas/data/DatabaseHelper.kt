package com.example.misfinanzas.data
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.mis_finanzas.model.Categoria
import com.mis_finanzas.model.Gasto
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MisFinanzasDB"
        private const val DATABASE_VERSION = 1

        // Table and Columns
        private const val TABLE_GASTOS = "gastos"
        private const val COLUMN_ID = "id"
        private const val COLUMN_MONTO = "monto"
        private const val COLUMN_DESCRIPCION = "descripcion"
        private const val COLUMN_FECHA = "fecha" // Stored as TEXT (yyyy-MM-dd)
        private const val COLUMN_CATEGORIA_NOMBRE = "categoria_nombre"

        // Date format for storing in SQLite
        private val SQLITE_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Predefined categories [cite: 5, 6]
        val PREDEFINED_CATEGORIES = listOf(
            Categoria("Alimentación", "🍽", "Verde", 800.00),
            Categoria("Transporte", "🚌", "Azul", 300.00),
            Categoria("Entretenimiento", "🎬", "Púrpura", 200.00),
            Categoria("Vivienda", "🏠", "Rojo", 1500.00),
            Categoria("Salud", "💊", "Rojo", 400.00),
            Categoria("Café/Bebidas", "☕", "Café", 150.00),
            Categoria("Compras", "🛒", "Naranja", 500.00),
            Categoria("Otros", "📦", "Gris", 300.00)
        )
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_GASTOS (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_MONTO REAL," +
                "$COLUMN_DESCRIPCION TEXT," +
                "$COLUMN_FECHA TEXT," +
                "$COLUMN_CATEGORIA_NOMBRE TEXT)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GASTOS")
        onCreate(db)
    }

    // --- CRUD Operations ---

    /**
     * Saves a new expense[cite: 15].
     * @return The ID of the new row or -1 if failed.
     */
    fun insertGasto(gasto: Gasto): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MONTO, gasto.monto)
            put(COLUMN_DESCRIPCION, gasto.descripcion)
            put(COLUMN_FECHA, SQLITE_DATE_FORMAT.format(gasto.fecha)) // Date to String
            put(COLUMN_CATEGORIA_NOMBRE, gasto.categoriaNombre)
        }
        val id = db.insert(TABLE_GASTOS, null, values)
        db.close()
        return id
    }

    /**
     * Deletes an expense[cite: 42].
     * @return The number of rows affected.
     */
    fun deleteGasto(gastoId: Int): Int {
        val db = this.writableDatabase
        val rows = db.delete(
            TABLE_GASTOS,
            "$COLUMN_ID = ?",
            arrayOf(gastoId.toString())
        )
        db.close()
        return rows
    }

    /**
     * Fetches all expenses, ordered by date (most recent first)[cite: 31, 32].
     */
    fun getAllGastos(): List<Gasto> {
        val gastosList = mutableListOf<Gasto>()
        // ORDER BY $COLUMN_FECHA DESC for most recent first [cite: 32]
        val selectQuery = "SELECT * FROM $TABLE_GASTOS ORDER BY $COLUMN_FECHA DESC"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                try {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                    val monto = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_MONTO))
                    val descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION))
                    val fechaStr = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA))
                    val categoriaNombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIA_NOMBRE))
                    val fecha = SQLITE_DATE_FORMAT.parse(fechaStr) ?: Date()

                    gastosList.add(Gasto(id, monto, descripcion, fecha, categoriaNombre))
                } catch (e: Exception) {
                    Log.e("DBHelper", "Error parsing Gasto: ${e.message}")
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return gastosList
    }

    /**
     * Calculates the total expenses for a category in the current month[cite: 20].
     */
    fun getTotalGastoCategoriaMesActual(categoriaNombre: String): Double {
        val db = this.readableDatabase
        val currentMonthYear = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

        val selectQuery = "SELECT SUM($COLUMN_MONTO) FROM $TABLE_GASTOS " +
                "WHERE $COLUMN_CATEGORIA_NOMBRE = ? AND STRFTIME('%Y-%m', $COLUMN_FECHA) = ?" // SQLite func

        val cursor = db.rawQuery(selectQuery, arrayOf(categoriaNombre, currentMonthYear))

        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        db.close()
        return total
    }

    /**
     * Calculates the total of ALL expenses[cite: 50].
     */
    fun getTotalGeneralGastos(): Double {
        val db = this.readableDatabase
        val selectQuery = "SELECT SUM($COLUMN_MONTO) FROM $TABLE_GASTOS"
        val cursor = db.rawQuery(selectQuery, null)

        var total = 0.0
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }
        cursor.close()
        db.close()
        return total
    }
}
