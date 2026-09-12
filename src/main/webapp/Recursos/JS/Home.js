/** Home.js
la página de bienvenida, visible para todos los
roles (visitante, lector, bibliotecario, administrador). El bloque del
hero alterna con [data-rol], mismo patrón que Header.js/Dashboard.js.

Los números y los libros destacados salen de Data.js (mock): no hay
backend todavía.

TODO: cuando exista el backend, "libros destacados" podría venir de
una consulta real (más prestados, mejor valorados, novedades, etc.)
en lugar de tomar los primeros del arreglo.*/

function mostrarBloquesDelRolActual() {
    const rolActual = obtenerRolActual();

    document.querySelectorAll("[data-rol]").forEach((elemento) => {
        const rolesPermitidos = elemento.getAttribute("data-rol").split(",");
        elemento.hidden = !rolesPermitidos.includes(rolActual);
    });
}

function pintarSaludoUsuario() {
    const elementoNombre = document.getElementById("home-nombre-usuario");
    if (elementoNombre) {
        elementoNombre.textContent = obtenerCorreoActual();
    }
}

function pintarMetrica(idElemento, valor) {
    const elemento = document.getElementById(idElemento);
    if (elemento) {
        elemento.textContent = valor;
    }
}

function pintarStatsHome() {
    pintarMetrica("home-total-libros", estadisticasBiblioteca.totalLibros);
    pintarMetrica("home-total-usuarios", estadisticasBiblioteca.totalUsuarios);
}

function crearTarjetaLibroDestacado(libro) {
    const columna = document.createElement("div");
    columna.className = "col-md-4 tarjeta-libro-contenedor";
    columna.innerHTML = `
        <div class="tarjeta-libro">
            <img class="portada-libro" src="${libro.portada}" alt="Portada de ${libro.titulo}">
            <h3 class="titulo-libro">${libro.titulo}</h3>
            <p class="autor-libro">${libro.autor}</p>
            <p class="categoria-libro">${libro.categoria}</p>
        </div>
    `;
    return columna;
}

function pintarLibrosDestacados() {
    const contenedor = document.getElementById("home-libros-destacados");
    if (!contenedor) {
        return;
    }
    contenedor.innerHTML = "";
    libros.slice(0, 3).forEach((libro) => {
        contenedor.appendChild(crearTarjetaLibroDestacado(libro));
    });
}

// Boletín: no hay backend todavía, así que "suscribirse" solo muestra
// un mensaje de confirmación simulado.
function manejarSuscripcionBoletin(evento) {
    evento.preventDefault();

    const correo = document.getElementById("boletin-correo").value.trim();
    if (!correo) {
        return;
    }

    const mensaje = document.getElementById("boletin-mensaje");
    mensaje.textContent = `¡Gracias! Te vamos a escribir a ${correo} con las novedades.`;
    mensaje.hidden = false;

    document.getElementById("form-boletin").reset();
}

document.addEventListener("DOMContentLoaded", () => {
    mostrarBloquesDelRolActual();
    pintarSaludoUsuario();
    pintarStatsHome();
    pintarLibrosDestacados();

    const formularioBoletin = document.getElementById("form-boletin");
    if (formularioBoletin) {
        formularioBoletin.addEventListener("submit", manejarSuscripcionBoletin);
    }
});
