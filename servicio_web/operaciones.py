"""
Práctica 5 · Actividad 1 — Servicio web simple.

Operaciones de cocina que resuelve el servicio. Cada método encapsula UNA
operación: recibe números, valida y regresa el resultado. Aquí no hay nada de
HTTP ni de JSON; eso lo hace servidor.py. Así la lógica se puede probar sola
y el servidor solo se encarga de recibir peticiones y contestar.
"""


def _texto(numero):
    """12.50 -> '12.5' · 3.00 -> '3' (para armar el detalle de la operación)."""
    return ("%.2f" % numero).rstrip("0").rstrip(".")


class CalculadoraCocina:
    """Métodos del servicio web simple (Actividad 1)."""

    # Cuántos gramos vale una unidad de cada tipo
    GRAMOS_POR_UNIDAD = {"g": 1.0, "kg": 1000.0, "oz": 28.349523125, "lb": 453.59237}
    ESCALAS_TEMPERATURA = ("C", "F")

    def calorias_por_peso(self, gramos, kcal_por_100g):
        """Calorías de un ingrediente según lo que pesa (la operación base de FARINAM)."""
        if gramos < 0 or kcal_por_100g < 0:
            raise ValueError("Los gramos y las kcal por 100 g no pueden ser negativos")
        kcal = gramos * kcal_por_100g / 100
        return self._respuesta(
            "calorias", kcal, "kcal",
            f"{_texto(gramos)} g × {_texto(kcal_por_100g)} kcal ÷ 100 g = {_texto(kcal)} kcal",
        )

    def escalar_porciones(self, cantidad, porciones_base, porciones_deseadas):
        """Ajusta la cantidad de un ingrediente a otro número de porciones (regla de tres)."""
        if cantidad < 0:
            raise ValueError("La cantidad no puede ser negativa")
        if porciones_base <= 0 or porciones_deseadas <= 0:
            raise ValueError("Las porciones deben ser mayores que cero")
        nueva = cantidad * porciones_deseadas / porciones_base
        return self._respuesta(
            "escalar", nueva, "",
            f"{_texto(cantidad)} × {_texto(porciones_deseadas)} ÷ {_texto(porciones_base)} = {_texto(nueva)}",
        )

    def convertir_peso(self, valor, de, a):
        """Convierte entre g, kg, oz y lb (TheMealDB da muchas medidas en onzas y libras)."""
        de, a = de.strip().lower(), a.strip().lower()
        for unidad in (de, a):
            if unidad not in self.GRAMOS_POR_UNIDAD:
                validas = ", ".join(self.GRAMOS_POR_UNIDAD)
                raise ValueError(f"Unidad de peso desconocida: '{unidad}'. Usa: {validas}")
        if valor < 0:
            raise ValueError("El peso no puede ser negativo")
        convertido = valor * self.GRAMOS_POR_UNIDAD[de] / self.GRAMOS_POR_UNIDAD[a]
        return self._respuesta(
            "peso", convertido, a,
            f"{_texto(valor)} {de} = {_texto(convertido)} {a}",
        )

    def convertir_temperatura(self, valor, de, a):
        """Convierte la temperatura del horno entre °C y °F."""
        de, a = de.strip().upper(), a.strip().upper()
        for escala in (de, a):
            if escala not in self.ESCALAS_TEMPERATURA:
                raise ValueError(f"Escala desconocida: '{escala}'. Usa: C o F")
        if de == a:
            convertido = valor
        elif de == "C":
            convertido = valor * 9 / 5 + 32
        else:
            convertido = (valor - 32) * 5 / 9
        return self._respuesta(
            "temperatura", convertido, f"°{a}",
            f"{_texto(valor)} °{de} = {_texto(convertido)} °{a}",
        )

    @staticmethod
    def _respuesta(operacion, resultado, unidad, detalle):
        """Todas las operaciones contestan con la misma forma (un solo modelo en la app)."""
        return {
            "operacion": operacion,
            "resultado": round(resultado, 2),
            "unidad": unidad,
            "detalle": detalle,
        }
