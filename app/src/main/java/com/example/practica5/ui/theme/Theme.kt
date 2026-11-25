package com.example.practica5.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BlueGrey80,
    secondary = BlueGrey60,
    tertiary = Amber40,
    background = BackgroundDark,
    surface = BlueGrey20,
    onPrimary = BlueGrey10,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = BlueGrey90,
    onSurface = BlueGrey90,
    primaryContainer = BlueGrey40,
    onPrimaryContainer = BlueGrey90,
    secondaryContainer = BlueGrey40,
    onSecondaryContainer = BlueGrey90,
    tertiaryContainer = AmberContainerDark,
    onTertiaryContainer = AmberContainerLight,
    surfaceVariant = BlueGrey40,
    onSurfaceVariant = BlueGrey80,
    surfaceContainerLow = SurfaceLowDark,
    surfaceContainer = BlueGrey20,
    surfaceContainerHigh = SurfaceHighDark
)

private val LightColorScheme = lightColorScheme(
    primary = BlueGrey40,
    secondary = BlueGrey60,
    tertiary = Amber40,
    background = BackgroundLight,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = BlueGrey20,
    onSurface = BlueGrey20,
    primaryContainer = BlueGrey90,
    onPrimaryContainer = BlueGrey20,
    secondaryContainer = BlueGrey90,
    onSecondaryContainer = BlueGrey20,
    tertiaryContainer = AmberContainerLight,
    onTertiaryContainer = BlueGrey20,
    surfaceVariant = BlueGrey90,
    onSurfaceVariant = BlueGrey60,
    surfaceContainerLow = SurfaceLowLight,
    surfaceContainer = SurfaceMidLight,
    surfaceContainerHigh = SurfaceHighLight
)

@Composable
fun Practica5Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Material You apagado: se conserva la paleta azul-grisácea fija del equipo.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
