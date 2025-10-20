package com.example.misfinanzas.viewmodel

package com.mis_finanzas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mis_finanzas.db.DatabaseHelper
import com.mis_finanzas.model.Gasto
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GastoViewModel(private val dbHelper: DatabaseHelper) : ViewModel() {

    // --- StateFlows (Datos expuestos a la UI) ---
    private val _gastos = MutableStateFlow<List<Gasto>>(emptyList())
    val gastos: StateFlow<List<Gasto>> = _gastos.asStateFlow()

    private val _totalGeneral = MutableStateFlow(0.0)
    val totalGeneral: StateFlow<Double> = _totalGeneral.asStateFlow()

    // Para manejar el estado de eliminación (ej: BottomSheet/AlertDialog)
    private val _gastoToDelete = MutableStateFlow<Gasto?>(null)
    val gastoToDelete: StateFlow<Gasto?> = _gastoToDelete.asStateFlow()

    init {
        loadGastos()
    }

    // --- Funciones de Lógica de Negocio ---

    private fun loadGastos() {
        viewModelScope.launch {
            // Reemplazar con una función que devuelva Flow si usa Room.
            // Con SQLiteOpenHelper, se debe llamar en un hilo de background.
            val list = dbHelper.getAllGastos()
            _gastos.value = list
            recalculateTotal()
        }
    }

    private fun recalculateTotal() {
        viewModelScope.launch {
            val total = dbHelper.getTotalGeneralGastos() // Sumar TODOS los gastos [cite: 50]
            _totalGeneral.value = total
        }
    }

    fun insertGasto(gasto: Gasto) {
        viewModelScope.launch {
            val id = dbHelper.insertGasto(gasto)
            if (id > 0) {
                // Recargar datos y recalcular
                loadGastos()
                // Verificar Límite Mensual (Lógica de Pantalla 1)
                checkLimiteMensual(gasto)
            }
        }
    }

    private fun checkLimiteMensual(gasto: Gasto) {
        // En Compose, esto debería emitir un evento a la UI para mostrar el Snackbar
        // Aquí solo se llama a la DB para la validación [cite: 20]
        val totalMes = dbHelper.getTotalGastoCategoriaMesActual(gasto.categoriaNombre)
        val categoria = DatabaseHelper.PREDEFINED_CATEGORIES.first { it.nombre == gasto.categoriaNombre }

        if (totalMes > categoria.limiteMensual) {
            // TODO: Emitir un evento de UI para mostrar Snackbar de ADVERTENCIA [cite: 21]
        }
    }

    fun setCurrentGastoToDelete(gasto: Gasto) {
        _gastoToDelete.value = gasto // Al hacer clic en un item [cite: 36, 37]
    }

    fun clearGastoToDelete() {
        _gastoToDelete.value = null
    }

    fun deleteConfirmed(gasto: Gasto) {
        viewModelScope.launch {
            val rows = dbHelper.deleteGasto(gasto.id)
            if (rows > 0) {
                // Eliminar el gasto de la base de datos [cite: 42]
                loadGastos() // Actualizar automáticamente la lista y el total [cite: 43, 44]
                // TODO: Emitir evento de UI para mostrar Snackbar: "🗑 Gasto eliminado" [cite: 45]
            }
        }
    }
}

// --- Factory ---

class GastoViewModelFactory(private val dbHelper: DatabaseHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GastoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GastoViewModel(dbHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}