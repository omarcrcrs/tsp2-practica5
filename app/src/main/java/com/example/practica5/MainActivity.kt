package com.example.practica5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.practica5.ui.Practica5App
import com.example.practica5.ui.theme.Practica5Theme

/**
 * Práctica 5 — Servicios web (Equipo 2).
 * Continúa la app de alimentación de las prácticas 2 y 3, pero ahora la app es solo el
 * cliente: las operaciones (Actividad 1) y la base de datos de recetas (Actividad 2) viven
 * en nuestro propio servicio web (carpeta servicio_web/ del proyecto).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Practica5Theme {
                Practica5App()
            }
        }
    }
}
