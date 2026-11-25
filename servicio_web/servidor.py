#!/usr/bin/env python3
"""
Práctica 5 — Servicios web (Equipo 2, Temas Selectos de Programación).

Servicio web REST "Cocina en red". Solo usa la biblioteca estándar de Python
(no hay que instalar nada):

    python3 servidor.py            # escucha en el puerto 8080
    python3 servidor.py --puerto 9000

Actividad 1 (servicio simple)        -> /api/operaciones/...   (operaciones.py)
Actividad 2 (servicio con BD SQLite) -> /api/recetas...        (base_datos.py)

Este archivo solo hace el trabajo de "servicio web": recibe la petición HTTP,
decide a qué método le toca, y contesta en JSON con su código de estado.
"""

import argparse
import json
import math
import socket
import sys
import time
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer
from pathlib import Path
from urllib.parse import parse_qs, urlsplit

from base_datos import RecetaNoEncontrada, RecetarioBD
from operaciones import CalculadoraCocina

CARPETA = Path(__file__).resolve().parent

calculadora = CalculadoraCocina()
recetario = None  # se crea en main() con la ruta de la base de datos


class NoEncontrado(Exception):
    """La ruta o el recurso pedido no existe (HTTP 404)."""


class MetodoNoPermitido(Exception):
    """La ruta existe, pero no con ese verbo HTTP (HTTP 405)."""


class ManejadorServicio(BaseHTTPRequestHandler):
    server_version = "CocinaEnRed/1.0"

    # Un método por verbo HTTP; todos pasan por el mismo embudo
    def do_GET(self):
        self._atender("GET")

    def do_POST(self):
        self._atender("POST")

    def do_PUT(self):
        self._atender("PUT")

    def do_DELETE(self):
        self._atender("DELETE")

    def _atender(self, verbo):
        """Ejecuta la petición y traduce cualquier falla a su código HTTP."""
        try:
            url = urlsplit(self.path)
            partes = [parte for parte in url.path.split("/") if parte]
            consulta = {clave: valores[0] for clave, valores in parse_qs(url.query).items()}
            codigo, cuerpo = self._enrutar(verbo, partes, consulta)
        except ValueError as error:            # dato inválido o faltante
            codigo, cuerpo = 400, {"error": str(error)}
        except (NoEncontrado, RecetaNoEncontrada) as error:
            codigo, cuerpo = 404, {"error": str(error)}
        except MetodoNoPermitido as error:
            codigo, cuerpo = 405, {"error": str(error)}
        except Exception as error:             # cualquier otra cosa: no tirar el servidor
            print(f"  !! error interno: {error!r}")
            codigo, cuerpo = 500, {"error": "Error interno del servicio"}
        self._responder(codigo, cuerpo)

    # ---------- rutas ----------

    def _enrutar(self, verbo, partes, consulta):
        if not partes or partes == ["api"]:
            return self._solo_get(verbo, self._bienvenida)
        if partes == ["api", "estado"]:
            return self._solo_get(verbo, self._estado)
        if partes[:2] == ["api", "operaciones"] and len(partes) == 3:
            return self._solo_get(verbo, lambda: self._operacion(partes[2], consulta))
        if partes == ["api", "recetas"]:
            return self._coleccion_recetas(verbo, consulta)
        if partes[:2] == ["api", "recetas"] and len(partes) == 3:
            return self._una_receta(verbo, partes[2])
        raise NoEncontrado(f"No existe la ruta /{'/'.join(partes)}")

    @staticmethod
    def _solo_get(verbo, accion):
        if verbo != "GET":
            raise MetodoNoPermitido("Esta ruta solo acepta GET")
        return 200, accion()

    def _bienvenida(self):
        return {
            "servicio": "Cocina en red — Práctica 5, Equipo 2",
            "actividad1": [
                "GET /api/operaciones/calorias?gramos=150&kcal100=165",
                "GET /api/operaciones/escalar?cantidad=2&porcionesBase=4&porcionesDeseadas=6",
                "GET /api/operaciones/peso?valor=1&de=lb&a=g",
                "GET /api/operaciones/temperatura?valor=175&de=C&a=F",
            ],
            "actividad2": [
                "GET /api/recetas   (opcional ?buscar=texto)",
                "GET /api/recetas/{id}",
                "POST /api/recetas",
                "PUT /api/recetas/{id}",
                "DELETE /api/recetas/{id}",
            ],
            "estado": "GET /api/estado",
        }

    def _estado(self):
        return {
            "servicio": "Cocina en red",
            "version": "1.0",
            "recetas": recetario.contar(),
            "horaServidor": time.strftime("%Y-%m-%d %H:%M:%S"),
        }

    # ---------- Actividad 1: operaciones simples ----------

    def _operacion(self, nombre, consulta):
        if nombre == "calorias":
            return calculadora.calorias_por_peso(
                self._numero(consulta, "gramos"), self._numero(consulta, "kcal100")
            )
        if nombre == "escalar":
            return calculadora.escalar_porciones(
                self._numero(consulta, "cantidad"),
                self._numero(consulta, "porcionesBase"),
                self._numero(consulta, "porcionesDeseadas"),
            )
        if nombre == "peso":
            return calculadora.convertir_peso(
                self._numero(consulta, "valor"), self._texto(consulta, "de"), self._texto(consulta, "a")
            )
        if nombre == "temperatura":
            return calculadora.convertir_temperatura(
                self._numero(consulta, "valor"), self._texto(consulta, "de"), self._texto(consulta, "a")
            )
        raise NoEncontrado(f"No existe la operación '{nombre}'")

    # ---------- Actividad 2: recetas en la base de datos ----------

    def _coleccion_recetas(self, verbo, consulta):
        if verbo == "GET":
            return 200, recetario.listar(consulta.get("buscar", ""))
        if verbo == "POST":
            return 201, recetario.insertar(self._leer_json())
        raise MetodoNoPermitido("/api/recetas acepta GET y POST")

    def _una_receta(self, verbo, id_texto):
        if not id_texto.isdigit():
            raise ValueError("El id de la receta debe ser un número entero")
        id_receta = int(id_texto)
        if verbo == "GET":
            return 200, recetario.obtener(id_receta)
        if verbo == "PUT":
            return 200, recetario.actualizar(id_receta, self._leer_json())
        if verbo == "DELETE":
            eliminada = recetario.eliminar(id_receta)
            return 200, {"mensaje": f"Se eliminó «{eliminada['nombre']}»", "id": id_receta}
        raise MetodoNoPermitido("/api/recetas/{id} acepta GET, PUT y DELETE")

    # ---------- entrada y salida ----------

    @staticmethod
    def _numero(consulta, nombre):
        if nombre not in consulta:
            raise ValueError(f"Falta el parámetro '{nombre}'")
        try:
            numero = float(consulta[nombre].replace(",", "."))
        except ValueError:
            raise ValueError(f"El parámetro '{nombre}' debe ser un número") from None
        if math.isnan(numero) or math.isinf(numero):
            raise ValueError(f"El parámetro '{nombre}' debe ser un número")
        return numero

    @staticmethod
    def _texto(consulta, nombre):
        if not consulta.get(nombre, "").strip():
            raise ValueError(f"Falta el parámetro '{nombre}'")
        return consulta[nombre]

    def _leer_json(self):
        largo = int(self.headers.get("Content-Length") or 0)
        if largo <= 0:
            raise ValueError("La petición no trae cuerpo JSON")
        if largo > 1_000_000:
            raise ValueError("El cuerpo de la petición es demasiado grande")
        try:
            return json.loads(self.rfile.read(largo).decode("utf-8"))
        except (UnicodeDecodeError, json.JSONDecodeError):
            raise ValueError("El cuerpo no es un JSON válido") from None

    def _responder(self, codigo, cuerpo):
        datos = json.dumps(cuerpo, ensure_ascii=False).encode("utf-8")
        self.send_response(codigo)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(datos)))
        self.end_headers()
        self.wfile.write(datos)

    def log_message(self, formato, *args):
        # Bitácora en la terminal: sirve de evidencia de que la app sí llega al servicio
        print(f"[{time.strftime('%H:%M:%S')}] {self.client_address[0]}  {formato % args}")


def ip_local():
    """IP de esta computadora en la red local (para escribirla en el teléfono)."""
    try:
        with socket.socket(socket.AF_INET, socket.SOCK_DGRAM) as sonda:
            sonda.connect(("10.255.255.255", 1))  # no envía nada; solo elige la interfaz de salida
            return sonda.getsockname()[0]
    except OSError:
        return None


def main():
    global recetario
    sys.stdout.reconfigure(line_buffering=True)  # la bitácora se imprime al instante
    argumentos = argparse.ArgumentParser(description="Servicio web de la Práctica 5")
    argumentos.add_argument("--puerto", type=int, default=8080)
    argumentos.add_argument("--bd", default=str(CARPETA / "recetario.db"), help="archivo SQLite")
    opciones = argumentos.parse_args()

    recetario = RecetarioBD(opciones.bd)
    servidor = ThreadingHTTPServer(("0.0.0.0", opciones.puerto), ManejadorServicio)

    print("Servicio web «Cocina en red» encendido")
    print(f"  Base de datos : {opciones.bd}  ({recetario.contar()} recetas)")
    print(f"  En esta PC    : http://localhost:{opciones.puerto}/")
    print(f"  Emulador      : http://10.0.2.2:{opciones.puerto}/")
    print(f"  Teléfono USB  : http://127.0.0.1:{opciones.puerto}/   (antes: adb reverse tcp:{opciones.puerto} tcp:{opciones.puerto})")
    if ip_local():
        print(f"  Teléfono WiFi : http://{ip_local()}:{opciones.puerto}/")
    print("Ctrl+C para apagarlo\n")
    try:
        servidor.serve_forever()
    except KeyboardInterrupt:
        print("\nServicio apagado")
    finally:
        servidor.server_close()


if __name__ == "__main__":
    main()
