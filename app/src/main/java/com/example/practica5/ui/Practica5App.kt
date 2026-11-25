@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.practica5.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.practica5.ui.views.OperacionesView
import com.example.practica5.ui.views.RecetarioView
import com.example.practica5.ui.views.ServidorView
import com.example.practica5.viewmodel.OperacionesViewModel
import com.example.practica5.viewmodel.RecetarioViewModel
import com.example.practica5.viewmodel.ServidorViewModel
import kotlinx.coroutines.launch

/** Las tres pestañas de la app, en el orden en que aparecen en la barra inferior. */
private enum class Pestana(val titulo: String, val icono: ImageVector) {
    OPERACIONES("Operaciones", Icons.Default.Build),   // Actividad 1
    RECETARIO("Recetario", Icons.Default.Favorite),    // Actividad 2
    SERVIDOR("Servidor", Icons.Default.Settings)       // conexión con el servicio web
}

/**
 * Armazón de la app: barra superior, pestañas y avisos. Un ViewModel por tarea:
 * la conexión, las operaciones (Actividad 1) y el recetario (Actividad 2).
 */
@Composable
fun Practica5App(
    servidorViewModel: ServidorViewModel = viewModel(),
    operacionesViewModel: OperacionesViewModel = viewModel(),
    recetarioViewModel: RecetarioViewModel = viewModel()
) {
    var pestana by rememberSaveable { mutableIntStateOf(0) }

    // Avisos cortos (Snackbar). Si llega uno nuevo, se quita el anterior para que no se encimen.
    val snackbarHostState = remember { SnackbarHostState() }
    val alcance = rememberCoroutineScope()
    val avisar: (String) -> Unit = { texto ->
        alcance.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(texto)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Cocina en red", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Práctica 5 · Servicios web · Equipo 2",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                Pestana.entries.forEachIndexed { indice, opcion ->
                    NavigationBarItem(
                        selected = pestana == indice,
                        onClick = { pestana = indice },
                        icon = { Icon(opcion.icono, contentDescription = null) },
                        label = { Text(opcion.titulo) }
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when (Pestana.entries[pestana]) {
                Pestana.OPERACIONES -> OperacionesView(viewModel = operacionesViewModel)
                Pestana.RECETARIO -> RecetarioView(
                    viewModel = recetarioViewModel,
                    urlServicio = servidorViewModel.urlActiva,
                    avisar = avisar
                )
                Pestana.SERVIDOR -> ServidorView(viewModel = servidorViewModel)
            }
        }
    }
}
