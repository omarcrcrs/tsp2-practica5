package com.example.recetariows.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recetariows.model.DailyFood
import com.example.recetariows.network.PROJECT_ID
import com.example.recetariows.network.RecetaService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

// Estados de cada llamada al servicio web
sealed interface EstadoServicio {
    object Inactivo : EstadoServicio
    object Cargando : EstadoServicio
    data class Exito(val mensaje: String) : EstadoServicio
    data class Error(val mensaje: String) : EstadoServicio
}

class RecetaViewModel : ViewModel() {

    private val servicio = RecetaService()

    // Lista de recetas que observa la pantalla (igual que en FoodCal)
    private val _dataDailyFood = MutableStateFlow<List<DailyFood>>(emptyList())
    val dataDailyFood: StateFlow<List<DailyFood>> = _dataDailyFood

    var estado by mutableStateOf<EstadoServicio>(EstadoServicio.Inactivo)
        private set

    init {
        consultarRecetas() // al abrir la app se consulta el servicio una vez
    }

    // GET
    fun consultarRecetas() {
        llamarServicio("Recetas consultadas") {
            _dataDailyFood.value = servicio.listarRecetas()
        }
    }

    // PATCH x5 y luego GET
    fun cargarRecetas() {
        llamarServicio("Se cargaron las 5 recetas") {
            servicio.cargarRecetasIniciales()
            _dataDailyFood.value = servicio.listarRecetas()
        }
    }

    // DELETE y luego GET
    fun eliminarReceta(idDoc: String) {
        llamarServicio("Receta eliminada") {
            servicio.eliminarReceta(idDoc)
            _dataDailyFood.value = servicio.listarRecetas()
        }
    }

    // Ejecuta una operación del servicio y actualiza el estado (Cargando → Éxito / Error)
    private fun llamarServicio(mensajeExito: String, operacion: suspend () -> Unit) {
        if (PROJECT_ID.startsWith("PEGA_AQUI")) {
            estado = EstadoServicio.Error("Falta poner tu PROJECT_ID en FirestoreApiService.kt")
            return
        }
        estado = EstadoServicio.Cargando
        viewModelScope.launch {
            try {
                operacion()
                estado = EstadoServicio.Exito(mensajeExito)
            } catch (e: HttpException) {
                estado = EstadoServicio.Error(
                    when (e.code()) {
                        403 -> "Permiso denegado (403): revisa las reglas de Firestore"
                        404 -> "No encontrado (404): revisa el PROJECT_ID y que creaste la base de datos"
                        else -> "Error del servicio: código ${e.code()}"
                    }
                )
            } catch (e: Exception) {
                estado = EstadoServicio.Error("Sin conexión o error: ${e.localizedMessage}")
            }
        }
    }
}
