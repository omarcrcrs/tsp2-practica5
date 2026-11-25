@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.practica5.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.practica5.network.ClienteServicioWeb
import com.example.practica5.viewmodel.ConexionUiState
import com.example.practica5.viewmodel.ServidorViewModel

/**
 * Dónde está el servicio web y si contesta. Hace falta porque la dirección cambia según dónde
 * corra la app: emulador, teléfono por USB o teléfono en la misma red WiFi.
 */
@Composable
fun ServidorView(viewModel: ServidorViewModel) {
    var direccion by rememberSaveable { mutableStateOf(viewModel.urlActiva) }
    // Si la detección o un atajo cambian la dirección activa, el campo de texto la refleja
    LaunchedEffect(viewModel.urlActiva) { direccion = viewModel.urlActiva }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TarjetaDeConexion(conexion = viewModel.conexion, url = viewModel.urlActiva)

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección del servicio web") },
            placeholder = { Text("http://192.168.1.70:8080") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.usar(direccion) }, modifier = Modifier.weight(1f)) {
                Text("Probar y guardar")
            }
            OutlinedButton(onClick = { viewModel.detectar() }, modifier = Modifier.weight(1f)) {
                Text("Detectar")
            }
        }

        Text(text = "Atajos", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = viewModel.urlActiva == ClienteServicioWeb.URL_EMULADOR,
                onClick = { viewModel.usar(ClienteServicioWeb.URL_EMULADOR) },
                label = { Text("Emulador") }
            )
            FilterChip(
                selected = viewModel.urlActiva == ClienteServicioWeb.URL_USB,
                onClick = { viewModel.usar(ClienteServicioWeb.URL_USB) },
                label = { Text("Teléfono por USB") }
            )
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Cómo conectar",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text("1. En la computadora, dentro de servicio_web/:", style = MaterialTheme.typography.bodySmall)
                Text("python3 servidor.py", style = MaterialTheme.typography.bodySmall, fontFamily = FontFamily.Monospace)
                Text(
                    "2. Emulador: usa el atajo \"Emulador\" (10.0.2.2 es la computadora vista desde el emulador).",
                    style = MaterialTheme.typography.bodySmall
                )
                Text("3. Teléfono por USB: corre en la computadora", style = MaterialTheme.typography.bodySmall)
                Text(
                    "adb reverse tcp:8080 tcp:8080",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace
                )
                Text("y usa el atajo \"Teléfono por USB\".", style = MaterialTheme.typography.bodySmall)
                Text(
                    "4. Teléfono por WiFi (misma red): escribe la dirección \"Teléfono WiFi\" que imprime el servidor al arrancar.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun TarjetaDeConexion(conexion: ConexionUiState, url: String) {
    val colores = when (conexion) {
        is ConexionUiState.Conectado -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        is ConexionUiState.SinConexion -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        is ConexionUiState.Probando -> CardDefaults.cardColors()
    }
    Card(modifier = Modifier.fillMaxWidth(), colors = colores) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            when (conexion) {
                is ConexionUiState.Probando -> Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(12.dp))
                    Text("Buscando el servicio…")
                }

                is ConexionUiState.Conectado -> {
                    Text(
                        text = "🟢 Conectado",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = url, style = MaterialTheme.typography.bodyMedium, fontFamily = FontFamily.Monospace)
                    Text(
                        text = "${conexion.estado.servicio} v${conexion.estado.version} · " +
                            "${conexion.estado.recetas} recetas en la base de datos",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Hora del servidor: ${conexion.estado.horaServidor}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                is ConexionUiState.SinConexion -> {
                    Text(
                        text = "🔴 Sin conexión",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = url,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Text(
                        text = conexion.mensaje,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}
