package com.example.recetariows.model

// Misma estructura que usa la app del profe (FoodCal).
// Cada receta es un documento de la colección "DayFood" en Cloud Firestore.
data class DailyFood(
    val idDoc: String = "",       // id del documento en Firestore (no se guarda como campo)
    val day: Int = 0,             // número de día
    val title: String = "",       // nombre del platillo
    val description: String = "", // descripción / receta
    val imageRes: String = ""     // URL de la imagen
)
