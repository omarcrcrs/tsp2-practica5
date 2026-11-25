package com.example.practica5.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica5.model.Receta
import com.example.practica5.network.ClienteServicioWeb
import com.example.practica5.network.RespuestaServicio
import com.example.practica5.network.llamarServicio
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Estado de la lista de recetas que entrega el servicio (Actividad 2). */
sealed interface RecetarioUiState {
    data object Cargando : RecetarioUiState
    data class Exito(val recetas: List<Receta>) : RecetarioUiState
    data class Error(val mensaje: String) : RecetarioUiState
}

/**
 * ViewModel del recetario. En la Práctica 3 este ViewModel hablaba con el DAO de Room; ahora
 * la base de datos está en el servidor y cada operación es una petición HTTP:
 *   consultar -> GET · agregar -> POST · cambiar -> PUT · eliminar -> DELETE
 * Después de cada cambio se vuelve a pedir la lista: lo que se ve siempre es lo que hay en la BD.
 * `avisar` es la función que muestra un mensaje corto en pantalla (la da la vista).
 */
class RecetarioViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<RecetarioUiState>(RecetarioUiState.Cargando)
    val uiState: StateFlow<RecetarioUiState> = _uiState.asStateFlow()

    /** Texto del buscador; el filtro lo aplica el servidor con un LIKE. */
    var busqueda by mutableStateOf("")
        private set

    /** Registro consultado por su id; null = no hay ninguna consulta abierta. */
    var recetaConsultada by mutableStateOf<Receta?>(null)
        private set

    private var trabajoDeCarga: Job? = null

    /** GET /api/recetas — si ya había una carga en camino se cancela para que gane la más nueva. */
    fun cargar(mostrarCarga: Boolean = true) {
        trabajoDeCarga?.cancel()
        trabajoDeCarga = viewModelScope.launch {
            if (mostrarCarga) _uiState.value = RecetarioUiState.Cargando
            val filtro = busqueda.trim().ifBlank { null }
            _uiState.value = when (val respuesta = llamarServicio { ClienteServicioWeb.api.listarRecetas(filtro) }) {
                is RespuestaServicio.Correcta -> RecetarioUiState.Exito(respuesta.datos)
                is RespuestaServicio.Fallida -> RecetarioUiState.Error(respuesta.mensaje)
            }
        }
    }

    fun buscar(texto: String) {
        busqueda = texto
        cargar(mostrarCarga = false)
    }

    /** GET /api/recetas/{id} — consulta de un solo registro. */
    fun consultar(id: Int, avisar: (String) -> Unit) {
        viewModelScope.launch {
            when (val respuesta = llamarServicio { ClienteServicioWeb.api.obtenerReceta(id) }) {
                is RespuestaServicio.Correcta -> recetaConsultada = respuesta.datos
                is RespuestaServicio.Fallida -> avisar(respuesta.mensaje)
            }
        }
    }

    fun cerrarConsulta() {
        recetaConsultada = null
    }

    /** POST /api/recetas — el servidor asigna el id y la fecha, y regresa el registro ya guardado. */
    fun agregar(receta: Receta, avisar: (String) -> Unit) {
        viewModelScope.launch {
            when (val respuesta = llamarServicio { ClienteServicioWeb.api.crearReceta(receta) }) {
                is RespuestaServicio.Correcta -> {
                    avisar("Se guardó «${respuesta.datos.nombre}» con el id ${respuesta.datos.id}")
                    cargar(mostrarCarga = false)
                }
                is RespuestaServicio.Fallida -> avisar(respuesta.mensaje)
            }
        }
    }

    /** PUT /api/recetas/{id} — regresa el registro como quedó en la base de datos. */
    fun actualizar(receta: Receta, avisar: (String) -> Unit, mensaje: String = "Cambios guardados") {
        viewModelScope.launch {
            when (val respuesta = llamarServicio { ClienteServicioWeb.api.actualizarReceta(receta.id, receta) }) {
                is RespuestaServicio.Correcta -> {
                    // si es el registro que está abierto en la consulta, se refresca también
                    if (recetaConsultada?.id == respuesta.datos.id) recetaConsultada = respuesta.datos
                    avisar(mensaje)
                    cargar(mostrarCarga = false)
                }
                is RespuestaServicio.Fallida -> avisar(respuesta.mensaje)
            }
        }
    }

    // Cambios rápidos: todos terminan en el mismo PUT
    fun calificar(receta: Receta, estrellas: Float, avisar: (String) -> Unit) =
        actualizar(receta.copy(calificacion = estrellas), avisar, "Calificación: ${estrellas.toInt()} de 5")

    fun alternarFavorita(receta: Receta, avisar: (String) -> Unit) =
        actualizar(
            receta.copy(favorita = !receta.favorita),
            avisar,
            if (receta.favorita) "Se quitó de favoritas" else "Se marcó como favorita"
        )

    fun registrarCocinada(receta: Receta, avisar: (String) -> Unit) =
        actualizar(receta.copy(vecesCocinada = receta.vecesCocinada + 1), avisar, "¡Buen provecho! Se anotó una vez más")

    /** DELETE /api/recetas/{id} */
    fun eliminar(receta: Receta, avisar: (String) -> Unit) {
        viewModelScope.launch {
            when (val respuesta = llamarServicio { ClienteServicioWeb.api.eliminarReceta(receta.id) }) {
                is RespuestaServicio.Correcta -> {
                    if (recetaConsultada?.id == receta.id) recetaConsultada = null
                    avisar(respuesta.datos.mensaje ?: "Receta eliminada")
                    cargar(mostrarCarga = false)
                }
                is RespuestaServicio.Fallida -> avisar(respuesta.mensaje)
            }
        }
    }
}
