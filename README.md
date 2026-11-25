# Práctica 5 — Servicios web · Equipo 2

## Módulos del proyecto

El proyecto tiene tres apps; se elige cuál correr en el selector de ejecución de Android Studio.

| Módulo | Qué es | Paquete |
|---|---|---|
| `recetariows` | **Actividad 1 del equipo:** API REST de Cloud Firestore con Retrofit | `com.example.recetariows` |
| `recetariodb` | **Actividad 2 del equipo:** SDK de Cloud Firestore en tiempo real | `com.example.recetariodb` |
| `app` + `servicio_web/` | Versión alterna: servicio web propio en Python + SQLite (descrita abajo) | `com.example.practica5` |

Los dos módulos del equipo usan la misma base de Firestore (colección `DayFood`). Cómo crear el
proyecto de Firebase y conectarlo: **`GUIA_FIREBASE.md`**. Conexión en un paso:
`python3 configurar_firebase.py ruta/al/google-services.json`.

---

# Versión alterna — servicio web propio (`app` + `servicio_web/`)

App de alimentación del equipo (continúa las prácticas 2 y 3), ahora como **cliente de un
servicio web propio**. El proyecto tiene dos partes:

| Parte | Carpeta | Qué es |
|---|---|---|
| Servicio web | `servicio_web/` | Servidor REST en Python (solo biblioteca estándar) + SQLite |
| App Android | `app/` | Kotlin + Jetpack Compose + Retrofit (paquete `com.example.practica5`) |

## Cómo correrlo

1. **Encender el servicio** (en la computadora):
   ```
   cd servicio_web
   python3 servidor.py
   ```
   Al arrancar imprime las direcciones y deja la bitácora de cada petición.
   En el navegador, `http://localhost:8080/` lista las rutas.
2. **Abrir el proyecto en Android Studio**, sincronizar Gradle y ejecutar la app.
3. **Conectar la app** (pestaña *Servidor*; al abrir intenta detectarlo sola):
   - Emulador → atajo *Emulador* (`http://10.0.2.2:8080/`).
   - Teléfono por USB → `adb reverse tcp:8080 tcp:8080` y atajo *Teléfono por USB*.
     (si `adb` no está en el PATH: `~/Android/Sdk/platform-tools/adb`)
   - Teléfono por WiFi (misma red) → la dirección *Teléfono WiFi* que imprime el servidor.
     Si no conecta, abrir el puerto: `sudo ufw allow 8080/tcp`.

## Actividad 1 — Servicio web simple

Operaciones de cocina. Cada una es un método de `CalculadoraCocina` (`servicio_web/operaciones.py`)
y el servidor la publica como una ruta `GET`. La app solo captura los datos y muestra la respuesta.

| Ruta | Método que la resuelve | Ejemplo de respuesta |
|---|---|---|
| `GET /api/operaciones/calorias?gramos=150&kcal100=165` | `calorias_por_peso` | `247.5 kcal` |
| `GET /api/operaciones/escalar?cantidad=2&porcionesBase=4&porcionesDeseadas=6` | `escalar_porciones` | `3` |
| `GET /api/operaciones/peso?valor=1&de=lb&a=g` | `convertir_peso` | `453.59 g` |
| `GET /api/operaciones/temperatura?valor=175&de=C&a=F` | `convertir_temperatura` | `347 °F` |

Todas contestan con la misma forma: `{"operacion", "resultado", "unidad", "detalle"}`.
Un dato inválido regresa **400** con `{"error": "..."}`.

## Actividad 2 — Servicio web basado en base de datos

El recetario de la Práctica 3, pero la base de datos (SQLite, tabla `recetas`, 5 registros
iniciales) vive en el servidor (`servicio_web/base_datos.py`). La app ya no usa Room: cada
operación es una petición HTTP.

| Operación | Ruta | SQL en el servidor |
|---|---|---|
| Listar / buscar | `GET /api/recetas` (`?buscar=texto`) | `SELECT … LIKE` |
| Consultar un registro | `GET /api/recetas/{id}` | `SELECT … WHERE id = ?` |
| Agregar | `POST /api/recetas` | `INSERT` |
| Cambiar | `PUT /api/recetas/{id}` | `UPDATE` |
| Eliminar | `DELETE /api/recetas/{id}` | `DELETE` |

Códigos: **200** correcto · **201** creado · **400** dato inválido · **404** no existe · **405** verbo no permitido.

## Capas de la app (MVVM)

- `model/` — `Receta` y las respuestas del servicio (mismos nombres que el JSON).
- `network/` — `ServicioWebApi` (rutas con Retrofit), `ClienteServicioWeb` (dirección del
  servidor) y `llamarServicio` (traduce las fallas a mensajes).
- `viewmodel/` — uno por tarea: `OperacionesViewModel` (Act. 1), `RecetarioViewModel` (Act. 2)
  y `ServidorViewModel` (conexión), cada uno con su `sealed interface` de estados.
- `ui/` — `Practica5App` (pestañas), `views/` (una por pestaña) y `components/`.

La app permite HTTP sin cifrar (`res/xml/network_security_config.xml`) porque el servicio corre
en la red local; publicado iría por HTTPS.
