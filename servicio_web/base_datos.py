"""
Práctica 5 · Actividad 2 — Servicio web basado en base de datos.

La base de datos (SQLite, la misma familia que usa Room en la Práctica 3) ahora
vive en el servidor. La app ya no la toca directamente: todo pasa por el
servicio web. Esta clase es la única que escribe SQL.
"""

import sqlite3
import threading
import time

from recetas_iniciales import RECETAS_INICIALES


class RecetaNoEncontrada(Exception):
    """Se pidió un id que no está en la tabla."""


class RecetarioBD:
    """Operaciones sobre la tabla `recetas`: crear, leer, actualizar y eliminar."""

    # nombre del campo en el JSON (igual que en la data class de Kotlin) -> columna en SQLite
    COLUMNAS = {
        "nombre": "nombre",
        "categoria": "categoria",
        "area": "area",
        "imagenUrl": "imagen_url",
        "ingredientes": "ingredientes",
        "instrucciones": "instrucciones",
        "calificacion": "calificacion",
        "favorita": "favorita",
        "vecesCocinada": "veces_cocinada",
        "notas": "notas",
        "fechaGuardado": "fecha_guardado",
    }
    CAMPOS_DE_TEXTO = ("nombre", "categoria", "area", "imagenUrl", "ingredientes", "instrucciones", "notas")

    def __init__(self, ruta):
        self.ruta = ruta
        self._candado = threading.Lock()  # una sola escritura a la vez (el servidor atiende en varios hilos)
        self._crear_tabla()

    # ---------- conexión y esquema ----------

    def _conectar(self):
        conexion = sqlite3.connect(self.ruta)
        conexion.row_factory = sqlite3.Row  # permite leer cada columna por su nombre
        return conexion

    def _crear_tabla(self):
        """Crea la tabla si no existe y, si está vacía, inserta los 5 registros iniciales."""
        with self._candado:
            conexion = self._conectar()
            try:
                conexion.execute(
                    """
                    CREATE TABLE IF NOT EXISTS recetas (
                        id             INTEGER PRIMARY KEY AUTOINCREMENT,
                        nombre         TEXT    NOT NULL,
                        categoria      TEXT    NOT NULL DEFAULT '',
                        area           TEXT    NOT NULL DEFAULT '',
                        imagen_url     TEXT    NOT NULL DEFAULT '',
                        ingredientes   TEXT    NOT NULL DEFAULT '',
                        instrucciones  TEXT    NOT NULL DEFAULT '',
                        calificacion   REAL    NOT NULL DEFAULT 0,
                        favorita       INTEGER NOT NULL DEFAULT 0,
                        veces_cocinada INTEGER NOT NULL DEFAULT 0,
                        notas          TEXT    NOT NULL DEFAULT '',
                        fecha_guardado INTEGER NOT NULL
                    )
                    """
                )
                vacia = conexion.execute("SELECT COUNT(*) FROM recetas").fetchone()[0] == 0
                if vacia:
                    for receta in RECETAS_INICIALES:
                        self._insertar(conexion, self._validar(receta, completa=True))
                conexion.commit()
            finally:
                conexion.close()

    # ---------- lectura (SELECT) ----------

    def contar(self):
        conexion = self._conectar()
        try:
            return conexion.execute("SELECT COUNT(*) FROM recetas").fetchone()[0]
        finally:
            conexion.close()

    def listar(self, buscar=""):
        """Todas las recetas (favoritas primero). Con `buscar`, filtra por nombre, categoría u origen."""
        conexion = self._conectar()
        try:
            patron = f"%{buscar.strip()}%"
            filas = conexion.execute(
                """
                SELECT * FROM recetas
                WHERE nombre LIKE ? OR categoria LIKE ? OR area LIKE ?
                ORDER BY favorita DESC, nombre COLLATE NOCASE
                """,
                (patron, patron, patron),
            ).fetchall()
            return [self._a_json(fila) for fila in filas]
        finally:
            conexion.close()

    def obtener(self, id_receta):
        """Un solo registro por su llave primaria."""
        conexion = self._conectar()
        try:
            return self._buscar(conexion, id_receta)
        finally:
            conexion.close()

    # ---------- escritura (INSERT, UPDATE, DELETE) ----------

    def insertar(self, datos):
        valores = self._validar(datos, completa=True)
        with self._candado:
            conexion = self._conectar()
            try:
                nuevo_id = self._insertar(conexion, valores)
                conexion.commit()
                return self._buscar(conexion, nuevo_id)
            finally:
                conexion.close()

    def actualizar(self, id_receta, datos):
        """Cambia solo los campos que vengan en `datos`; los demás se quedan como estaban."""
        valores = self._validar(datos, completa=False)
        with self._candado:
            conexion = self._conectar()
            try:
                self._buscar(conexion, id_receta)  # lanza RecetaNoEncontrada si no existe
                if valores:
                    asignaciones = ", ".join(f"{columna} = ?" for columna in valores)
                    conexion.execute(
                        f"UPDATE recetas SET {asignaciones} WHERE id = ?",
                        (*valores.values(), id_receta),
                    )
                    conexion.commit()
                return self._buscar(conexion, id_receta)
            finally:
                conexion.close()

    def eliminar(self, id_receta):
        with self._candado:
            conexion = self._conectar()
            try:
                receta = self._buscar(conexion, id_receta)
                conexion.execute("DELETE FROM recetas WHERE id = ?", (id_receta,))
                conexion.commit()
                return receta
            finally:
                conexion.close()

    # ---------- apoyo ----------

    def _buscar(self, conexion, id_receta):
        fila = conexion.execute("SELECT * FROM recetas WHERE id = ?", (id_receta,)).fetchone()
        if fila is None:
            raise RecetaNoEncontrada(f"No existe la receta con id {id_receta}")
        return self._a_json(fila)

    @staticmethod
    def _insertar(conexion, valores):
        columnas = ", ".join(valores)
        marcas = ", ".join("?" for _ in valores)
        cursor = conexion.execute(
            f"INSERT INTO recetas ({columnas}) VALUES ({marcas})", tuple(valores.values())
        )
        return cursor.lastrowid

    def _validar(self, datos, completa):
        """
        Revisa lo que mandó el cliente y lo regresa como {columna: valor}.
        completa=True  -> alta: el nombre es obligatorio y se pone la fecha de guardado.
        completa=False -> cambio: solo se revisan los campos recibidos.
        Los nombres de columna salen de COLUMNAS, nunca del texto que mandó el cliente.
        """
        if not isinstance(datos, dict):
            raise ValueError("El cuerpo debe ser un objeto JSON con los datos de la receta")

        valores = {}
        for campo in self.CAMPOS_DE_TEXTO:
            if campo in datos and datos[campo] is not None:
                if not isinstance(datos[campo], str):
                    raise ValueError(f"El campo '{campo}' debe ser texto")
                valores[self.COLUMNAS[campo]] = datos[campo].strip()

        if "nombre" in datos or completa:
            if not valores.get("nombre"):
                raise ValueError("La receta necesita un nombre")

        if datos.get("calificacion") is not None:
            calificacion = datos["calificacion"]
            if isinstance(calificacion, bool) or not isinstance(calificacion, (int, float)):
                raise ValueError("La calificación debe ser un número")
            if not 0 <= calificacion <= 5:
                raise ValueError("La calificación va de 0 a 5 estrellas")
            valores["calificacion"] = float(calificacion)

        if datos.get("favorita") is not None:
            if not isinstance(datos["favorita"], bool):
                raise ValueError("El campo 'favorita' debe ser true o false")
            valores["favorita"] = 1 if datos["favorita"] else 0

        if datos.get("vecesCocinada") is not None:
            veces = datos["vecesCocinada"]
            if isinstance(veces, bool) or not isinstance(veces, int) or veces < 0:
                raise ValueError("'vecesCocinada' debe ser un entero mayor o igual a cero")
            valores["veces_cocinada"] = veces

        fecha = datos.get("fechaGuardado")
        if isinstance(fecha, int) and not isinstance(fecha, bool) and fecha > 0:
            valores["fecha_guardado"] = fecha
        elif completa:
            valores["fecha_guardado"] = int(time.time() * 1000)  # milisegundos, como en Kotlin

        return valores

    @staticmethod
    def _a_json(fila):
        """Fila de SQLite -> diccionario con los mismos nombres que la data class Receta de la app."""
        return {
            "id": fila["id"],
            "nombre": fila["nombre"],
            "categoria": fila["categoria"],
            "area": fila["area"],
            "imagenUrl": fila["imagen_url"],
            "ingredientes": fila["ingredientes"],
            "instrucciones": fila["instrucciones"],
            "calificacion": fila["calificacion"],
            "favorita": bool(fila["favorita"]),
            "vecesCocinada": fila["veces_cocinada"],
            "notas": fila["notas"],
            "fechaGuardado": fila["fecha_guardado"],
        }
