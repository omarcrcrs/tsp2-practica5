package com.example.recetariows.model

// Las 5 recetas que se suben a Firestore con el botón "Cargar recetas".
//
// IMÁGENES: sube una foto de cada platillo a https://imgbb.com, copia el
// "Enlace directo" (empieza con https://i.ibb.co/...) y pégalo en imageRes.
// Si lo dejas vacío, la app muestra un icono de tenedor y cuchillo.
object RecetasIniciales {
    val recetas = listOf(
        DailyFood(
            day = 1,
            title = "Tacos",
            description = "Calienta tortillas de maíz en el comal. Rellénalas con carne " +
                    "asada picada, cebolla y cilantro. Sirve con salsa verde y unas gotas de limón.",
            imageRes = ""
        ),
        DailyFood(
            day = 2,
            title = "Quesadillas",
            description = "Pon queso Oaxaca deshebrado sobre una tortilla en el comal y dóblala " +
                    "a la mitad. Voltea hasta que el queso se derrita. Puedes agregar flor de " +
                    "calabaza o champiñones.",
            imageRes = ""
        ),
        DailyFood(
            day = 3,
            title = "Sopes",
            description = "Forma bolitas de masa, aplánalas y cuécelas en el comal. Pellizca la " +
                    "orilla para hacer un borde. Úntales frijoles refritos y agrega lechuga, " +
                    "queso fresco, crema y salsa.",
            imageRes = ""
        ),
        DailyFood(
            day = 4,
            title = "Tlacoyos",
            description = "Rellena una tortilla gruesa de masa con frijol o haba y dale forma " +
                    "ovalada. Cuécela en el comal por ambos lados. Encima pon nopales, cebolla, " +
                    "queso y salsa.",
            imageRes = ""
        ),
        DailyFood(
            day = 5,
            title = "Tostadas",
            description = "Sobre una tostada crujiente unta frijoles refritos. Agrega pollo " +
                    "deshebrado, lechuga, jitomate, crema y queso. Termina con salsa al gusto.",
            imageRes = ""
        )
    )
}
