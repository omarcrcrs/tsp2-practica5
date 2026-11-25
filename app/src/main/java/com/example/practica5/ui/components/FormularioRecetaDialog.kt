package com.example.practica5.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.practica5.model.Receta

/**
 * Formulario compartido por el alta y la edición.
 * [recetaInicial] = null -> receta nueva (POST); con una receta -> edición de ese registro (PUT).
 * Los campos que no están en el formulario (id, calificación, favorita…) se conservan.
 */
@Composable
fun FormularioRecetaDialog(
    recetaInicial: Receta?,
    onGuardar: (Receta) -> Unit,
    onCancelar: () -> Unit
) {
    val base = recetaInicial ?: Receta()
    var nombre by rememberSaveable { mutableStateOf(base.nombre) }
    var categoria by rememberSaveable { mutableStateOf(base.categoria) }
    var area by rememberSaveable { mutableStateOf(base.area) }
    var ingredientes by rememberSaveable { mutableStateOf(base.ingredientes) }
    var instrucciones by rememberSaveable { mutableStateOf(base.instrucciones) }
    var notas by rememberSaveable { mutableStateOf(base.notas) }
    var imagenUrl by rememberSaveable { mutableStateOf(base.imagenUrl) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(if (recetaInicial == null) "Nueva receta" else "Editar receta #${base.id}") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = categoria,
                    onValueChange = { categoria = it },
                    label = { Text("Categoría (Chicken, Vegetarian…)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = area,
                    onValueChange = { area = it },
                    label = { Text("Cocina de origen") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = ingredientes,
                    onValueChange = { ingredientes = it },
                    label = { Text("Ingredientes (uno por línea)") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = instrucciones,
                    onValueChange = { instrucciones = it },
                    label = { Text("Preparación") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it },
                    label = { Text("Notas") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = imagenUrl,
                    onValueChange = { imagenUrl = it },
                    label = { Text("URL de la foto (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = nombre.isNotBlank(),
                onClick = {
                    onGuardar(
                        base.copy(
                            nombre = nombre.trim(),
                            categoria = categoria.trim(),
                            area = area.trim(),
                            ingredientes = ingredientes.trim(),
                            instrucciones = instrucciones.trim(),
                            notas = notas.trim(),
                            imagenUrl = imagenUrl.trim()
                        )
                    )
                }
            ) { Text(if (recetaInicial == null) "Guardar" else "Guardar cambios") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}
