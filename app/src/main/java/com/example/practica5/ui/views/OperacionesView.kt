@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.practica5.ui.views

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.practica5.model.ResultadoOperacion
import com.example.practica5.ui.theme.Practica5Theme
import com.example.practica5.viewmodel.OperacionUiState
import com.example.practica5.viewmodel.OperacionesViewModel

/** Las cuatro operaciones del servicio web simple. [ruta] es el final de /api/operaciones/… */
enum class Operacion(val titulo: String, val ruta: String) {
    CALORIAS("Calorías", "calorias"),
    PORCIONES("Porciones", "escalar"),
    PESO("Peso", "peso"),
    TEMPERATURA("Horno", "temperatura")
}

private val unidadesDePeso = listOf("g", "kg", "oz", "lb")
private val escalasDeTemperatura = listOf("C", "F")

/** Actividad 1: pantalla conectada a su ViewModel. */
@Composable
fun OperacionesView(viewModel: OperacionesViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    OperacionesContent(
        uiState = uiState,
        onCalorias = viewModel::calcularCalorias,
        onPorciones = viewModel::escalarPorciones,
        onPeso = viewModel::convertirPeso,
        onTemperatura = viewModel::convertirTemperatura,
        onCambioDeOperacion = viewModel::limpiar
    )
}

/** La pantalla sin ViewModel: recibe el estado y avisa con lambdas (así se puede previsualizar). */
@Composable
fun OperacionesContent(
    uiState: OperacionUiState,
    onCalorias: (String, String) -> Unit,
    onPorciones: (String, String, String) -> Unit,
    onPeso: (String, String, String) -> Unit,
    onTemperatura: (String, String, String) -> Unit,
    onCambioDeOperacion: () -> Unit,
    modifier: Modifier = Modifier
) {
    var operacion by rememberSaveable { mutableStateOf(Operacion.CALORIAS) }
    // Calorías por peso
    var gramos by rememberSaveable { mutableStateOf("") }
    var kcalPor100g by rememberSaveable { mutableStateOf("") }
    // Escalar porciones
    var cantidad by rememberSaveable { mutableStateOf("") }
    var porcionesBase by rememberSaveable { mutableStateOf("") }
    var porcionesDeseadas by rememberSaveable { mutableStateOf("") }
    // Convertir peso
    var peso by rememberSaveable { mutableStateOf("") }
    var pesoDe by rememberSaveable { mutableStateOf("lb") }
    var pesoA by rememberSaveable { mutableStateOf("g") }
    // Temperatura del horno
    var temperatura by rememberSaveable { mutableStateOf("") }
    var temperaturaDe by rememberSaveable { mutableStateOf("C") }
    val temperaturaA = if (temperaturaDe == "C") "F" else "C"

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Actividad 1 · Servicio web simple",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "La app solo captura los datos. La cuenta la hace el servicio web y regresa el resultado en JSON.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Operacion.entries.forEach { opcion ->
                FilterChip(
                    selected = opcion == operacion,
                    onClick = {
                        operacion = opcion
                        onCambioDeOperacion()
                    },
                    label = { Text(opcion.titulo) }
                )
            }
        }

        when (operacion) {
            Operacion.CALORIAS -> {
                CampoNumerico(valor = gramos, onCambio = { gramos = it }, etiqueta = "Peso del ingrediente (g)")
                CampoNumerico(valor = kcalPor100g, onCambio = { kcalPor100g = it }, etiqueta = "kcal por cada 100 g")
            }

            Operacion.PORCIONES -> {
                CampoNumerico(valor = cantidad, onCambio = { cantidad = it }, etiqueta = "Cantidad del ingrediente en la receta")
                CampoNumerico(valor = porcionesBase, onCambio = { porcionesBase = it }, etiqueta = "Porciones que rinde la receta")
                CampoNumerico(valor = porcionesDeseadas, onCambio = { porcionesDeseadas = it }, etiqueta = "Porciones que quiero preparar")
            }

            Operacion.PESO -> {
                CampoNumerico(valor = peso, onCambio = { peso = it }, etiqueta = "Peso a convertir")
                SelectorDeUnidad(titulo = "De", opciones = unidadesDePeso, seleccionada = pesoDe, onSeleccion = { pesoDe = it })
                SelectorDeUnidad(titulo = "A", opciones = unidadesDePeso, seleccionada = pesoA, onSeleccion = { pesoA = it })
            }

            Operacion.TEMPERATURA -> {
                CampoNumerico(valor = temperatura, onCambio = { temperatura = it }, etiqueta = "Temperatura del horno")
                SelectorDeUnidad(
                    titulo = "De",
                    opciones = escalasDeTemperatura,
                    seleccionada = temperaturaDe,
                    onSeleccion = { temperaturaDe = it },
                    prefijo = "°"
                )
                Text(text = "Se convierte a °$temperaturaA", style = MaterialTheme.typography.bodyMedium)
            }
        }

        Button(
            onClick = {
                when (operacion) {
                    Operacion.CALORIAS -> onCalorias(gramos, kcalPor100g)
                    Operacion.PORCIONES -> onPorciones(cantidad, porcionesBase, porcionesDeseadas)
                    Operacion.PESO -> onPeso(peso, pesoDe, pesoA)
                    Operacion.TEMPERATURA -> onTemperatura(temperatura, temperaturaDe, temperaturaA)
                }
            },
            enabled = uiState !is OperacionUiState.Cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calcular en el servicio")
        }

        ResultadoDelServicio(uiState = uiState, ruta = operacion.ruta)
    }
}

@Composable
private fun CampoNumerico(valor: String, onCambio: (String) -> Unit, etiqueta: String) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SelectorDeUnidad(
    titulo: String,
    opciones: List<String>,
    seleccionada: String,
    onSeleccion: (String) -> Unit,
    prefijo: String = ""
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = titulo, modifier = Modifier.width(28.dp), style = MaterialTheme.typography.labelLarge)
        opciones.forEach { opcion ->
            FilterChip(
                selected = opcion == seleccionada,
                onClick = { onSeleccion(opcion) },
                label = { Text("$prefijo$opcion") }
            )
        }
    }
}

/** Dibuja lo que contestó el servicio según el estado. */
@Composable
private fun ResultadoDelServicio(uiState: OperacionUiState, ruta: String) {
    when (uiState) {
        is OperacionUiState.Inicial -> Text(
            text = "La petición saldrá a GET /api/operaciones/$ruta",
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        is OperacionUiState.Cargando -> Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            Spacer(Modifier.width(12.dp))
            Text("Esperando la respuesta del servicio…")
        }

        is OperacionUiState.Exito -> ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Respuesta del servicio", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = "${formatearNumero(uiState.resultado.resultado)} ${uiState.resultado.unidad}".trim(),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(text = uiState.resultado.detalle, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "GET /api/operaciones/${uiState.resultado.operacion}",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        is OperacionUiState.Error -> Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Text(
                text = uiState.mensaje,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

/** 3.0 -> "3" · 247.5 -> "247.5" (el servicio ya redondea a 2 decimales). */
internal fun formatearNumero(numero: Double): String =
    if (numero % 1.0 == 0.0) numero.toLong().toString() else numero.toString()

@Preview(showBackground = true)
@Composable
private fun OperacionesContentPreview() {
    Practica5Theme {
        OperacionesContent(
            uiState = OperacionUiState.Exito(
                ResultadoOperacion(
                    operacion = "calorias",
                    resultado = 247.5,
                    unidad = "kcal",
                    detalle = "150 g × 165 kcal ÷ 100 g = 247.5 kcal"
                )
            ),
            onCalorias = { _, _ -> },
            onPorciones = { _, _, _ -> },
            onPeso = { _, _, _ -> },
            onTemperatura = { _, _, _ -> },
            onCambioDeOperacion = {}
        )
    }
}
