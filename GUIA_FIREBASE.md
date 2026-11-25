# Práctica 5 – Servicios Web · Guía de configuración

Dos módulos del proyecto `Practica5` que usan **la misma base de datos** de Cloud Firestore
(colección `DayFood`, igual que la app FoodCal del profe):

| Módulo | Actividad | Cómo se conecta a Firestore |
|---|---|---|
| **recetariows** | 1. Servicio web simple | API REST de Firestore con Retrofit (GET, PATCH, DELETE) |
| **recetariodb** | 2. Servicio web basado en base de datos | SDK de Firebase (tiempo real), como FoodCal |

Todo funciona con el **plan gratuito (Spark)**. No se pide tarjeta.

> **Integración en `Practica5` (24-nov).** El código llegó como dos proyectos separados
> (`RecetarioWS` y `RecetarioDB`, paquete `com.kevin.…`). Ahora son los módulos `recetariows` y
> `recetariodb` de este proyecto, con el paquete renombrado a `com.example.recetariows` y
> `com.example.recetariodb`, y con las versiones de Gradle/Compose del proyecto. Para correr uno,
> elígelo en el selector de ejecución de Android Studio (junto al botón ▶).
> La conexión con Firebase se llena con un comando: `python3 configurar_firebase.py ruta/al/google-services.json`

---

## Parte A. Configurar Firebase (una sola vez)

### 1. Crear el proyecto
1. Entra a <https://console.firebase.google.com> con tu cuenta de Google.
2. **Crear un proyecto** → nombre, por ejemplo `recetario-practica5`.
3. Google Analytics: puedes **desactivarlo** (no se usa). → **Crear proyecto**.

### 2. Crear la base de datos Cloud Firestore
1. En el menú izquierdo: **Compilación → Firestore Database** (en algunas versiones de la
   consola aparece como *Bases de datos y almacenamiento → Firestore*).
2. **Crear base de datos**.
3. Si pregunta la edición, elige **Standard**. Deja el ID `(default)`.
4. Ubicación: la que aparezca por defecto (por ejemplo `nam5 (United States)`).
   *No se puede cambiar después, pero para la práctica da igual.*
5. Elige **Comenzar en modo de prueba** → **Crear**.

### 3. Reglas de seguridad
El modo de prueba deja leer y escribir **solo 30 días**. Para que no te falle el día de la
entrega, ve a la pestaña **Reglas**, reemplaza todo por esto y presiona **Publicar**:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /DayFood/{documento} {
      allow read, write: if true;
    }
  }
}
```

> Estas reglas dejan que cualquiera lea y escriba en la colección `DayFood`.
> Sirven para la práctica, pero **no** para una app real (ahí se usa Firebase Authentication).

### 4. Copiar el ID del proyecto (para la Actividad 1)
Engrane ⚙ (junto a "Descripción general del proyecto") → **Configuración del proyecto** →
copia el **ID del proyecto** (por ejemplo `recetario-practica5-a1b2c`).
*Ojo: es el ID, no el nombre.*

### 5. Registrar la app Android (para la Actividad 2)
1. En **Configuración del proyecto → Tus apps**, presiona el icono de **Android**.
2. **Nombre del paquete de Android:** `com.example.recetariodb`. Si la app ya está registrada con otro
   nombre (por ejemplo `com.kevin.recetariodb`), ese json también sirve: no hace falta registrarla otra vez.
3. Sobrenombre: `Recetario DB` (opcional). El SHA-1 **no** es necesario.
4. **Registrar app** → **Descargar google-services.json**.
5. Los pasos de "Agregar el SDK" **ya están hechos** en el proyecto; solo dale *Siguiente* hasta terminar.

---

## Parte B. Actividad 1 – RecetarioWS

1. Android Studio → abre la carpeta `Practica5`, espera el *Gradle Sync* y elige el módulo
   **recetariows** en el selector de ejecución.
2. Corre `python3 configurar_firebase.py --project-id TU_ID` (o con el json, que configura los dos
   módulos). A mano: abre `recetariows/src/main/java/com/example/recetariows/network/FirestoreApiService.kt` y cambia:
   ```kotlin
   const val PROJECT_ID = "PEGA_AQUI_TU_PROJECT_ID"
   ```
   por tu ID del paso A.4.
3. (Opcional) Pon las URLs de las imágenes en `model/RecetasIniciales.kt` (ver Parte D).
4. Ejecuta ▶ en el emulador o en tu celular.
5. Presiona **Cargar recetas** → se crean los documentos `dia1`…`dia5`.
   En la consola de Firebase (Firestore → Datos) verás la colección `DayFood` con las 5 comidas.
6. Prueba **Consultar** y el icono de **basura** de una tarjeta.

**Qué es el "servicio web" aquí:** Firestore expone una API REST en
`https://firestore.googleapis.com/v1/`. La app define sus métodos en la interfaz Retrofit
`FirestoreApiService` y la clase `RecetaService` los **encapsula**: la pantalla solo llama a
`listarRecetas()`, `guardarReceta()` y `eliminarReceta()` sin saber de URLs ni JSON.

| Método de `RecetaService` | Verbo HTTP | Ruta |
|---|---|---|
| `listarRecetas()` | GET | `.../documents/DayFood` |
| `guardarReceta(receta)` | PATCH | `.../documents/DayFood/dia1` |
| `eliminarReceta(id)` | DELETE | `.../documents/DayFood/dia1` |
| `cargarRecetasIniciales()` | 5 × PATCH | una por comida |

---

## Parte C. Actividad 2 – RecetarioDB

1. Con el **google-services.json** (paso A.5), corre dentro de `Practica5`:
   `python3 configurar_firebase.py ruta/al/google-services.json`
   Eso llena `recetariodb/src/main/res/values/firebase_config.xml`. El módulo no usa el plugin
   `google-services`, así que el json no se copia al proyecto.
2. En Android Studio elige el módulo **recetariodb** en el selector de ejecución.
3. Ejecuta ▶.
4. Si ya usaste la Actividad 1, verás las 5 recetas. Si la colección está vacía, presiona
   **Cargar las 5 recetas**.
5. Prueba:
   - Botón **+** → llena el formulario → **Agregar receta**.
   - Icono de **basura** → elimina.
   - **Tiempo real:** con la app abierta, cambia el `title` de un documento en la consola de
     Firebase; la app se actualiza sola, sin recargar.

**Qué hace diferente a la Actividad 1:** aquí la app depende por completo de la base de datos
y usa el SDK oficial (`Firebase.firestore`), que mantiene una conexión abierta con
`addSnapshotListener` para recibir los cambios al instante.

---

## Parte D. Imágenes (opcional)

Como en FoodCal, la imagen se guarda como **URL** en el campo `imageRes`.
1. Entra a <https://imgbb.com> → **Start uploading** → sube la foto de un platillo.
2. Copia el **Enlace directo** (termina en `.jpg`/`.png` y empieza con `https://i.ibb.co/`).
3. Úsalo en una de estas formas:
   - Pégalo en `RecetasIniciales.kt` antes de cargar las recetas, **o**
   - En la consola de Firebase, abre el documento y edita el campo `imageRes`, **o**
   - En la Actividad 2, escríbelo en "URL de la imagen" del formulario.

Si no hay imagen, la app muestra un icono de tenedor y cuchillo.

---

## Estructura del código

**recetariows** (`recetariows/src/main/java/com/example/recetariows/`)
```
MainActivity.kt              crea el ViewModel y muestra la pantalla
model/DailyFood.kt           datos de una receta (igual que FoodCal)
model/RecetasIniciales.kt    las 5 comidas
network/FirestoreApiService.kt  PROJECT_ID, formato JSON de Firestore,
                                interfaz Retrofit y cliente
network/RecetaService.kt     encapsula el servicio (listar, guardar, eliminar)
viewmodel/RecetaViewModel.kt estados Cargando / Éxito / Error
ui/screens/ListFood.kt       pantalla con botones y lista
ui/screens/DayComponents.kt  tarjeta de cada receta (de FoodCal)
ui/theme/                    tema del profe
```

**recetariodb** (`recetariodb/src/main/java/com/example/recetariodb/`)
```
MainActivity.kt                 crea el ViewModel y la navegación
model/DailyFood.kt, RecetasIniciales.kt
viewmodel/DailyFoodViewModel.kt leer (tiempo real), agregar, cargar 5, eliminar
navigation/NavManager.kt        rutas "FoodApp" y "AgregarReceta"
ui/screens/ListFood.kt          lista + botón +
ui/screens/AgregarReceta.kt     formulario
ui/screens/DayComponents.kt     tarjeta de cada receta
```

---

## Problemas comunes

| Mensaje | Causa y solución |
|---|---|
| `Falta poner tu PROJECT_ID` | Cambia la constante en `FirestoreApiService.kt` (Act. 1). |
| `No encontrado (404)` | El PROJECT_ID está mal escrito o no creaste la base de datos Firestore. |
| `Permiso denegado (403)` / `PERMISSION_DENIED` | Las reglas no permiten el acceso o ya venció el modo de prueba → publica las reglas del paso A.3. |
| `Falta configurar Firebase` | No se ha llenado `recetariodb/src/main/res/values/firebase_config.xml` (Act. 2): corre `configurar_firebase.py`. |
| La lista no aparece y no hay error | Revisa que el emulador tenga internet (abre Chrome en el emulador). |
| Android Studio sugiere actualizar AGP/Gradle | Puedes aceptarlo o ignorarlo; el proyecto funciona con las versiones incluidas. |
