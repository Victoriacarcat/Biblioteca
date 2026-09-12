<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Mis favoritos - Wisebooks</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Recursos/CSS/Styles.css">
    </head>
    <body>

        <div id="header-placeholder"></div>

        <main class="pagina-favoritos container">

            <h1 class="titulo-favoritos">Mis favoritos</h1>

            <div class="bloque-buscador-favoritos">
                <input
                    type="text"
                    id="campo-busqueda-favoritos"
                    class="campo-busqueda"
                    placeholder="Buscar en favoritos">
            </div>

            <p id="mensaje-sin-favoritos" class="mensaje-sin-favoritos" hidden>
                Aún no tienes libros favoritos.
            </p>

            <div id="contenedor-favoritos" class="contenedor-favoritos row"></div>

        </main>

        <script>
            const contextPath = "${pageContext.request.contextPath}";
        </script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Auth.js"></script>
        <script>
            // Guard de rol a nivel de página: Favoritos es exclusivo de "lector".
            (function verificarAccesoFavoritos() {
                if (obtenerRolActual() !== "lector") {
                    window.location.href = contextPath + "/FrontEnd/Catalogo/Catalogo.jsp";
                }
            })();
        </script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Header.js"></script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Data.js"></script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Favoritos.js"></script>
    </body>
</html>