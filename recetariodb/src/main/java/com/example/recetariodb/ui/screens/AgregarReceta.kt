package com.example.recetariodb.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.recetariodb.viewmodel.DailyFoodViewModel

// Formulario para agregar una receta (basado en DataFields de FoodCal,
// pero con campos que sí se pueden escribir y un botón que sí guarda)
@Composable
fun AgregarReceta(navController: NavHostController, dailyFoodVM: DailyFoodViewModel) {
    var dayText by remember { mutableStateOf("") }
    var titleText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }
    var imageText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        dailyFoodVM.limpiarError() // no mostrar errores viejos al entrar
    }

    Scaffold(
        topBar = { DataTopBar(navController) }
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = dayText,
                onValueChange = { dayText = it },
                label = { Text("Número de día") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = titleText,
                onValueChange = { titleText = it },
                label = { Text("Nombre del platillo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = descriptionText,
                onValueChange = { descriptionText = it },
                label = { Text("Descripción / receta") },
                minLines = 3,
                maxLines = 6,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = imageText,
                onValueChange = { imageText = it },
                label = { Text("URL de la imagen (opcional)") },
                placeholder = { Text("https://i.ibb.co/...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Vista previa de la imagen
            if (imageText.isNotBlank()) {
                ShowImage(imageRes = imageText.trim())
            }

            dailyFoodVM.mensajeError?.let { mensaje ->
                Text(text = mensaje, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    dailyFoodVM.addRecipe(
                        day = dayText.toIntOrNull() ?: 0,
                        title = titleText.trim(),
                        description = descriptionText.trim(),
                        imageRes = imageText.trim(),
                        onSuccess = { navController.navigateUp() } // regresa a la lista
                    )
                },
                enabled = !dailyFoodVM.guardando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (dailyFoodVM.guardando) "Guardando..." else "Agregar receta")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataTopBar(navController: NavHostController) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Nueva receta",
                style = MaterialTheme.typography.headlineMedium
            )
        },
        modifier = Modifier.fillMaxWidth(),
        colors = TopAppBarDefaults.topAppBarColors(
            titleContentColor = MaterialTheme.colorScheme.onSecondary,
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Regresar",
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }
        }
    )
}
