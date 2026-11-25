package com.example.recetariodb.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.recetariodb.R
import com.example.recetariodb.model.DailyFood
import com.example.recetariodb.ui.theme.FoodAppTheme

// Tarjeta de cada receta (basada en CardDayInfo de FoodCal).
// Al tocarla se muestra u oculta la descripción.
@Composable
fun CardDayInfo(
    dayFood: DailyFood,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var descripVisibility by remember { mutableStateOf(false) }

    Card(
        onClick = { descripVisibility = !descripVisibility },
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                DayTitle(
                    day = dayFood.day,
                    title = dayFood.title,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onEliminar) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar receta"
                    )
                }
            }
            ShowImage(imageRes = dayFood.imageRes)
            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.space_component)))
            BodyContent(body = dayFood.description, descVisibility = descripVisibility)
        }
    }
}

@Composable
fun ShowImage(
    imageRes: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(dimensionResource(id = R.dimen.height_size))
            .padding(dimensionResource(id = R.dimen.padding_component))
            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(imageRes)
                .crossfade(true)
                .build(),
            contentDescription = "Receta",
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_platillo),
            error = painterResource(id = R.drawable.ic_platillo),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun DayTitle(
    day: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_component))
    ) {
        Text(
            text = "Día $day",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
fun BodyContent(
    body: String,
    descVisibility: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(visible = descVisibility) {
        Text(
            text = body,
            style = MaterialTheme.typography.bodyLarge,
            modifier = modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_component))
                .animateContentSize()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CardDayInfoPreview() {
    FoodAppTheme {
        CardDayInfo(
            dayFood = DailyFood(
                idDoc = "dia1",
                day = 1,
                title = "Tacos",
                description = "Tortillas de maíz con carne asada, cebolla y cilantro."
            ),
            onEliminar = {}
        )
    }
}
