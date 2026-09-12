// Catalogo.js
// Lee el arreglo "libros" (definido en Data.js) y pinta las cards
// dentro de #contenedor-libros. Los botones extra de cada card
// se muestran u ocultan según el rol actual (ver Auth.js).
//
// Por ahora NO implementa búsqueda, filtros ni orden (solo el pintado
// de las cards): eso se agrega en un paso posterior. 

function crearCardLibro(libro) {
    const rolActual = obtenerRolActual();

    const textoDisponibilidad = libro.disponible ? "Disponible" : "No disponible";
    const claseDisponibilidad = libro.disponible ? "disponible" : "no-disponible";

    const card = document.createElement("div");
    card.className = "col-md-4 tarjeta-libro-contenedor";

    card.innerHTML = `
        <div class="tarjeta-libro">
            <img class="portada-libro" src="${libro.portada}" alt="Portada de ${libro.titulo}">
            <h3 class="titulo-libro">${libro.titulo}</h3>
            <p class="autor-libro">${libro.autor}</p>
            <p class="categoria-libro">${libro.categoria}</p>
            <p class="disponibilidad-libro ${claseDisponibilidad}">● ${textoDisponibilidad}</p>

            <button class="btn btn-primary boton-ver-detalles">Ver detalles</button>

            <button class="btn btn-outline-secondary boton-favoritos" data-rol="lector" hidden>
                ${obtenerIconoSvg("corazon")} Favoritos
            </button>
            <button class="btn btn-outline-secondary boton-reservar" data-rol="lector" hidden>
                Reservar
            </button>

            <button class="btn btn-outline-secondary boton-gestionar-ejemplares" data-rol="bibliotecario,administrador" hidden>
                Gestionar ejemplares
            </button>

            <button class="btn btn-outline-secondary boton-editar" data-rol="administrador" hidden>
                Editar
            </button>
        </div>
    `;

    // Muestra solo los botones que corresponden al rol actual
    card.querySelectorAll("[data-rol]").forEach((elemento) => {
        const rolesPermitidos = elemento.getAttribute("data-rol").split(",");
        if (rolesPermitidos.includes(rolActual)) {
            elemento.hidden = false;
        }
    });

    return card;
}

function pintarLibros(listaLibros) {
    const contenedor = document.getElementById("contenedor-libros");
    contenedor.innerHTML = "";

    listaLibros.forEach((libro) => {
        contenedor.appendChild(crearCardLibro(libro));
    });
}

document.addEventListener("DOMContentLoaded", () => {
    pintarLibros(libros);
});