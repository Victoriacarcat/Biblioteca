<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Reservas - Wisebooks</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Bootstrap 5.3.3 (CDN) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Estilos propios -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Recursos/CSS/Styles.css">
</head>
<body>

    <!-- Header.js arma el header según el rol -->
    <div id="header-placeholder"></div>

    <main class="contenedor-reservas container">

        <section class="franja-titulo">
            <div class="icono-circulo">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="icono-svg" aria-hidden="true">
                    <path d="M10.854 7.146a.5.5 0 0 1 0 .708l-3 3a.5.5 0 0 1-.708 0l-1.5-1.5a.5.5 0 1 1 .708-.708L7.5 9.793l2.646-2.647a.5.5 0 0 1 .708 0"/>
                    <path d="M3.5 0a.5.5 0 0 1 .5.5V1h8V.5a.5.5 0 0 1 1 0V1h1a2 2 0 0 1 2 2v11a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V3a2 2 0 0 1 2-2h1V.5a.5.5 0 0 1 .5-.5M1 4v10a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1V4z"/>
                </svg>
            </div>
            <h1 class="titulo-reservas">Reservas</h1>
            <p class="texto-introductorio">Gestiona la disponibilidad de libros a través de reservas.</p>
        </section>

        <!-- Vista Lector: mis reservas -->
        <section class="bloque-reservas-lector" data-rol="lector" hidden>
            <div class="tarjeta-tabla">
                <table class="table table-hover tabla-mis-reservas">
                    <thead>
                        <tr>
                            <th>Libro</th>
                            <th>Fecha</th>
                            <th>Estado</th>
                            <th>Cancelar</th>
                        </tr>
                    </thead>
                    <tbody id="cuerpo-mis-reservas"></tbody>
                </table>
            </div>
        </section>

        <!-- Vista Bibliotecario/Administrador: todas las reservas -->
        <section class="bloque-reservas-staff" data-rol="bibliotecario,administrador" hidden>
            <div class="grupo-filtro">
                <label for="filtro-estado-reserva">Filtrar por estado</label>
                <select id="filtro-estado-reserva" class="form-select campo-filtro">
                    <option value="">Todas</option>
                    <option value="Pendiente">Pendientes</option>
                    <option value="Aprobada">Aprobadas</option>
                    <option value="Vencida">Vencidas</option>
                    <option value="Cancelada">Canceladas</option>
                </select>
            </div>

            <div class="tarjeta-tabla">
                <table class="table table-hover tabla-reservas">
                    <thead>
                        <tr>
                            <th>Usuario</th>
                            <th>Libro</th>
                            <th>Fecha</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody id="cuerpo-reservas"></tbody>
                </table>
            </div>
        </section>

    </main>

    <!-- Scripts: sesión/rol -> header -> datos -> lógica de reservas -->
    <script>
    const contextPath = "${pageContext.request.contextPath}";
    </script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Auth.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Header.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Iconos.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Data.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/CeldasTabla.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Reservas.js"></script>
</body>
</html>
