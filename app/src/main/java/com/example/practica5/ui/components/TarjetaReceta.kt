package com.example.practica5.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.practica5.model.Receta

/** "Chicken · Mexican"; si faltan los dos datos, lo dice. */
fun Receta.etiqueta(): String =
    listOf(categoria, area).filter { it.isNotBlank() }.joinToString(" · ").ifBlank { "Sin categoría" }

/** Foto de la receta (Coil la descarga de internet). Las recetas sin foto muestran un plato. */
@Composable
fun FotoReceta(url: String, nombre: String, modifier: Modifier = Modifier) {
    if (url.isBlank()) {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text("🍽️", style = MaterialTheme.typography.headlineSmall)
        }
    } else {
        AsyncImage(
            model = url,
            contentDescription = "Foto de $nombre",
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    }
}

/**
 * Un registro de la tabla en la lista. Tocar la tarjeta consulta ese registro en el servicio;
 * el corazón cambia el campo "favorita" directamente (PUT).
 */
@Composable
fun TarjetaReceta(receta: Receta, onClick: () -> Unit, onAlternarFavorita: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FotoReceta(
                url = receta.imagenUrl,
                nombre = receta.nombre,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = receta.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = receta.etiqueta(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Estrellas(calificacion = receta.calificacion, tamano = 16.dp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "cocinada ${receta.vecesCocinada} veces",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onAlternarFavorita) {
                    Icon(
                        imageVector = if (receta.favorita) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (receta.favorita) "Quitar de favoritas" else "Marcar como favorita",
                        tint = if (receta.favorita) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                    )
                }
                Text(
                    text = "#${receta.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
