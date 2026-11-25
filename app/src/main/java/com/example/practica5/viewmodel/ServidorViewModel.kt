package com.example.practica5.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica5.model.EstadoServicio
import com.example.practica5.network.ClienteServicioWeb
import com.example.practica5.network.RespuestaServicio
import com.example.practica5.network.llamarServicio
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** ¿La app alcanza al servicio web? */
sealed interface ConexionUiState {
    data object Probando : ConexionUiState
    data class Conectado(val estado: EstadoServicio) : ConexionUiState
    data class SinConexion(val mensaje: String) : ConexionUiState
}

/**
 * ViewModel de la conexión: en qué dirección está el servicio y si contesta.
 * Es AndroidViewModel porque guarda la dirección en SharedPreferences (necesita el contexto
 * de la aplicación) para no volver a escribirla cada vez que se abre la app.
 */
class ServidorViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencias = application.getSharedPreferences("servidor", Context.MODE_PRIVATE)

    /** Dirección que usa toda la app. Cuando cambia, el recetario vuelve a pedir sus datos. */
    var urlActiva by mutableStateOf(ClienteServicioWeb.urlBase)
        private set

    var conexion by mutableStateOf<ConexionUiState>(ConexionUiState.Probando)
        private set

    private var trabajoDePrueba: Job? = null

    init {
        val guardada = preferencias.getString(CLAVE_URL, null)
        if (guardada != null && ClienteServicioWeb.cambiarServidor(guardada)) {
            urlActiva = ClienteServicioWeb.urlBase
            probar()
        } else {
            detectar()   // primera vez: buscar el servicio en las direcciones típicas
        }
    }

    /** GET /api/estado en la dirección activa. */
    fun probar() {
        trabajoDePrueba?.cancel()
        trabajoDePrueba = viewModelScope.launch {
            conexion = ConexionUiState.Probando
            conexion = when (val respuesta = llamarServicio { ClienteServicioWeb.api.estado() }) {
                is RespuestaServicio.Correcta -> ConexionUiState.Conectado(respuesta.datos)
                is RespuestaServicio.Fallida -> ConexionUiState.SinConexion(respuesta.mensaje)
            }
        }
    }

    /** Cambia la dirección del servicio, la guarda y la prueba. */
    fun usar(direccion: String) {
        if (!ClienteServicioWeb.cambiarServidor(direccion)) {
            trabajoDePrueba?.cancel()
            conexion = ConexionUiState.SinConexion("Esa dirección no es válida. Ejemplo: http://192.168.1.70:8080")
            return
        }
        urlActiva = ClienteServicioWeb.urlBase
        preferencias.edit().putString(CLAVE_URL, urlActiva).apply()
        probar()
    }

    /** Prueba las dos direcciones típicas (teléfono por USB y emulador) y se queda con la que conteste. */
    fun detectar() {
        trabajoDePrueba?.cancel()
        trabajoDePrueba = viewModelScope.launch {
            conexion = ConexionUiState.Probando
            for (candidata in listOf(ClienteServicioWeb.URL_USB, ClienteServicioWeb.URL_EMULADOR)) {
                val respuesta = llamarServicio { ClienteServicioWeb.crear(candidata).estado() }
                if (respuesta is RespuestaServicio.Correcta) {
                    ClienteServicioWeb.cambiarServidor(candidata)
                    urlActiva = ClienteServicioWeb.urlBase
                    preferencias.edit().putString(CLAVE_URL, urlActiva).apply()
                    conexion = ConexionUiState.Conectado(respuesta.datos)
                    return@launch
                }
            }
            conexion = ConexionUiState.SinConexion(
                "No se encontró el servicio. Enciende servidor.py y, si usas el teléfono por WiFi, " +
                    "escribe la dirección que muestra el servidor al arrancar."
            )
        }
    }

    private companion object {
        const val CLAVE_URL = "url_base"
    }
}
