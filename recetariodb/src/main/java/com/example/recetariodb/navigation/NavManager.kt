package com.example.recetariodb.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recetariodb.ui.screens.AgregarReceta
import com.example.recetariodb.ui.screens.FoodsApp
import com.example.recetariodb.viewmodel.DailyFoodViewModel

@Composable
fun NavManager(dailyFoodVM: DailyFoodViewModel) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "FoodApp") {
        composable("FoodApp") {
            FoodsApp(navController, dailyFoodVM)
        }
        composable("AgregarReceta") {
            AgregarReceta(navController, dailyFoodVM)
        }
    }
}
