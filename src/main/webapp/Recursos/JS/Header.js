// header.js
// Construye el header de la app UNA sola vez (aquí) y lo inyecta en cada
// página dentro de <div id="header-placeholder"></div>.
// Depende de auth.js (debe cargarse antes en el HTML).

/**
 * Devuelve el HTML del header. Los enlaces con data-rol se muestran u
 * ocultan según el rol activo (ver aplicarVisibilidadPorRol). Los enlaces
 * sin data-rol son públicos (siempre visibles, ej. "Inicio", "Libros").
 *
 * NOTA sobre los href: todos apuntan a "#" por ahora. Los .jsp viven dentro
 * de WEB-INF/Frontend, que el navegador no puede pedir directamente; hace
 * falta un Servlet mapeado a una URL limpia (ej. @WebServlet("/catalogo"))
 * que haga forward() al .jsp físico. Hasta que existan esos servlets,
 * ningún enlace es funcional -- ni los que ya tienen .jsp construido ni
 * los pendientes. Cuando se definan las rutas, se reemplaza cada "#" por
 * la ruta real (ej. "/catalogo").
 */
function crearHeaderHTML() {
    return `
        <div class="encabezado-marca">
            <a href="#" class="logo-auth"><!-- Catalogo.jsp -->
                <span class="nombre-marca">WiseBooks</span>
            </a>
        </div>
        <nav class="encabezado-nav">
            <a href="#"><!-- Catalogo.jsp -->Inicio</a>
            <a href="#"><!-- Catalogo.jsp -->Libros</a>

            <a href="#" data-rol="visitante"><!-- Login.jsp -->Iniciar sesión</a>
            <a href="#" data-rol="visitante"><!-- CrearCuenta.jsp -->Crear cuenta</a>

            <a href="#" data-rol="lector"><!-- MisPrestamos.jsp pendiente -->Préstamos</a>
            <a href="#" data-rol="lector"><!-- MisReservas.jsp pendiente -->Reservas</a>
            <a href="#" data-rol="lector"><!-- Favoritos.jsp -->Favoritos</a>

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

/**
 * Muestra u oculta los elementos [data-rol] del header según el rol activo,
 * y llena el correo del usuario / conecta el botón de cerrar sesión.
 */
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

/**
 * Busca el <div id="header-placeholder"> en la página actual, le inyecta
 * el HTML del header y aplica la visibilidad por rol.
 */
function insertarHeader() {
    const contenedor = document.getElementById("header-placeholder");
    if (!contenedor) {
        console.error("No se encontró #header-placeholder en esta página.");
        return;
    }
    contenedor.outerHTML = `<header class="encabezado-app">${crearHeaderHTML()}</header>`;
    aplicarVisibilidadPorRol();
}

// Este script debe cargarse después de auth.js y del <div id="header-placeholder">
// en el <body>, así que el DOM ya está disponible al ejecutarse.
insertarHeader();

