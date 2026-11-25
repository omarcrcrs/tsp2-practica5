"""
Los 5 registros con los que arranca la base de datos del servicio.
Son las mismas recetas de TheMealDB que el equipo usó en la Práctica 3 (Recetario con Room).
"""

RECETAS_INICIALES = [
    {
        "nombre": "Spicy Arrabiata Penne",
        "categoria": "Vegetarian",
        "area": "Italian",
        "imagenUrl": "https://www.themealdb.com/images/media/meals/ustsqw1468250014.jpg",
        "ingredientes": "\n".join([
            "penne rigate · 1 pound",
            "olive oil · 1/4 cup",
            "garlic · 3 cloves",
            "chopped tomatoes · 1 tin",
            "red chilli flakes · 1/2 teaspoon",
            "italian seasoning · 1/2 teaspoon",
            "basil · 6 leaves",
            "Parmigiano-Reggiano · sprinkling",
        ]),
        "instrucciones": (
            "Cocer la pasta en agua con sal unos 9 minutos. Aparte, dorar el ajo en aceite de "
            "oliva, agregar el tomate, el chile en hojuelas y las especias; hervir 5 minutos. Retirar del "
            "fuego, añadir la albahaca y mezclar con la pasta escurrida. Servir con queso parmesano."
        ),
        "calificacion": 4.5,
        "favorita": True,
        "vecesCocinada": 3,
        "notas": "Con la mitad del chile queda perfecta para toda la familia.",
        "fechaGuardado": 1_792_546_200_000,
    },
    {
        "nombre": "Teriyaki Chicken Casserole",
        "categoria": "Chicken",
        "area": "Japanese",
        "imagenUrl": "https://www.themealdb.com/images/media/meals/wvpsxx1468256321.jpg",
        "ingredientes": "\n".join([
            "soy sauce · 3/4 cup",
            "water · 1/2 cup",
            "brown sugar · 1/4 cup",
            "ground ginger · 1/2 teaspoon",
            "minced garlic · 1/2 teaspoon",
            "cornstarch · 4 Tablespoons",
            "chicken breasts · 2",
            "stir-fry vegetables · 1 (12 oz.)",
            "brown rice · 3 cups",
        ]),
        "instrucciones": (
            "Precalentar el horno a 175 °C. Hervir la salsa de soya con agua, azúcar, jengibre y "
            "ajo, y espesar con la fécula. Hornear el pollo bañado en una taza de salsa 35 minutos, "
            "desmenuzarlo y mezclarlo con las verduras cocidas y el arroz. Hornear 15 minutos más."
        ),
        "calificacion": 3.0,
        "favorita": False,
        "vecesCocinada": 1,
        "notas": "",
        "fechaGuardado": 1_792_634_400_000,
    },
    {
        "nombre": "Corba",
        "categoria": "Side",
        "area": "Turkish",
        "imagenUrl": "https://www.themealdb.com/images/media/meals/58oia61564916529.jpg",
        "ingredientes": "\n".join([
            "Lentils · 1 cup",
            "Onion · 1 large",
            "Carrots · 1 large",
            "Tomato Puree · 1 tbs",
            "Cumin · 2 tsp",
            "Paprika · 1 tsp",
            "Mint · 1/2 tsp",
            "Thyme · 1/2 tsp",
            "Black Pepper · 1/4 tsp",
            "Red Pepper Flakes · 1/4 tsp",
            "Vegetable Stock · 4 cups",
            "Water · 1 cup",
            "Sea Salt · Pinch",
        ]),
        "instrucciones": (
            "Sofreír la cebolla con sal, agregar la zanahoria, el puré de tomate y las especias. "
            "Añadir las lentejas lavadas, el agua y el caldo; hervir y cocer tapado a fuego bajo 15 a 20 "
            "minutos hasta que las lentejas se deshagan. Licuar hasta obtener una sopa tersa y rectificar la sal."
        ),
        "calificacion": 5.0,
        "favorita": True,
        "vecesCocinada": 2,
        "notas": "Sopa de lentejas rojas. Sale muy barata y rinde para 6 personas.",
        "fechaGuardado": 1_792_764_000_000,
    },
    {
        "nombre": "Dal fry",
        "categoria": "Vegetarian",
        "area": "Indian",
        "imagenUrl": "https://www.themealdb.com/images/media/meals/wuxrtu1483564410.jpg",
        "ingredientes": "\n".join([
            "Toor dal · 1 cup",
            "Water · 2-1/2 cups",
            "Salt · 1 tsp",
            "Turmeric · 1/4 tsp",
            "Ghee · 3 tbs",
            "Chopped tomatoes · 1 cup",
            "Cumin seeds · 1/2 tsp",
            "Mustard Seeds · 1/2 tsp",
            "Bay Leaf · 2",
            "Green Chilli · 1 tbs chopped",
            "Ginger · 2 tsp shredded",
            "Cilantro · 2 tbs",
            "Red Pepper · 1/2 tsp",
            "Sugar · 1 tsp",
            "Garam Masala · 1/4 tsp",
        ]),
        "instrucciones": (
            "Remojar las lentejas una hora y cocerlas con agua, sal y cúrcuma unos 30 minutos. "
            "En ghee caliente tostar comino y mostaza con laurel, chile verde y jengibre; agregar el tomate, "
            "sal y azúcar hasta que ablande. Incorporar cilantro y garam masala, verter sobre el dal y "
            "hervir unos minutos. Servir con pan naan."
        ),
        "calificacion": 0.0,
        "favorita": False,
        "vecesCocinada": 0,
        "notas": "Pendiente de probar.",
        "fechaGuardado": 1_792_958_400_000,
    },
    {
        "nombre": "Chicken Enchilada Casserole",
        "categoria": "Chicken",
        "area": "Mexican",
        "imagenUrl": "https://www.themealdb.com/images/media/meals/qtuwxu1468233098.jpg",
        "ingredientes": "\n".join([
            "Enchilada sauce · 14 oz jar",
            "Shredded Monterey Jack cheese · 3 Cups",
            "Corn tortillas · 6",
            "Chicken breasts · 2",
        ]),
        "instrucciones": (
            "Cocer el pollo en trozos dentro de la salsa para enchiladas hasta que esté cocido y "
            "desmenuzarlo. En un refractario armar tres capas de tortilla, pollo, salsa y queso. Hornear a "
            "190 °C de 20 a 30 minutos, hasta que burbujee y dore por encima."
        ),
        "calificacion": 4.0,
        "favorita": False,
        "vecesCocinada": 1,
        "notas": "Cambiar el Monterey Jack por queso Oaxaca.",
        "fechaGuardado": 1_793_016_000_000,
    },
]
