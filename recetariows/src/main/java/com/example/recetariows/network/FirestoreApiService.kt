package com.example.recetariows.network

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

// ⚠️ CAMBIA ESTO: el "ID del proyecto" de tu proyecto de Firebase
// (Consola de Firebase → engrane ⚙ → Configuración del proyecto → ID del proyecto)
const val PROJECT_ID = "PEGA_AQUI_TU_PROJECT_ID"

// ---------- Forma del JSON que usa la API REST de Firestore ----------
// Ejemplo de un documento:
// {
//   "name": "projects/mi-proyecto/databases/(default)/documents/DayFood/dia1",
//   "fields": {
//     "day":   { "integerValue": "1" },
//     "title": { "stringValue": "Tacos" }
//   }
// }

data class FirestoreValue(
    val stringValue: String? = null,
    val integerValue: String? = null,
    val doubleValue: Double? = null
)

data class FirestoreDocument(
    val name: String? = null,
    val fields: Map<String, FirestoreValue>? = null
)

data class FirestoreListResponse(
    val documents: List<FirestoreDocument>? = null
)

// ---------- Métodos del servicio web (uno por operación HTTP) ----------
interface FirestoreApiService {

    // GET: consultar todos los documentos de la colección
    @GET("projects/{projectId}/databases/(default)/documents/DayFood")
    suspend fun listarDocumentos(
        @Path("projectId") projectId: String
    ): FirestoreListResponse

    // PATCH: guardar (crear o reemplazar) un documento con un id conocido
    @PATCH("projects/{projectId}/databases/(default)/documents/DayFood/{idDoc}")
    suspend fun guardarDocumento(
        @Path("projectId") projectId: String,
        @Path("idDoc") idDoc: String,
        @Body documento: FirestoreDocument
    ): FirestoreDocument

    // DELETE: eliminar un documento
    @DELETE("projects/{projectId}/databases/(default)/documents/DayFood/{idDoc}")
    suspend fun eliminarDocumento(
        @Path("projectId") projectId: String,
        @Path("idDoc") idDoc: String
    ): Response<Unit>
}

// ---------- Cliente Retrofit (igual que ImgBbClient en la app del profe) ----------
object FirestoreClient {
    private const val BASE_URL = "https://firestore.googleapis.com/v1/"

    val apiService: FirestoreApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FirestoreApiService::class.java)
    }
}
