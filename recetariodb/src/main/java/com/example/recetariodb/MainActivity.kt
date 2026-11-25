package com.example.recetariodb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.recetariodb.navigation.NavManager
import com.example.recetariodb.ui.theme.FoodAppTheme
import com.example.recetariodb.viewmodel.DailyFoodViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val dailyFoodVM: DailyFoodViewModel by viewModels()
        setContent {
            FoodAppTheme {
                NavManager(dailyFoodVM)
            }
        }
    }
}
