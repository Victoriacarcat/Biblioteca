<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Sanciones - Wisebooks</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Bootstrap 5.3.3 (CDN) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Estilos propios -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Recursos/CSS/Styles.css">
</head>
<body>

    <!-- Header.js arma el header según el rol -->
    <div id="header-placeholder"></div>

    <main class="contenedor-sanciones container">

        <section class="franja-titulo">
            <div class="icono-circulo icono-circulo-alerta">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="icono-svg" aria-hidden="true">
                    <path d="M7.938 2.016A.13.13 0 0 1 8.002 2a.13.13 0 0 1 .063.016.15.15 0 0 1 .054.057l6.857 11.667c.036.06.035.124.002.183a.2.2 0 0 1-.054.06.1.1 0 0 1-.066.017H1.146a.1.1 0 0 1-.066-.017.2.2 0 0 1-.054-.06.18.18 0 0 1 .002-.183L7.884 2.073a.15.15 0 0 1 .054-.057m1.044-.45a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767z"/>
                    <path d="M7.002 12a1 1 0 1 1 2 0 1 1 0 0 1-2 0M7.1 5.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0z"/>
                </svg>
            </div>
            <h1 class="titulo-sanciones">Sanciones</h1>
            <p class="texto-introductorio">Multas por devoluciones tardías o libros dañados.</p>
        </section>

        <!-- Vista Lector: mis sanciones -->
        <section class="bloque-sanciones-lector" data-rol="lector" hidden>
            <div class="tarjeta-tabla">
                <table class="table table-hover tabla-mis-sanciones">
                    <thead>
                        <tr>
                            <th>Motivo</th>
                            <th>Estado</th>
                            <th>Valor</th>
                        </tr>
                    </thead>
                    <tbody id="cuerpo-mis-sanciones"></tbody>
                </table>
            </div>
        </section>

        <!-- Vista Bibliotecario/Administrador: usuarios sancionados -->
        <section class="bloque-sanciones-staff" data-rol="bibliotecario,administrador" hidden>
            <div class="tarjeta-tabla">
                <table class="table table-hover tabla-sanciones">
                    <thead>
                        <tr>
                            <th>Usuario</th>
                            <th>Motivo</th>
                            <th>Fecha</th>
                            <th>Estado</th>
                            <th>Valor</th>
                            <th>Gestionar</th>
                        </tr>
                    </thead>
                    <tbody id="cuerpo-sanciones"></tbody>
                </table>
            </div>
        </section>

    </main>

    <!-- Scripts: sesión/rol -> header -> datos -> lógica de sanciones -->
    <script>
    const contextPath = "${pageContext.request.contextPath}";
    </script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Auth.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Header.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Iconos.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Data.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/CeldasTabla.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Sanciones.js"></script>
</body>
</html>
