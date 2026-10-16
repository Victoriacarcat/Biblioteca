/** Favoritos.js
Vista del Lector: sus libros favoritos.

    GET    /api/favoritos           -> sus favoritos (LibroDTO), en orden alfabético
    DELETE /api/favoritos?idLibro=5 -> quita uno

El buscador filtra en el navegador, por título o autor, la lista ya recibida.
*/

// Favoritos que mandó el servidor
let misFavoritos = [];

function crearCardFavorito(libro) {
    const disponible = libro.stockDisponible > 0;
    const card = document.createElement("div");
    card.className = "col-md-4 tarjeta-favorito-contenedor";

    // Todo dato del servidor pasa por escaparHtml antes de entrar al HTML
    card.innerHTML = `
        <div class="tarjeta-favorito">
            <img class="portada-libro" src="${escaparHtml(urlPortada(libro.portadaUrl))}"
                 alt="Portada de ${escaparHtml(libro.titulo)}" onerror="portadaRota(this)">
            <h3 class="titulo-libro">${escaparHtml(libro.titulo)}</h3>
            <p class="autor-libro">${escaparHtml(libro.autores.join(", "))}</p>
            <p class="disponibilidad-libro ${disponible ? "disponible" : "no-disponible"}">
                ● ${disponible ? "Disponible" : "No disponible"}
            </p>

            <a class="btn btn-primary boton-ver-detalles"
               href="${contextPath}/FrontEnd/Catalogo/Detallelibro.jsp?id=${libro.id}">Ver detalles</a>
            <button type="button" class="btn btn-outline-danger boton-eliminar-favorito">
                Eliminar
            </button>
        </div>
    `;

    card.querySelector(".boton-eliminar-favorito").addEventListener("click", (evento) => {
        quitarFavorito(libro, evento.currentTarget);
    });
    return card;
}

/* Favoritos cuyo título o autor contiene el texto del buscador. */
function favoritosFiltrados() {
    const texto = document.getElementById("campo-busqueda-favoritos").value.trim().toLowerCase();
    return misFavoritos.filter((libro) =>
        libro.titulo.toLowerCase().includes(texto)
        || libro.autores.some((autor) => autor.toLowerCase().includes(texto)));
}

function pintarFavoritos() {
    const contenedor = document.getElementById("contenedor-favoritos");
    const mensajeSinFavoritos = document.getElementById("mensaje-sin-favoritos");
    const lista = favoritosFiltrados();
    contenedor.innerHTML = "";

    if (misFavoritos.length === 0) {
        mensajeSinFavoritos.textContent = "Aún no tienes libros favoritos.";
        mensajeSinFavoritos.hidden = false;
        return;
    }
    if (lista.length === 0) {
        mensajeSinFavoritos.textContent = "Ningún favorito coincide con la búsqueda.";
        mensajeSinFavoritos.hidden = false;
        return;
    }
    mensajeSinFavoritos.hidden = true;
    lista.forEach((libro) => {
        contenedor.appendChild(crearCardFavorito(libro));
    });
}

async function quitarFavorito(libro, boton) {
    ocultarError("favoritos-error");
    boton.disabled = true;
    try {
        await api.delete("/favoritos?idLibro=" + libro.id);
        await cargarFavoritos();
    } catch (error) {
        mostrarError("favoritos-error", error.message);
        boton.disabled = false;
    }
}

async function cargarFavoritos() {
    try {
        misFavoritos = await api.get("/favoritos");
        pintarFavoritos();
    } catch (error) {
        mostrarError("favoritos-error", error.message);
    }
}

// Espera a saber quién inició sesión (Auth.js) antes de pintar
sesionLista.then(() => {
    if (paginaPermitida(["lector"], "favoritos-error")) {
        document.getElementById("campo-busqueda-favoritos").addEventListener("input", pintarFavoritos);
        cargarFavoritos();
    }
});
