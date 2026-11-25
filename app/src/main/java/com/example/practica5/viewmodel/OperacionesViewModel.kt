package com.example.practica5.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica5.model.ResultadoOperacion
import com.example.practica5.network.ClienteServicioWeb
import com.example.practica5.network.RespuestaServicio
import com.example.practica5.network.ServicioWebApi
import com.example.practica5.network.llamarServicio
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Un solo estado posible a la vez para la pantalla de operaciones (Actividad 1). */
sealed interface OperacionUiState {
    data object Inicial : OperacionUiState
    data object Cargando : OperacionUiState
    data class Exito(val resultado: ResultadoOperacion) : OperacionUiState
    data class Error(val mensaje: String) : OperacionUiState
}

/**
 * ViewModel del servicio web simple. Aquí NO se hace ninguna cuenta: solo se revisa que lo
 * escrito sean números, se le pide la operación al servicio y se publica lo que conteste.
 * Los rangos (negativos, porciones en cero, unidades) los valida el servicio.
 */
class OperacionesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<OperacionUiState>(OperacionUiState.Inicial)
    val uiState: StateFlow<OperacionUiState> = _uiState.asStateFlow()

    fun calcularCalorias(gramos: String, kcalPor100g: String) {
        val peso = gramos.aNumero()
        val kcal = kcalPor100g.aNumero()
        if (peso == null || kcal == null) {
            avisarDatosIncompletos("Escribe con números el peso y las kcal por cada 100 g")
            return
        }
        pedir { servicio -> servicio.calorias(peso, kcal) }
    }

    fun escalarPorciones(cantidad: String, porcionesBase: String, porcionesDeseadas: String) {
        val original = cantidad.aNumero()
        val base = porcionesBase.aNumero()
        val deseadas = porcionesDeseadas.aNumero()
        if (original == null || base == null || deseadas == null) {
            avisarDatosIncompletos("Escribe con números la cantidad y las dos porciones")
            return
        }
        pedir { servicio -> servicio.escalarPorciones(original, base, deseadas) }
    }

    fun convertirPeso(valor: String, de: String, a: String) {
        val peso = valor.aNumero()
        if (peso == null) {
            avisarDatosIncompletos("Escribe con números el peso que quieres convertir")
            return
        }
        pedir { servicio -> servicio.convertirPeso(peso, de, a) }
    }

    fun convertirTemperatura(valor: String, de: String, a: String) {
        val temperatura = valor.aNumero()
        if (temperatura == null) {
            avisarDatosIncompletos("Escribe con números la temperatura que quieres convertir")
            return
        }
        pedir { servicio -> servicio.convertirTemperatura(temperatura, de, a) }
    }

    /** Al cambiar de operación se borra el resultado anterior. */
    fun limpiar() {
        _uiState.value = OperacionUiState.Inicial
    }

    private fun avisarDatosIncompletos(mensaje: String) {
        _uiState.value = OperacionUiState.Error(mensaje)
    }

    private fun pedir(peticion: suspend (ServicioWebApi) -> ResultadoOperacion) {
        viewModelScope.launch {
            _uiState.value = OperacionUiState.Cargando
            _uiState.value = when (val respuesta = llamarServicio { peticion(ClienteServicioWeb.api) }) {
                is RespuestaServicio.Correcta -> OperacionUiState.Exito(respuesta.datos)
                is RespuestaServicio.Fallida -> OperacionUiState.Error(respuesta.mensaje)
            }
        }
    }
}

/** "12,5" o "12.5" -> 12.5 · vacío o con letras -> null */
private fun String.aNumero(): Double? = trim().replace(',', '.').toDoubleOrNull()
