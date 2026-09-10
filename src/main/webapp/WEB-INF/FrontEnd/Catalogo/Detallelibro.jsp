<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Detalle del libro - Wisebooks</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Bootstrap 5.3.3 (CDN) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Estilos propios -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Recursos/CSS/Styles.css">
</head>
<body>

    <!-- Pantalla pública: Header.js igual la arma según el rol actual (o Visitante) -->
    <div id="header-placeholder"></div>

    <main class="contenedor-detalle-libro container">

        <div class="fila-detalle-libro row">

            <!-- Parte izquierda: portada grande -->
            <div class="col-md-4 columna-portada">
                <img
                    class="portada-grande"
                    src="https://via.placeholder.com/300x440?text=Portada"
                    alt="Portada del libro">
            </div>

            <!-- Parte derecha: información principal -->
            <div class="col-md-8 columna-info-libro">
                <h1 class="titulo-libro-detalle">Título del libro</h1>
                <p class="autor-libro-detalle">Nombre del autor</p>
                <p class="categoria-libro-detalle">Categoría</p>
                <p class="editorial-libro-detalle">Editorial: Nombre de la editorial</p>
                <p class="fecha-libro-detalle">Año: 0000</p>
                <p class="isbn-libro-detalle">ISBN: 000-0000000000</p>
                <p class="disponibilidad-libro-detalle disponible">🟢 Disponible</p>

                <!-- Botones según rol -->
                <div class="bloque-botones-rol">
                    <button class="btn btn-secondary boton-iniciar-sesion-reservar" data-rol="Visitante" hidden>
                        Iniciar sesión para reservar
                    </button>

                    <button class="btn btn-primary boton-reservar" data-rol="Lector" hidden>
                        Reservar
                    </button>
                    <button class="btn btn-outline-secondary boton-favoritos" data-rol="Lector" hidden>
                        ♡ Añadir a favoritos
                    </button>

                    <button class="btn btn-outline-secondary boton-gestionar-ejemplares" data-rol="Bibliotecario,Administrador" hidden>
                        Gestionar ejemplares
                    </button>

                    <button class="btn btn-outline-secondary boton-editar-libro" data-rol="Administrador" hidden>
                        Editar libro
                    </button>
                </div>
            </div>
        </div>

        <!-- Descripción -->
        <section class="seccion-descripcion">
            <h2>Descripción</h2>
            <p class="texto-descripcion">
                Sinopsis / descripción del libro...
            </p>
        </section>

        <!-- Información del ejemplar -->
        <section class="seccion-info-ejemplar">
            <h2>Información del ejemplar</h2>
            <ul class="lista-info-ejemplar">
                <li>Número de ejemplares: 0</li>
                <li>Disponibles: 0</li>
                <li>Prestados: 0</li>
                <li>Ubicación: Ubicación del ejemplar</li>
                <li>Estado: Estado del ejemplar</li>
            </ul>
        </section>

    </main>

    <!-- Scripts: sesión/rol -> header -> visibilidad de botones por rol -->
    <script src="${pageContext.request.contextPath}/Recursos/JS/Auth.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Header.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/DetalleLibro.js"></script>
</body>
</html>

