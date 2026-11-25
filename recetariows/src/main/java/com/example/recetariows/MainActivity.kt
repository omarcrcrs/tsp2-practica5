package com.example.recetariows

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.recetariows.ui.screens.FoodsApp
import com.example.recetariows.ui.theme.FoodAppTheme
import com.example.recetariows.viewmodel.RecetaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val recetaVM: RecetaViewModel by viewModels()
        setContent {
            FoodAppTheme {
                FoodsApp(recetaVM)
            }
        }
    }
}
