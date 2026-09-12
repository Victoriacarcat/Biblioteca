<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Inventario - Wisebooks</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Bootstrap 5.3.3 (CDN) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Estilos propios -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Recursos/CSS/Styles.css">
</head>
<body>

    <!-- Vista de Bibliotecario/Administrador. "Eliminar" un ejemplar es
         exclusivo del Administrador (ver data-rol en Inventario.js);
         Bibliotecario solo puede darlo de baja. -->
    <div id="header-placeholder"></div>

    <main class="contenedor-inventario container">

        <section class="franja-titulo">
            <div class="icono-circulo">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="icono-svg" aria-hidden="true">
                    <path d="M8.186 1.113a.5.5 0 0 0-.372 0L1.846 3.5l2.404.961L10.404 2zm3.564 1.426L5.596 5 8 5.961 14.154 3.5zm3.25 1.7-6.5 2.6v7.922l6.5-2.6V4.24zM7.5 14.762V6.838L1 4.239v7.923zM7.443.184a1.5 1.5 0 0 1 1.114 0l7.129 2.852A.5.5 0 0 1 16 3.5v8.662a1 1 0 0 1-.629.928l-7.185 2.874a.5.5 0 0 1-.372 0L.63 13.09a1 1 0 0 1-.63-.928V3.5a.5.5 0 0 1 .314-.464z"/>
                </svg>
            </div>
            <h1 class="titulo-inventario">Inventario</h1>
            <p class="texto-introductorio">Ejemplares físicos y digitales de cada libro del catálogo.</p>
        </section>

        <div class="fila-metricas row">
            <div class="col-md-3 tarjeta-metrica">
                <h2 class="valor-metrica" id="metrica-total-ejemplares">0</h2>
                <p class="etiqueta-metrica">Ejemplares totales</p>
            </div>
            <div class="col-md-3 tarjeta-metrica">
                <h2 class="valor-metrica" id="metrica-ejemplares-disponibles">0</h2>
                <p class="etiqueta-metrica">Disponibles</p>
            </div>
            <div class="col-md-3 tarjeta-metrica">
                <h2 class="valor-metrica" id="metrica-ejemplares-prestados">0</h2>
                <p class="etiqueta-metrica">Prestados</p>
            </div>
            <div class="col-md-3 tarjeta-metrica">
                <h2 class="valor-metrica" id="metrica-ejemplares-fuera-servicio">0</h2>
                <p class="etiqueta-metrica">Fuera de servicio</p>
            </div>
        </div>

        <div class="error-form" id="inventario-error" hidden></div>

        <section class="bloque-agregar-ejemplar">
            <h2 class="titulo-seccion-form">Agregar ejemplar</h2>
            <form id="form-agregar-ejemplar" class="row g-2" novalidate>
                <div class="col-md-5">
                    <label for="ejemplar-libro">Libro</label>
                    <select id="ejemplar-libro" class="form-select" required>
                        <option value="" disabled selected>Selecciona un libro</option>
                    </select>
                </div>
                <div class="col-md-4">
                    <label for="ejemplar-codigo">Código de inventario</label>
                    <input type="text" id="ejemplar-codigo" class="form-control" placeholder="Ej. COL-003">
                </div>
                <div class="col-md-3 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary w-100">Agregar</button>
                </div>
            </form>
        </section>

        <div class="bloque-buscador row">
            <div class="col-md-6">
                <input type="text" id="buscar-ejemplar" class="form-control" placeholder="Buscar por libro o código...">
            </div>
            <div class="col-md-6">
                <select id="filtro-estado-ejemplar" class="form-select campo-filtro">
                    <option value="">Todos los estados</option>
                    <option value="Disponible">Disponible</option>
                    <option value="Prestado">Prestado</option>
                    <option value="Reservado">Reservado</option>
                    <option value="Dañado">Dañado</option>
                    <option value="De baja">De baja</option>
                </select>
            </div>
        </div>

        <div class="tarjeta-tabla">
            <table class="table table-hover tabla-inventario">
                <thead>
                    <tr>
                        <th>Código</th>
                        <th>Libro</th>
                        <th>Estado</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody id="cuerpo-inventario"></tbody>
            </table>
        </div>

    </main>

    <!-- Scripts: sesión/rol -> header -> datos -> lógica de inventario -->
    <script>
    const contextPath = "${pageContext.request.contextPath}";
    </script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Auth.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Header.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Iconos.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Data.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Inventario.js"></script>
</body>
</html>
