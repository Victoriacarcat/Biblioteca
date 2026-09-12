/** Favoritos.js
Pinta las cards de los libros marcados como favoritos, reutilizando
el arreglo "libros" de Data.js. Como todavía no hay backend, la
lista de favoritos es un mock: por ahora solo el libro con
id 1 está marcado como favorito, a modo de ejemplo. */

const idsFavoritosEjemplo = [1];

function obtenerLibrosFavoritos() {
    return libros.filter((libro) => idsFavoritosEjemplo.includes(libro.id));
}

function crearCardFavorito(libro) {
    const textoDisponibilidad = libro.disponible ? "Disponible" : "No disponible";
    const claseDisponibilidad = libro.disponible ? "disponible" : "no-disponible";

    const card = document.createElement("div");
    card.className = "col-md-4 tarjeta-favorito-contenedor";

    card.innerHTML = `
        <div class="tarjeta-favorito">
            <img class="portada-libro" src="${libro.portada}" alt="Portada de ${libro.titulo}">
            <h3 class="titulo-libro">${libro.titulo}</h3>
            <p class="autor-libro">${libro.autor}</p>
            <p class="disponibilidad-libro ${claseDisponibilidad}">● ${textoDisponibilidad}</p>

            <button class="btn btn-primary boton-ver-detalles">Ver detalles</button>
            <button class="btn btn-outline-danger boton-eliminar-favorito" data-id-libro="${libro.id}">
                Eliminar
            </button>
        </div>
    `;

    return card;
}

function pintarFavoritos() {
    const contenedor = document.getElementById("contenedor-favoritos");
    const mensajeSinFavoritos = document.getElementById("mensaje-sin-favoritos");
    const librosFavoritos = obtenerLibrosFavoritos();

    contenedor.innerHTML = "";

    if (librosFavoritos.length === 0) {
        mensajeSinFavoritos.hidden = false;
        return;
    }

    mensajeSinFavoritos.hidden = true;
    librosFavoritos.forEach((libro) => {
        contenedor.appendChild(crearCardFavorito(libro));
    });
}

document.addEventListener("DOMContentLoaded", () => {
    pintarFavoritos();
});


