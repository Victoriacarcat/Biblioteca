/** Catalogo.js
Catálogo de libros con datos reales del backend.

De dónde sale cada cosa:
    GET /api/libros?texto=&idCategoria=&idGenero=&soloDisponibles=true
        -> la lista de libros. El servidor aplica estos filtros. El texto
           busca en título, ISBN y nombre del autor.
    GET /api/catalogos?tipo=categorias | generos
        -> las opciones de los selects de filtro.
    GET /api/favoritos (solo lector)
        -> para saber qué libros ya están en sus favoritos.

Algunos filtros los aplica el navegador sobre la lista ya recibida,
porque el backend no los ofrece: "No disponible", el año de publicación
y el orden.

Acciones del lector: Reservar (POST /api/reservas) y Favoritos
(POST /DELETE /api/favoritos). Los del personal ("Gestionar copias" y
"Editar") llevan a Inventario con el libro elegido.
*/

// Libros que mandó el servidor con los filtros actuales
let librosCargados = [];

// Ids de los libros favoritos del lector (Set: buscar un id es inmediato)
let idsFavoritos = new Set();

// ---- Avisos ----

/* Muestra un aviso verde o rojo arriba de los resultados (y oculta el otro). */
function avisarCatalogo(tipo, mensaje) {
    ocultarError("catalogo-exito");
    ocultarError("catalogo-error");
    if (tipo === "exito") {
        mostrarExito("catalogo-exito", mensaje);
    } else {
        mostrarError("catalogo-error", mensaje);
    }
}

// ---- Selects de filtro ----

/* Llena un <select> con las opciones [{id, nombre}] que manda el backend. */
function llenarSelect(idSelect, opciones) {
    const select = document.getElementById(idSelect);
    opciones.forEach((opcion) => {
        const elemento = document.createElement("option");
        elemento.value = opcion.id;
        elemento.textContent = opcion.nombre;
        select.appendChild(elemento);
    });
}

async function cargarFiltros() {
    try {
        llenarSelect("filtro-categoria", await api.get("/catalogos?tipo=categorias"));
        llenarSelect("filtro-genero", await api.get("/catalogos?tipo=generos"));
    } catch (error) {
        avisarCatalogo("error", "No se pudieron cargar los filtros: " + error.message);
    }
}

/* Año (número) de la fecha de publicación "1967-05-30", o null si no tiene. */
function anioDe(libro) {
    return libro.fechaPublicacion ? Number(libro.fechaPublicacion.substring(0, 4)) : null;
}

/*
 El select de año se arma con los años de los libros recibidos (del más
 reciente al más antiguo). Si el año elegido sigue existiendo, se conserva.
*/
function llenarSelectAnio() {
    const select = document.getElementById("filtro-fecha");
    const anioElegido = select.value;
    const anios = [...new Set(librosCargados.map(anioDe).filter((anio) => anio !== null))]
        .sort((a, b) => b - a);

    select.innerHTML = '<option value="">Todos</option>';
    anios.forEach((anio) => {
        const elemento = document.createElement("option");
        elemento.value = anio;
        elemento.textContent = anio;
        select.appendChild(elemento);
    });
    if (anios.includes(Number(anioElegido))) {
        select.value = anioElegido;
    }
}

// ---- Cargar libros ----

/* Arma la dirección de /api/libros con los filtros que aplica el servidor. */
function rutaLibrosConFiltros() {
    const parametros = new URLSearchParams();
    const texto = document.getElementById("campo-busqueda").value.trim();
    const idCategoria = document.getElementById("filtro-categoria").value;
    const idGenero = document.getElementById("filtro-genero").value;
    const disponibilidad = document.getElementById("filtro-disponibilidad").value;

    if (texto) {
        parametros.set("texto", texto);
    }
    if (idCategoria) {
        parametros.set("idCategoria", idCategoria);
    }
    if (idGenero) {
        parametros.set("idGenero", idGenero);
    }
    if (disponibilidad === "disponible") {
        parametros.set("soloDisponibles", "true");
    }
    return "/libros?" + parametros.toString();
}

async function cargarLibros() {
    try {
        librosCargados = await api.get(rutaLibrosConFiltros());
        llenarSelectAnio();
        mostrarLibros();
    } catch (error) {
        librosCargados = [];
        mostrarLibros();
        avisarCatalogo("error", "No se pudieron cargar los libros: " + error.message);
    }
}

/* Solo para el lector: qué libros ya tiene en favoritos. */
async function cargarFavoritos() {
    if (obtenerRolActual() !== "lector") {
        return;
    }
    try {
        const favoritos = await api.get("/favoritos");
        idsFavoritos = new Set(favoritos.map((libro) => libro.id));
    } catch (error) {
        // Sin la lista, los botones de favoritos simplemente empiezan en "Favoritos"
        console.error("No se pudieron cargar los favoritos:", error.message);
    }
}

// ---- Filtros del navegador y orden ----

/* Aplica a librosCargados los filtros que no hace el servidor y el orden elegido. */
function librosParaMostrar() {
    const disponibilidad = document.getElementById("filtro-disponibilidad").value;
    const anio = document.getElementById("filtro-fecha").value;
    const orden = document.getElementById("orden-resultados").value;

    let lista = librosCargados.filter((libro) => {
        const pasaDisponibilidad = disponibilidad !== "no-disponible" || libro.stockDisponible === 0;
        const pasaAnio = !anio || anioDe(libro) === Number(anio);
        return pasaDisponibilidad && pasaAnio;
    });

    // "relevantes" deja el orden del servidor (alfabético por título)
    if (orden === "titulo-asc") {
        lista.sort((a, b) => a.titulo.localeCompare(b.titulo, "es"));
    } else if (orden === "titulo-desc") {
        lista.sort((a, b) => b.titulo.localeCompare(a.titulo, "es"));
    } else if (orden === "recientes") {
        lista.sort((a, b) => (anioDe(b) ?? 0) - (anioDe(a) ?? 0));
    } else if (orden === "antiguos") {
        // Los libros sin fecha van al final
        lista.sort((a, b) => (anioDe(a) ?? 9999) - (anioDe(b) ?? 9999));
    }
    return lista;
}

// ---- Pintar ----

function textoBotonFavorito(idLibro) {
    return idsFavoritos.has(idLibro) ? "En favoritos" : "Favoritos";
}

function crearCardLibro(libro) {
    const disponible = libro.stockDisponible > 0;
    const textoDisponibilidad = disponible
        ? `Disponible (${libro.stockDisponible})`
        : "No disponible";
    // Solo el personal recibe libros desactivados; se marcan para distinguirlos
    const textoDesactivado = libro.activo ? "" : " · Desactivado";

    const card = document.createElement("div");
    card.className = "col-md-4 tarjeta-libro-contenedor";

    // Todo dato del servidor pasa por escaparHtml antes de entrar al HTML
    card.innerHTML = `
        <div class="tarjeta-libro">
            <img class="portada-libro" src="${escaparHtml(urlPortada(libro.portadaUrl))}"
                 alt="Portada de ${escaparHtml(libro.titulo)}" onerror="portadaRota(this)">
            <h3 class="titulo-libro">${escaparHtml(libro.titulo)}</h3>
            <p class="autor-libro">${escaparHtml(libro.autores.join(", "))}</p>
            <p class="categoria-libro">${escaparHtml(libro.categoria)}</p>
            <p class="disponibilidad-libro ${disponible ? "disponible" : "no-disponible"}">
                ● ${textoDisponibilidad}${textoDesactivado}
            </p>

            <a class="btn btn-primary boton-ver-detalles"
               href="${contextPath}/FrontEnd/Catalogo/Detallelibro.jsp?id=${libro.id}">Ver detalles</a>

            <button type="button" class="btn btn-outline-secondary boton-favoritos" data-rol="lector" hidden>
                ${obtenerIconoSvg("corazon")} <span class="texto-favorito">${textoBotonFavorito(libro.id)}</span>
            </button>
            <button type="button" class="btn btn-outline-secondary boton-reservar" data-rol="lector" hidden>
                Reservar
            </button>

            <a class="btn btn-outline-secondary boton-gestionar-ejemplares" data-rol="bibliotecario,administrador" hidden
               href="${contextPath}/FrontEnd/Inventario/Inventario.jsp?id=${libro.id}">Gestionar copias</a>

            <a class="btn btn-outline-secondary boton-editar" data-rol="administrador" hidden
               href="${contextPath}/FrontEnd/Inventario/Inventario.jsp?id=${libro.id}&editar=1">Editar</a>
        </div>
    `;

    // Muestra solo los botones que corresponden al rol actual (Utilidades.js)
    mostrarSegunRol(card);

    card.querySelector(".boton-reservar").addEventListener("click", (evento) => {
        reservarLibro(libro, evento.currentTarget);
    });
    card.querySelector(".boton-favoritos").addEventListener("click", (evento) => {
        cambiarFavorito(libro, evento.currentTarget);
    });

    return card;
}

function mostrarLibros() {
    const contenedor = document.getElementById("contenedor-libros");
    const lista = librosParaMostrar();
    contenedor.innerHTML = "";

    if (lista.length === 0) {
        contenedor.innerHTML = '<p class="col-12">No se encontraron libros con esos filtros.</p>';
        return;
    }
    lista.forEach((libro) => {
        contenedor.appendChild(crearCardLibro(libro));
    });
}

// ---- Acciones del lector ----

async function reservarLibro(libro, boton) {
    boton.disabled = true;
    try {
        await api.post("/reservas", { idLibro: libro.id });
        avisarCatalogo("exito", `Reservaste "${libro.titulo}". Puedes ver su estado en Mis reservas.`);
    } catch (error) {
        avisarCatalogo("error", error.message);
    }
    boton.disabled = false;
}

/* Agrega o quita el libro de favoritos según cómo esté ahora. */
async function cambiarFavorito(libro, boton) {
    const esFavorito = idsFavoritos.has(libro.id);
    boton.disabled = true;
    try {
        if (esFavorito) {
            await api.delete("/favoritos?idLibro=" + libro.id);
            idsFavoritos.delete(libro.id);
        } else {
            await api.post("/favoritos", { idLibro: libro.id });
            idsFavoritos.add(libro.id);
        }
        boton.querySelector(".texto-favorito").textContent = textoBotonFavorito(libro.id);
    } catch (error) {
        avisarCatalogo("error", error.message);
    }
    boton.disabled = false;
}

// ---- Eventos ----

function limpiarFiltros() {
    document.getElementById("campo-busqueda").value = "";
    document.getElementById("filtro-categoria").value = "";
    document.getElementById("filtro-genero").value = "";
    document.getElementById("filtro-disponibilidad").value = "";
    document.getElementById("filtro-fecha").value = "";
    document.getElementById("orden-resultados").value = "relevantes";
    cargarLibros();
}

function conectarEventos() {
    // Filtros que aplica el servidor: hay que volver a pedir los libros
    document.getElementById("boton-buscar").addEventListener("click", cargarLibros);
    document.getElementById("campo-busqueda").addEventListener("keydown", (evento) => {
        if (evento.key === "Enter") {
            cargarLibros();
        }
    });
    document.getElementById("filtro-categoria").addEventListener("change", cargarLibros);
    document.getElementById("filtro-genero").addEventListener("change", cargarLibros);
    document.getElementById("filtro-disponibilidad").addEventListener("change", cargarLibros);

    // Filtros del navegador: basta con volver a pintar
    document.getElementById("filtro-fecha").addEventListener("change", mostrarLibros);
    document.getElementById("orden-resultados").addEventListener("change", mostrarLibros);

    document.getElementById("boton-limpiar-filtros").addEventListener("click", limpiarFiltros);
}

// Espera a saber quién inició sesión (Auth.js) antes de pintar
sesionLista.then(async () => {
    conectarEventos();
    await cargarFiltros();
    await cargarFavoritos();
    await cargarLibros();
});
