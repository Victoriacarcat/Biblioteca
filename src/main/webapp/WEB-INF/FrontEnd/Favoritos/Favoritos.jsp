<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Mis favoritos - Wisebooks</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <!-- Bootstrap 5.3.3 (CDN) -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

        <!-- Estilos propios -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Recursos/CSS/Styles.css">
    </head>
    <body>

        <!-- Pantalla principalmente para Lector -->
        <div id="header-placeholder"></div>

        <main class="contenedor-favoritos container">

            <h1 class="titulo-favoritos">Mis favoritos</h1>

            <!-- Buscador dentro de favoritos -->
            <div class="bloque-buscador-favoritos">
                <input
                    type="text"
                    id="campo-busqueda-favoritos"
                    class="campo-busqueda"
                    placeholder="Buscar en favoritos 🔍">
            </div>

            <!-- Mensaje para cuando no hay favoritos -->
            <p id="mensaje-sin-favoritos" class="mensaje-sin-favoritos" hidden>
                Aún no tienes libros favoritos.
            </p>

            <!-- Aquí Favoritos.js inserta las cards de libros favoritos -->
            <div id="contenedor-favoritos" class="contenedor-favoritos row"></div>

        </main>

        <!-- Scripts: sesión/rol -> header -> datos -> lógica de favoritos -->
        <script src="${pageContext.request.contextPath}/Recursos/JS/Auth.js"></script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Header.js"></script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Data.js"></script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Favoritos.js"></script>
    </body>
</html>