package com.example.practica5.network

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Punto único de configuración del servicio web. A diferencia de TheMealDB (Práctica 2), la
 * dirección no es fija: depende de dónde corra servidor.py, así que se puede cambiar desde la
 * pestaña "Servidor".
 */
object ClienteServicioWeb {

    /** Desde el emulador, 10.0.2.2 es la computadora donde corre Android Studio. */
    const val URL_EMULADOR = "http://10.0.2.2:8080/"

    /** Teléfono por USB después de `adb reverse tcp:8080 tcp:8080`. */
    const val URL_USB = "http://127.0.0.1:8080/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(4, TimeUnit.SECONDS)   // el servidor está en la red local: si no contesta rápido, no está
        .readTimeout(8, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .build()

    /** Dirección activa, siempre con http:// al inicio y / al final. */
    var urlBase: String = URL_EMULADOR
        private set

    private var servicioActivo: ServicioWebApi? = null

    /** Servicio de la dirección activa; se crea la primera vez que se usa. */
    val api: ServicioWebApi
        get() = servicioActivo ?: crear(urlBase).also { servicioActivo = it }

    /** Crea un cliente para una dirección concreta (ya normalizada). */
    fun crear(url: String): ServicioWebApi =
        Retrofit.Builder()
            .baseUrl(url)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServicioWebApi::class.java)

    /** Cambia el servidor activo. Regresa false (y no cambia nada) si la dirección no es válida. */
    fun cambiarServidor(direccion: String): Boolean {
        val url = normalizar(direccion) ?: return false
        urlBase = url
        servicioActivo = null
        return true
    }

    /** "192.168.1.70:8080" -> "http://192.168.1.70:8080/". null si no se puede usar como URL. */
    fun normalizar(direccion: String): String? {
        var url = direccion.trim()
        if (url.isEmpty()) return null
        if (!url.startsWith("http://") && !url.startsWith("https://")) url = "http://$url"
        if (!url.endsWith("/")) url += "/"
        return if (url.toHttpUrlOrNull() != null) url else null
    }
}
