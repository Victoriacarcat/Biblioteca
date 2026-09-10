// header.js
// Construye el header de la app UNA sola vez (aquí) y lo inyecta en cada
// página dentro de <div id="header-placeholder"></div>.
// Depende de auth.js (debe cargarse antes en el HTML).

/**
 * Devuelve el HTML del header. Los enlaces con data-rol se muestran u
 * ocultan según el rol activo (ver aplicarVisibilidadPorRol). Los enlaces
 * sin data-rol son públicos (siempre visibles, ej. "Inicio", "Libros").
 */
function crearHeaderHTML() {
    return `
        <div class="encabezado-marca">
            <a href="catalogo.html" class="logo-auth">
                <span class="nombre-marca">WiseBooks</span>
            </a>
        </div>
        <nav class="encabezado-nav">
            <a href="catalogo.html">Inicio</a>
            <a href="catalogo.html">Libros</a>

            <a href="login.html" data-rol="visitante">Iniciar sesión</a>
            <a href="registro.html" data-rol="visitante">Crear cuenta</a>

            <a href="mis-prestamos.html" data-rol="lector">Préstamos</a>
            <a href="reservas.html" data-rol="lector">Reservas</a>
            <a href="favoritos.html" data-rol="lector">Favoritos</a>

            <a href="prestamos.html" data-rol="bibliotecario">Préstamos</a>
            <a href="reservas.html" data-rol="bibliotecario">Reservas</a>
            <a href="devoluciones.html" data-rol="bibliotecario">Devoluciones</a>
            <a href="inventario.html" data-rol="bibliotecario">Inventario</a>

            <!-- TODO: definir enlaces de Administrador (pendiente de decisión) -->

            <div class="encabezado-usuario" data-rol="lector,bibliotecario,administrador">
                <span class="encabezado-usuario-icono" aria-hidden="true">👤</span>
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


