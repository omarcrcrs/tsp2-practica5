package com.example.practica5.model

/** Respuesta de cualquier operación del servicio simple (Actividad 1). */
data class ResultadoOperacion(
    val operacion: String = "",
    val resultado: Double = 0.0,
    val unidad: String = "",
    val detalle: String = ""    // la cuenta escrita paso a paso, la arma el servidor
)

/** Respuesta de GET /api/estado: sirve para saber si la app sí alcanza al servicio. */
data class EstadoServicio(
    val servicio: String = "",
    val version: String = "",
    val recetas: Int = 0,
    val horaServidor: String = ""
)

/**
 * Mensajes cortos del servicio: `mensaje` cuando algo salió bien (por ejemplo al eliminar)
 * y `error` cuando contesta con un código 4xx.
 */
data class MensajeServicio(
    val mensaje: String? = null,
    val error: String? = null
)
