package com.example.practica5.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.practica5.model.Receta
import com.example.practica5.ui.components.DetalleRecetaDialog
import com.example.practica5.ui.components.FormularioRecetaDialog
import com.example.practica5.ui.components.IndicadorCargando
import com.example.practica5.ui.components.MensajeCentrado
import com.example.practica5.ui.components.TarjetaReceta
import com.example.practica5.viewmodel.RecetarioUiState
import com.example.practica5.viewmodel.RecetarioViewModel

/**
 * Actividad 2: el recetario de la Práctica 3, pero con la base de datos en el servidor.
 * La vista nunca arma peticiones: le pide cada operación al ViewModel.
 * [urlServicio] es la dirección activa; [avisar] muestra un mensaje corto (Snackbar).
 */
@Composable
fun RecetarioView(
    viewModel: RecetarioViewModel,
    urlServicio: String,
    avisar: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Se pide la lista al entrar a la pestaña y cada vez que cambia la dirección del servicio
    LaunchedEffect(urlServicio) { viewModel.cargar() }

    var mostrarAlta by rememberSaveable { mutableStateOf(false) }
    var recetaEnEdicion by remember { mutableStateOf<Receta?>(null) }
    var recetaPorEliminar by remember { mutableStateOf<Receta?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            OutlinedTextField(
                value = viewModel.busqueda,
                onValueChange = { viewModel.buscar(it) },
                label = { Text("Buscar por nombre, categoría u origen") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { viewModel.cargar() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Volver a pedir la lista")
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (val estado = uiState) {
                    is RecetarioUiState.Cargando -> IndicadorCargando(texto = "Pidiendo las recetas al servicio…")

                    is RecetarioUiState.Error -> Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MensajeCentrado(
                            emoji = "🔌",
                            titulo = "El servicio no contestó",
                            detalle = estado.mensaje,
                            esError = true,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { viewModel.cargar() },
                            modifier = Modifier.padding(bottom = 96.dp)
                        ) { Text("Reintentar") }
                    }

                    is RecetarioUiState.Exito -> {
                        if (estado.recetas.isEmpty()) {
                            MensajeCentrado(
                                emoji = "🍽️",
                                titulo = "No hay recetas",
                                detalle = if (viewModel.busqueda.isBlank()) {
                                    "La tabla está vacía. Agrega una con el botón +."
                                } else {
                                    "Ningún registro coincide con \"${viewModel.busqueda}\"."
                                }
                            )
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(estado.recetas, key = { it.id }) { receta ->
                                    TarjetaReceta(
                                        receta = receta,
                                        onClick = { viewModel.consultar(receta.id, avisar) },
                                        onAlternarFavorita = { viewModel.alternarFavorita(receta, avisar) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { mostrarAlta = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar receta")
        }
    }

    // Consulta de un registro (lo que contestó GET /api/recetas/{id})
    viewModel.recetaConsultada?.let { receta ->
        DetalleRecetaDialog(
            receta = receta,
            onCerrar = { viewModel.cerrarConsulta() },
            onCalificar = { estrellas -> viewModel.calificar(receta, estrellas, avisar) },
            onAlternarFavorita = { viewModel.alternarFavorita(receta, avisar) },
            onRegistrarCocinada = { viewModel.registrarCocinada(receta, avisar) },
            onEditar = {
                recetaEnEdicion = receta
                viewModel.cerrarConsulta()
            },
            onEliminar = {
                recetaPorEliminar = receta
                viewModel.cerrarConsulta()
            }
        )
    }

    // Alta (POST)
    if (mostrarAlta) {
        FormularioRecetaDialog(
            recetaInicial = null,
            onGuardar = { nueva ->
                viewModel.agregar(nueva, avisar)
                mostrarAlta = false
            },
            onCancelar = { mostrarAlta = false }
        )
    }

    // Cambio (PUT)
    recetaEnEdicion?.let { receta ->
        FormularioRecetaDialog(
            recetaInicial = receta,
            onGuardar = { editada ->
                viewModel.actualizar(editada, avisar)
                recetaEnEdicion = null
            },
            onCancelar = { recetaEnEdicion = null }
        )
    }

    // Baja (DELETE), con confirmación
    recetaPorEliminar?.let { receta ->
        AlertDialog(
            onDismissRequest = { recetaPorEliminar = null },
            title = { Text("¿Eliminar la receta?") },
            text = { Text("«${receta.nombre}» se borrará de la base de datos del servidor.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.eliminar(receta, avisar)
                        recetaPorEliminar = null
                    }
                ) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { recetaPorEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}
