package com.example.recetariodb.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.recetariodb.R
import com.example.recetariodb.viewmodel.DailyFoodViewModel

@Composable
fun FoodsApp(navController: NavHostController, dailyFoodVM: DailyFoodViewModel) {
    val dataFood by dailyFoodVM.dataDailyFood.collectAsState()

    Scaffold(
        topBar = { FoodTopBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("AgregarReceta") }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar receta")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {

            dailyFoodVM.mensajeError?.let { mensaje ->
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            if (dataFood.isEmpty()) {
                Text(
                    text = "No hay recetas todavía. Agrega una con el botón + " +
                            "o carga las 5 recetas de ejemplo.",
                    modifier = Modifier.padding(16.dp)
                )
                Button(
                    onClick = { dailyFoodVM.cargarRecetasIniciales() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Cargar las 5 recetas")
                }
            }

            LazyColumn {
                items(dataFood) { food ->
                    CardDayInfo(
                        dayFood = food,
                        onEliminar = { dailyFoodVM.deleteRecipe(food.idDoc) }
                    )
                }
            }
        }
    }
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
