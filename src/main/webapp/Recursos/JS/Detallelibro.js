/** Detallelibro.js
Ficha de un libro con datos reales: Detallelibro.jsp?id=5

    GET /api/libros?id=5             -> datos del libro
    GET /api/favoritos?idLibro=5     -> {"favorito": true|false} (solo lector)
    POST /api/reservas {idLibro}     -> el lector reserva
    POST / DELETE /api/favoritos     -> agrega o quita de favoritos

Los datos se escriben con textContent (no innerHTML): así se muestran
como texto aunque traigan caracteres especiales.
*/

// Libro que se está mostrando (LibroDTO del backend)
let libroActual = null;
let esFavorito = false;

/* Pone un texto en el elemento con ese id; si el dato no existe, muestra "—". */
function ponerTexto(id, valor) {
    const vacio = valor === null || valor === undefined || valor === "";
    document.getElementById(id).textContent = vacio ? "—" : valor;
}

function avisarDetalle(tipo, mensaje) {
    ocultarError("detalle-exito");
    ocultarError("detalle-error");
    if (tipo === "exito") {
        mostrarExito("detalle-exito", mensaje);
    } else {
        mostrarError("detalle-error", mensaje);
    }
}

function pintarLibro(libro) {
    document.title = libro.titulo + " - Wisebooks";

    const portada = document.getElementById("libro-portada");
    portada.src = urlPortada(libro.portadaUrl);
    portada.alt = "Portada de " + libro.titulo;

    ponerTexto("libro-categoria", libro.categoria);
    ponerTexto("libro-titulo", libro.titulo);
    ponerTexto("libro-autores", libro.autores.join(", "));
    ponerTexto("libro-editorial", libro.editorial);
    ponerTexto("libro-anio", libro.fechaPublicacion ? libro.fechaPublicacion.substring(0, 4) : null);
    ponerTexto("libro-isbn", libro.isbn);
    ponerTexto("libro-descripcion", libro.descripcion || "Este libro todavía no tiene descripción.");

    ponerTexto("libro-stock-total", libro.stockTotal);
    ponerTexto("libro-stock-disponible", libro.stockDisponible);
    // Las copias que no están disponibles están prestadas o apartadas por una reserva aprobada
    ponerTexto("libro-stock-prestados", libro.stockTotal - libro.stockDisponible);
    ponerTexto("libro-ubicacion", libro.ubicacion);
    ponerTexto("libro-genero", libro.genero);

    const disponible = libro.stockDisponible > 0;
    const sello = document.getElementById("libro-disponibilidad");
    sello.textContent = (disponible ? "Disponible" : "No disponible") + (libro.activo ? "" : " · Desactivado");
    sello.className = "sello-disponibilidad " + (disponible ? "disponible" : "no-disponible");

    // Enlaces del personal a Inventario con este libro
    const inventario = contextPath + "/FrontEnd/Inventario/Inventario.jsp?id=" + libro.id;
    document.getElementById("enlace-gestionar-copias").href = inventario;
    document.getElementById("enlace-editar-libro").href = inventario + "&editar=1";

    document.getElementById("contenido-libro").hidden = false;
    // Botones según el rol actual (Utilidades.js)
    mostrarSegunRol();
}

function pintarBotonFavorito() {
    document.getElementById("boton-favorito").textContent =
        esFavorito ? "Quitar de favoritos" : "Añadir a favoritos";
}

/* Solo para el lector: consulta si este libro ya está en sus favoritos. */
async function cargarEstadoFavorito() {
    if (obtenerRolActual() !== "lector") {
        return;
    }
    try {
        const respuesta = await api.get("/favoritos?idLibro=" + libroActual.id);
        esFavorito = respuesta.favorito;
    } catch (error) {
        console.error("No se pudo consultar el favorito:", error.message);
    }
    pintarBotonFavorito();
}

async function reservar() {
    const boton = document.getElementById("boton-reservar");
    boton.disabled = true;
    try {
        await api.post("/reservas", { idLibro: libroActual.id });
        avisarDetalle("exito", "Reserva creada. Puedes ver su estado en Mis reservas.");
    } catch (error) {
        avisarDetalle("error", error.message);
    }
    boton.disabled = false;
}

async function cambiarFavorito() {
    const boton = document.getElementById("boton-favorito");
    boton.disabled = true;
    try {
        if (esFavorito) {
            await api.delete("/favoritos?idLibro=" + libroActual.id);
        } else {
            await api.post("/favoritos", { idLibro: libroActual.id });
        }
        esFavorito = !esFavorito;
        pintarBotonFavorito();
    } catch (error) {
        avisarDetalle("error", error.message);
    }
    boton.disabled = false;
}

async function cargarDetalle() {
    const id = new URLSearchParams(window.location.search).get("id");
    if (!id) {
        avisarDetalle("error", "No se indicó qué libro mostrar. Vuelve al catálogo y elige uno.");
        return;
    }
    try {
        libroActual = await api.get("/libros?id=" + encodeURIComponent(id));
    } catch (error) {
        // Por ejemplo "El libro no existe." (también si está desactivado y no eres personal)
        avisarDetalle("error", error.message);
        return;
    }
    pintarLibro(libroActual);
    await cargarEstadoFavorito();

    document.getElementById("boton-reservar").addEventListener("click", reservar);
    document.getElementById("boton-favorito").addEventListener("click", cambiarFavorito);
}

// Espera a saber quién inició sesión (Auth.js) antes de pintar
sesionLista.then(cargarDetalle);
