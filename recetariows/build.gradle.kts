// Actividad 1 — Servicio web simple: API REST de Firestore con Retrofit.
// Código del equipo (antes com.kevin.recetariows); misma configuración que el módulo app.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.recetariows"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.recetariows"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    // Fuentes de Google Fonts que usa el tema del profe (Type.kt)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // ViewModel y viewModelScope
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // Servicio web: Retrofit convierte las peticiones HTTP en funciones de Kotlin
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)

    // Mostrar imágenes desde una URL
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    debugImplementation(libs.androidx.compose.ui.tooling)
}
