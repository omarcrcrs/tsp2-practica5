package com.example.recetariows.network

import com.example.recetariows.model.DailyFood
import com.example.recetariows.model.RecetasIniciales

// Esta clase ENCAPSULA el servicio web: la app solo llama a
// listarRecetas(), guardarReceta() y eliminarReceta() con objetos DailyFood,
// sin saber nada de URLs, verbos HTTP ni del formato JSON de Firestore.
class RecetaService {

    private val api = FirestoreClient.apiService

    // Operación 1: consultar todas las recetas, ordenadas por día
    suspend fun listarRecetas(): List<DailyFood> {
        val respuesta = api.listarDocumentos(PROJECT_ID)
        val documentos = respuesta.documents ?: emptyList() // colección vacía → lista vacía
        return documentos
            .map { documento -> convertirADailyFood(documento) }
            .sortedBy { receta -> receta.day }
    }

    // Operación 2: guardar una receta (si ya existe con ese id, se reemplaza)
    suspend fun guardarReceta(receta: DailyFood) {
        val idDoc = "dia${receta.day}"
        api.guardarDocumento(PROJECT_ID, idDoc, convertirADocumento(receta))
    }

    // Operación 3: eliminar una receta por su id
    suspend fun eliminarReceta(idDoc: String) {
        val respuesta = api.eliminarDocumento(PROJECT_ID, idDoc)
        if (!respuesta.isSuccessful) {
            throw Exception("No se pudo eliminar (código ${respuesta.code()})")
        }
    }

    // Operación 4: subir las 5 recetas iniciales
    suspend fun cargarRecetasIniciales() {
        for (receta in RecetasIniciales.recetas) {
            guardarReceta(receta)
        }
    }

    // ---------- Conversión entre el JSON de Firestore y DailyFood ----------

    private fun convertirADailyFood(documento: FirestoreDocument): DailyFood {
        val campos = documento.fields ?: emptyMap()
        return DailyFood(
            // "projects/.../documents/DayFood/dia1" → "dia1"
            idDoc = documento.name?.substringAfterLast("/") ?: "",
            day = campos["day"]?.integerValue?.toIntOrNull()
                ?: campos["day"]?.doubleValue?.toInt()
                ?: 0,
            title = campos["title"]?.stringValue ?: "",
            description = campos["description"]?.stringValue ?: "",
            imageRes = campos["imageRes"]?.stringValue ?: ""
        )
    }

    private fun convertirADocumento(receta: DailyFood): FirestoreDocument {
        return FirestoreDocument(
            fields = mapOf(
                "day" to FirestoreValue(integerValue = receta.day.toString()),
                "title" to FirestoreValue(stringValue = receta.title),
                "description" to FirestoreValue(stringValue = receta.description),
                "imageRes" to FirestoreValue(stringValue = receta.imageRes)
            )
        )
    }
}
