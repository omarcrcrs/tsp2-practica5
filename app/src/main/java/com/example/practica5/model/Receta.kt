package com.example.practica5.model

/**
 * Un registro de la tabla `recetas` tal como viaja en el JSON del servicio web (Actividad 2).
 * Los nombres de las propiedades son los mismos que las llaves del JSON, por eso Gson los
 * convierte solo. Es la misma información que guardaba Room en la Práctica 3, pero ahora la
 * base de datos vive en el servidor.
 * Tipos distintos: Int, String, Float, Boolean y Long. Los valores por defecto sirven para
 * crear una receta nueva (id = 0: el servidor asigna la llave primaria).
 */
data class Receta(
    val id: Int = 0,
    val nombre: String = "",
    val categoria: String = "",
    val area: String = "",              // cocina de origen
    val imagenUrl: String = "",
    val ingredientes: String = "",      // un ingrediente por línea
    val instrucciones: String = "",
    val calificacion: Float = 0f,       // 0 a 5 estrellas
    val favorita: Boolean = false,
    val vecesCocinada: Int = 0,
    val notas: String = "",
    val fechaGuardado: Long = 0L        // milisegundos; la pone el servidor al guardar
)
