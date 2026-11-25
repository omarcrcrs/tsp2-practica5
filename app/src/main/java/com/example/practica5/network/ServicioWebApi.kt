package com.example.practica5.network

import com.example.practica5.model.EstadoServicio
import com.example.practica5.model.MensajeServicio
import com.example.practica5.model.Receta
import com.example.practica5.model.ResultadoOperacion
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Las rutas de nuestro servicio web (servicio_web/servidor.py), una función por ruta.
 * Al ser suspend, Retrofit las ejecuta fuera del hilo principal.
 */
interface ServicioWebApi {

    // GET /api/estado
    @GET("api/estado")
    suspend fun estado(): EstadoServicio

    // ---------- Actividad 1: servicio web simple ----------

    // GET /api/operaciones/calorias?gramos=150&kcal100=165
    @GET("api/operaciones/calorias")
    suspend fun calorias(
        @Query("gramos") gramos: Double,
        @Query("kcal100") kcalPor100g: Double
    ): ResultadoOperacion

    // GET /api/operaciones/escalar?cantidad=2&porcionesBase=4&porcionesDeseadas=6
    @GET("api/operaciones/escalar")
    suspend fun escalarPorciones(
        @Query("cantidad") cantidad: Double,
        @Query("porcionesBase") porcionesBase: Double,
        @Query("porcionesDeseadas") porcionesDeseadas: Double
    ): ResultadoOperacion

    // GET /api/operaciones/peso?valor=1&de=lb&a=g
    @GET("api/operaciones/peso")
    suspend fun convertirPeso(
        @Query("valor") valor: Double,
        @Query("de") de: String,
        @Query("a") a: String
    ): ResultadoOperacion

    // GET /api/operaciones/temperatura?valor=175&de=C&a=F
    @GET("api/operaciones/temperatura")
    suspend fun convertirTemperatura(
        @Query("valor") valor: Double,
        @Query("de") de: String,
        @Query("a") a: String
    ): ResultadoOperacion

    // ---------- Actividad 2: servicio web con base de datos ----------

    // GET /api/recetas  (con ?buscar=texto filtra; si es null Retrofit no manda el parámetro)
    @GET("api/recetas")
    suspend fun listarRecetas(@Query("buscar") buscar: String? = null): List<Receta>

    // GET /api/recetas/3
    @GET("api/recetas/{id}")
    suspend fun obtenerReceta(@Path("id") id: Int): Receta

    // POST /api/recetas  (la receta va como JSON en el cuerpo)
    @POST("api/recetas")
    suspend fun crearReceta(@Body receta: Receta): Receta

    // PUT /api/recetas/3
    @PUT("api/recetas/{id}")
    suspend fun actualizarReceta(@Path("id") id: Int, @Body receta: Receta): Receta

    // DELETE /api/recetas/3
    @DELETE("api/recetas/{id}")
    suspend fun eliminarReceta(@Path("id") id: Int): MensajeServicio
}
