#!/usr/bin/env python3
"""
Conecta los módulos recetariows (Actividad 1) y recetariodb (Actividad 2) con el proyecto
de Firebase del equipo, sin tener que copiar los datos a mano.

    python3 configurar_firebase.py ruta/al/google-services.json     # configura los dos módulos
    python3 configurar_firebase.py --project-id mi-proyecto-a1b2c   # solo la Actividad 1

No importa con qué nombre de paquete se registró la app en Firebase: solo se usan el ID del
proyecto, el ID de la app y la llave que vienen en el json.
"""
import argparse
import json
import pathlib
import re
from xml.sax.saxutils import escape

parser = argparse.ArgumentParser(description="Configura Firebase en los módulos de la práctica")
parser.add_argument("json", nargs="?", help="google-services.json descargado de la consola de Firebase")
parser.add_argument("--project-id", help="ID del proyecto (si no tienes el json)")
parser.add_argument("--raiz", default=str(pathlib.Path(__file__).resolve().parent), help=argparse.SUPPRESS)
opciones = parser.parse_args()
raiz = pathlib.Path(opciones.raiz)

if opciones.json:
    datos = json.loads(pathlib.Path(opciones.json).read_text(encoding="utf-8"))
    project_id = datos["project_info"]["project_id"]
    cliente = datos["client"][0]
    app_id = cliente["client_info"]["mobilesdk_app_id"]
    api_key = cliente["api_key"][0]["current_key"]
elif opciones.project_id:
    project_id, app_id, api_key = opciones.project_id.strip(), None, None
else:
    parser.error("pasa el google-services.json o --project-id")

# Actividad 1: la constante PROJECT_ID que usa Retrofit
servicio = raiz / "recetariows/src/main/java/com/example/recetariows/network/FirestoreApiService.kt"
texto = servicio.read_text(encoding="utf-8")
nuevo, cambios = re.subn(r'const val PROJECT_ID = "[^"]*"', f'const val PROJECT_ID = "{project_id}"', texto)
if cambios != 1:
    raise SystemExit("No encontré la constante PROJECT_ID en FirestoreApiService.kt")
servicio.write_text(nuevo, encoding="utf-8")
print(f"Actividad 1 (recetariows): PROJECT_ID = {project_id}")

# Actividad 2: los recursos que lee el SDK de Firebase al arrancar
if app_id and api_key:
    config = raiz / "recetariodb/src/main/res/values/firebase_config.xml"
    config.write_text(
        '<?xml version="1.0" encoding="utf-8"?>\n'
        "<!-- Generado por configurar_firebase.py a partir de google-services.json -->\n"
        "<resources>\n"
        f'    <string name="project_id" translatable="false">{escape(project_id)}</string>\n'
        f'    <string name="google_app_id" translatable="false">{escape(app_id)}</string>\n'
        f'    <string name="google_api_key" translatable="false">{escape(api_key)}</string>\n'
        "</resources>\n",
        encoding="utf-8",
    )
    print(f"Actividad 2 (recetariodb): firebase_config.xml listo (app {app_id})")
else:
    print("Actividad 2 (recetariodb): sin cambios; para configurarla hace falta el google-services.json")
