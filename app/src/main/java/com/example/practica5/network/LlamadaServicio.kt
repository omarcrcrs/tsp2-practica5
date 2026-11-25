package com.example.practica5.network

import com.example.practica5.model.MensajeServicio
import com.google.gson.Gson
import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.io.IOException

/** Resultado de una petición al servicio: o llegaron los datos, o hay un mensaje que mostrar. */
sealed interface RespuestaServicio<out T> {
    data class Correcta<T>(val datos: T) : RespuestaServicio<T>
    data class Fallida(val mensaje: String) : RespuestaServicio<Nothing>
}

/**
 * Hace la petición y traduce las fallas a un mensaje para la IU, en un solo lugar para los
 * tres ViewModel. Las fallas posibles al hablar con un servicio web son:
 *  - IOException: no hubo conexión (servidor apagado, dirección equivocada, sin red).
 *  - HttpException: sí contestó, pero con error (400 dato inválido, 404 no existe…).
 *  - JsonParseException: contestó algo que no es el JSON que esperamos.
 */
suspend fun <T> llamarServicio(peticion: suspend () -> T): RespuestaServicio<T> =
    try {
        RespuestaServicio.Correcta(peticion())
    } catch (e: IOException) {
        RespuestaServicio.Fallida(
            "No se pudo conectar con el servicio web. Revisa que el servidor esté encendido " +
                "y la dirección en la pestaña Servidor."
        )
    } catch (e: HttpException) {
        RespuestaServicio.Fallida(e.mensajeDelServicio())
    } catch (e: JsonParseException) {
        RespuestaServicio.Fallida("El servidor contestó algo que no es el JSON esperado. ¿Es la dirección correcta?")
    }

/** Nuestro servicio explica cada error en {"error": "..."}; si no viene, se muestra el código HTTP. */
private fun HttpException.mensajeDelServicio(): String {
    val explicacion = try {
        val cuerpo = response()?.errorBody()?.string()
        Gson().fromJson(cuerpo, MensajeServicio::class.java)?.error
    } catch (e: IOException) {
        null
    } catch (e: JsonParseException) {
        null
    }
    return explicacion ?: "El servicio respondió con un error (código ${code()})."
}
