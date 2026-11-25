package com.example.practica5.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.practica5.model.Receta
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Consulta de un registro: muestra lo que contestó GET /api/recetas/{id}.
 * Desde aquí se hacen los cambios al registro (calificar, favorita, veces cocinada, editar)
 * y la baja.
 */
@Composable
fun DetalleRecetaDialog(
    receta: Receta,
    onCerrar: () -> Unit,
    onCalificar: (Float) -> Unit,
    onAlternarFavorita: () -> Unit,
    onRegistrarCocinada: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text(receta.nombre) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FotoReceta(
                    url = receta.imagenUrl,
                    nombre = receta.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Text(
                    text = "Registro #${receta.id} · ${receta.etiqueta()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Estrellas(
                        calificacion = receta.calificacion,
                        modifier = Modifier.weight(1f),
                        tamano = 30.dp,
                        onCalificar = onCalificar
                    )
                    IconButton(onClick = onAlternarFavorita) {
                        Icon(
                            imageVector = if (receta.favorita) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (receta.favorita) "Quitar de favoritas" else "Marcar como favorita",
                            tint = if (receta.favorita) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Cocinada ${receta.vecesCocinada} veces",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    TextButton(onClick = onRegistrarCocinada) { Text("La cociné") }
                }
                Seccion(titulo = "Ingredientes", texto = receta.ingredientes)
                Seccion(titulo = "Preparación", texto = receta.instrucciones)
                Seccion(titulo = "Notas", texto = receta.notas)
                Text(
                    text = "Guardada el ${formatearFecha(receta.fechaGuardado)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onCerrar) { Text("Cerrar") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onEliminar) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
                TextButton(onClick = onEditar) { Text("Editar") }
            }
        }
    )
}

/** Título + texto; si el campo viene vacío no se dibuja nada. */
@Composable
private fun Seccion(titulo: String, texto: String) {
    if (texto.isBlank()) return
    Column {
        Text(text = titulo, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
        Text(text = texto, style = MaterialTheme.typography.bodyMedium)
    }
}

/** La fecha viaja como Long (milisegundos); aquí se vuelve texto para la persona. */
private fun formatearFecha(milisegundos: Long): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(milisegundos))
