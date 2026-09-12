// header.js
// Construye el header de la app UNA sola vez (aquí) y lo inyecta en cada
// página dentro de <div id="header-placeholder"></div>.
// Depende de auth.js (debe cargarse antes en el HTML) y de la variable
// global `contextPath` (definida en un <script> dentro de cada .jsp,
// justo antes de cargar Auth.js).

function crearHeaderHTML() {
    return `
        <div class="encabezado-marca">
            <a href="${contextPath}/FrontEnd/Home/Home.jsp" class="logo-auth">
                <img
                    src="${contextPath}/Recursos/Imagenes/Logo.jpeg"
                    alt="WiseBooks"
                    class="logo-wisebooks">
            </a>
        </div>
        <nav class="encabezado-nav">
            <a href="${contextPath}/FrontEnd/Home/Home.jsp">Inicio</a>
            <a href="${contextPath}/FrontEnd/Dashboard/Dashboard.jsp" data-rol="lector,bibliotecario,administrador">Dashboard</a>
            <a href="${contextPath}/FrontEnd/Catalogo/Catalogo.jsp">Catálogo</a>

            <a href="${contextPath}/FrontEnd/Auth/Login.jsp" class="btn btn-outline-primary btn-sm" data-rol="visitante">Iniciar sesión</a>
            <a href="${contextPath}/FrontEnd/Auth/CrearCuenta.jsp" class="btn btn-primary btn-sm" data-rol="visitante">Crear cuenta</a>

            <!-- Lector: 5 interfaces (Dashboard y Catálogo ya están arriba) -->
            <details class="encabezado-dropdown" data-rol="lector">
                <summary>Mis préstamos y reservas</summary>
                <div class="encabezado-dropdown-menu">
                    <a href="${contextPath}/FrontEnd/Prestamos/MisPrestamos.jsp">Préstamos</a>
                    <a href="${contextPath}/FrontEnd/Reservas/Reservas.jsp">Reservas</a>
                </div>
            </details>
            <details class="encabezado-dropdown" data-rol="lector">
                <summary>Mi cuenta</summary>
                <div class="encabezado-dropdown-menu">
                    <a href="${contextPath}/FrontEnd/Favoritos/Favoritos.jsp">Favoritos</a>
                    <a href="${contextPath}/FrontEnd/Historial/Historial.jsp">Historial</a>
                    <a href="${contextPath}/FrontEnd/Sanciones/Sanciones.jsp">Sanciones</a>
                </div>
            </details>

            <!-- Bibliotecario: 4 interfaces (Dashboard y Catálogo/Libros ya están arriba) -->
            <details class="encabezado-dropdown" data-rol="bibliotecario">
                <summary>Gestión de circulación</summary>
                <div class="encabezado-dropdown-menu">
                    <a href="${contextPath}/FrontEnd/Prestamos/Prestamos.jsp">Préstamos</a>
                    <a href="${contextPath}/FrontEnd/Devoluciones/Devoluciones.jsp">Devoluciones</a>
                    <a href="${contextPath}/FrontEnd/Reservas/Reservas.jsp">Reservas</a>
                    <a href="${contextPath}/FrontEnd/Sanciones/Sanciones.jsp">Sanciones</a>
                </div>
            </details>
            <a href="${contextPath}/FrontEnd/Inventario/Inventario.jsp" data-rol="bibliotecario">Inventario</a>

            <!-- Administrador: 5 interfaces (Dashboard y Catálogo/Libros ya están arriba) -->
            <details class="encabezado-dropdown" data-rol="administrador">
                <summary>Gestión</summary>
                <div class="encabezado-dropdown-menu">
                    <a href="${contextPath}/FrontEnd/Prestamos/Prestamos.jsp">Préstamos</a>
                    <a href="${contextPath}/FrontEnd/Devoluciones/Devoluciones.jsp">Devoluciones</a>
                    <a href="${contextPath}/FrontEnd/Reservas/Reservas.jsp">Reservas</a>
                    <a href="${contextPath}/FrontEnd/Sanciones/Sanciones.jsp">Sanciones</a>
                </div>
            </details>
            <a href="${contextPath}/FrontEnd/Inventario/Inventario.jsp" data-rol="administrador">Inventario</a>
            <a href="${contextPath}/FrontEnd/Usuarios/Usuarios.jsp" data-rol="administrador">Usuarios</a>

            <div class="encabezado-usuario" data-rol="lector,bibliotecario,administrador">
                <span class="encabezado-usuario-icono" aria-hidden="true">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="icono-svg" aria-hidden="true">
                        <path d="M11 6a3 3 0 1 1-6 0 3 3 0 0 1 6 0"/>
                        <path fill-rule="evenodd" d="M0 8a8 8 0 1 1 16 0A8 8 0 0 1 0 8m8-7a7 7 0 0 0-5.468 11.37C3.242 11.226 4.805 10 8 10s4.757 1.225 5.468 2.37A7 7 0 0 0 8 1"/>
                    </svg>
                </span>
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

// El logo (Recursos/Imagenes/Logo.jpeg) es un JPEG con fondo negro sólido
// (JPEG no admite transparencia y no hay forma de editar la imagen en
// este proyecto). Se "recorta" el fondo en tiempo de ejecución con un
// canvas: cualquier píxel casi negro pasa a alpha 0, así el logo se ve
// bien sobre cualquier fondo (blanco, menta, etc.). Se aplica a toda
// imagen con la clase "logo-wisebooks" que haya en la página (el del
// header, y cualquier otra que se agregue en un JSP puntual).
function quitarFondoNegroDelLogo(imagen) {
    if (!imagen.naturalWidth || !imagen.naturalHeight) {
        return;
    }

    const lienzo = document.createElement("canvas");
    lienzo.width = imagen.naturalWidth;
    lienzo.height = imagen.naturalHeight;

    const contexto = lienzo.getContext("2d");
    contexto.drawImage(imagen, 0, 0);

    const UMBRAL_NEGRO = 40;
    const datos = contexto.getImageData(0, 0, lienzo.width, lienzo.height);
    const pixeles = datos.data;

    for (let i = 0; i < pixeles.length; i += 4) {
        if (pixeles[i] < UMBRAL_NEGRO && pixeles[i + 1] < UMBRAL_NEGRO && pixeles[i + 2] < UMBRAL_NEGRO) {
            pixeles[i + 3] = 0;
        }
    }

    contexto.putImageData(datos, 0, 0);
    imagen.src = lienzo.toDataURL("image/png");
}

function limpiarFondoDeLogos() {
    document.querySelectorAll("img.logo-wisebooks").forEach((imagen) => {
        if (imagen.complete) {
            quitarFondoNegroDelLogo(imagen);
        } else {
            imagen.addEventListener("load", () => quitarFondoNegroDelLogo(imagen), { once: true });
        }
    });
}

function insertarHeader() {
    const contenedor = document.getElementById("header-placeholder");
    if (!contenedor) {
        console.error("No se encontró #header-placeholder en esta página.");
        return;
    }
    contenedor.outerHTML = `<header class="encabezado-app">${crearHeaderHTML()}</header>`;
    aplicarVisibilidadPorRol();
    limpiarFondoDeLogos();
}

insertarHeader();