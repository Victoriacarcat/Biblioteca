// header.js
// Construye el header de la app UNA sola vez (aquí) y lo inyecta en cada
// página dentro de <div id="header-placeholder"></div>.
// Depende de auth.js (debe cargarse antes en el HTML) y de la variable
// global `contextPath` (definida en un <script> dentro de cada .jsp,
// justo antes de cargar Auth.js).

function crearHeaderHTML() {
    return `
        <div class="encabezado-marca">
            <a href="${contextPath}/FrontEnd/Catalogo/Catalogo.jsp" class="logo-auth">
                <span class="nombre-marca">WiseBooks</span>
            </a>
        </div>
        <nav class="encabezado-nav">
            <a href="${contextPath}/index.html">Inicio</a>
            <a href="${contextPath}/FrontEnd/Catalogo/Catalogo.jsp">Libros</a>

            <a href="${contextPath}/FrontEnd/Auth/Login.jsp" class="btn btn-outline-primary btn-sm" data-rol="visitante">Iniciar sesión</a>
            <a href="${contextPath}/FrontEnd/Auth/CrearCuenta.jsp" class="btn btn-primary btn-sm" data-rol="visitante">Crear cuenta</a>

            <a href="#" data-rol="lector"><!-- MisPrestamos.jsp pendiente -->Préstamos</a>
            <a href="#" data-rol="lector"><!-- MisReservas.jsp pendiente -->Reservas</a>
            <a href="${contextPath}/FrontEnd/Favoritos/Favoritos.jsp" data-rol="lector">Favoritos</a>

            <a href="#" data-rol="bibliotecario,administrador"><!-- Prestamos.jsp pendiente -->Préstamos</a>
            <a href="#" data-rol="bibliotecario,administrador"><!-- Reservas.jsp pendiente -->Reservas</a>
            <a href="#" data-rol="bibliotecario,administrador"><!-- Devoluciones.jsp pendiente -->Devoluciones</a>
            <a href="#" data-rol="bibliotecario,administrador"><!-- Inventario.jsp pendiente -->Inventario</a>

            <a href="#" data-rol="administrador"><!-- Libros.jsp pendiente -->Libros</a>
            <a href="#" data-rol="administrador"><!-- Usuarios.jsp pendiente -->Usuarios</a>
            <a href="#" data-rol="administrador"><!-- Reportes.jsp pendiente -->Reportes</a>

            <div class="encabezado-usuario" data-rol="lector,bibliotecario,administrador">
                <span class="encabezado-usuario-icono" aria-hidden="true">Icono</span>
                <span class="encabezado-usuario-nombre" id="encabezado-nombre-usuario"></span>
                <button type="button" id="btn-cerrar-sesion" class="btn btn-outline-secondary btn-sm">Cerrar sesión</button>
            </div>
        </nav>
    `;
}

function aplicarVisibilidadPorRol() {
    const rolActual = obtenerRolActual();

    document.querySelectorAll("[data-rol]").forEach(function (elemento) {
        const rolesPermitidos = elemento.getAttribute("data-rol").split(",");
        elemento.hidden = !rolesPermitidos.includes(rolActual);
    });

    const nombreUsuarioEl = document.getElementById("encabezado-nombre-usuario");
    if (nombreUsuarioEl) {
        nombreUsuarioEl.textContent = rolActual !== "visitante" ? obtenerCorreoActual() : "";
    }

    const botonCerrarSesion = document.getElementById("btn-cerrar-sesion");
    if (botonCerrarSesion) {
        botonCerrarSesion.addEventListener("click", cerrarSesion);
    }
}

function insertarHeader() {
    const contenedor = document.getElementById("header-placeholder");
    if (!contenedor) {
        console.error("No se encontró #header-placeholder en esta página.");
        return;
    }
    contenedor.outerHTML = `<header class="encabezado-app">${crearHeaderHTML()}</header>`;
    aplicarVisibilidadPorRol();
}

insertarHeader();