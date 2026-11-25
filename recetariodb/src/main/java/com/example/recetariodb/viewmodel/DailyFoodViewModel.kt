package com.example.recetariodb.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.firestore
import com.example.recetariodb.model.DailyFood
import com.example.recetariodb.model.RecetasIniciales
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DailyFoodViewModel : ViewModel() {

    private val firestore = Firebase.firestore
    private var listener: ListenerRegistration? = null

    // Lista de recetas que observa la pantalla
    private val _dataDailyFood = MutableStateFlow<List<DailyFood>>(emptyList())
    val dataDailyFood: StateFlow<List<DailyFood>> = _dataDailyFood

    var mensajeError by mutableStateOf<String?>(null)
        private set

    var guardando by mutableStateOf(false)
        private set

    init {
        // Se registra UNA sola vez el listener (en FoodCal se registraba cada vez
        // que se volvía a la lista)
        if (firestore.app.options.projectId.orEmpty().startsWith("PEGA_AQUI")) {
            // Todavía no se llenó res/values/firebase_config.xml: se avisa en pantalla
            mensajeError = "Falta configurar Firebase: llena res/values/firebase_config.xml " +
                    "(ver GUIA_FIREBASE.md)"
        } else {
            fetchDaysFood()
        }
    }

    // LEER en tiempo real: cada cambio en la colección actualiza la lista sola
    private fun fetchDaysFood() {
        listener = firestore.collection("DayFood").orderBy("day")
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    mensajeError = "Error al leer Firestore: ${error.localizedMessage}"
                    return@addSnapshotListener
                }
                val documents = mutableListOf<DailyFood>()
                if (querySnapshot != null) {
                    for (document in querySnapshot) {
                        val myDocument = document.toObject(DailyFood::class.java)
                            .copy(idDoc = document.id)
                        documents.add(myDocument)
                    }
                }
                _dataDailyFood.value = documents
            }
    }

    // CREAR una receta nueva (Firestore le asigna un id automático)
    fun addRecipe(
        day: Int,
        title: String,
        description: String,
        imageRes: String,
        onSuccess: () -> Unit
    ) {
        if (day <= 0 || title.isBlank() || description.isBlank()) {
            mensajeError = "Completa el día, el título y la descripción."
            return
        }

        val receta = hashMapOf(
            "day" to day,
            "title" to title,
            "description" to description,
            "imageRes" to imageRes
        )

        guardando = true
        firestore.collection("DayFood")
            .add(receta)
            .addOnSuccessListener {
                guardando = false
                mensajeError = null
                onSuccess()
            }
            .addOnFailureListener { e ->
                guardando = false
                mensajeError = "Error al guardar en Firestore: ${e.localizedMessage}"
            }
    }

    // CREAR las 5 recetas iniciales con id fijo (dia1, dia2...).
    // set() crea el documento o lo reemplaza, así que no se duplican.
    fun cargarRecetasIniciales() {
        for (receta in RecetasIniciales.recetas) {
            val datos = hashMapOf(
                "day" to receta.day,
                "title" to receta.title,
                "description" to receta.description,
                "imageRes" to receta.imageRes
            )
            firestore.collection("DayFood").document("dia${receta.day}")
                .set(datos)
                .addOnFailureListener { e ->
                    mensajeError = "Error al cargar recetas: ${e.localizedMessage}"
                }
        }
    }

    // ELIMINAR una receta por su id
    fun deleteRecipe(idDoc: String) {
        firestore.collection("DayFood").document(idDoc)
            .delete()
            .addOnFailureListener { e ->
                mensajeError = "Error al eliminar: ${e.localizedMessage}"
            }
    }

    fun limpiarError() {
        mensajeError = null
    }

    override fun onCleared() {
        listener?.remove() // se deja de escuchar a Firestore al cerrar la app
        super.onCleared()
    }
}
