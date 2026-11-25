package com.example.recetariows.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.recetariows.R
import com.example.recetariows.viewmodel.EstadoServicio
import com.example.recetariows.viewmodel.RecetaViewModel

@Composable
fun FoodsApp(recetaVM: RecetaViewModel) {
    val dataFood by recetaVM.dataDailyFood.collectAsState()
    val estado = recetaVM.estado

    Scaffold(
        topBar = { FoodTopBar() }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            // Botones que llaman a los métodos del servicio web
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Button(
                    onClick = { recetaVM.cargarRecetas() },
                    enabled = estado !is EstadoServicio.Cargando,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cargar recetas")
                }
                OutlinedButton(
                    onClick = { recetaVM.consultarRecetas() },
                    enabled = estado !is EstadoServicio.Cargando,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Consultar")
                }
            }

            // Estado de la última llamada al servicio
            when (estado) {
                is EstadoServicio.Cargando ->
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                is EstadoServicio.Exito ->
                    MensajeEstado(estado.mensaje, esError = false)
                is EstadoServicio.Error ->
                    MensajeEstado(estado.mensaje, esError = true)
                EstadoServicio.Inactivo -> {}
            }

            if (dataFood.isEmpty() && estado !is EstadoServicio.Cargando) {
                Text(
                    text = "No hay recetas. Presiona \"Cargar recetas\".",
                    modifier = Modifier.padding(16.dp)
                )
            }

            LazyColumn {
                items(dataFood) { food ->
                    CardDayInfo(
                        dayFood = food,
                        onEliminar = { recetaVM.eliminarReceta(food.idDoc) }
                    )
                }
            }
        }
    }
}

@Composable
fun MensajeEstado(mensaje: String, esError: Boolean) {
    Text(
        text = mensaje,
        color = if (esError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineMedium
            )
        },
        modifier = Modifier.fillMaxWidth(),
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.onSecondary,
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}
