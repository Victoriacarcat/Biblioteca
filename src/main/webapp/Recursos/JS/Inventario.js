/** Inventario.js
Vista de Bibliotecario/Administrador: los libros y su stock.

El inventario se maneja por STOCK de cada libro, no por ejemplares
individuales: stockTotal (copias que tiene la biblioteca) y stockDisponible
(las que se pueden prestar ahora). La diferencia son las prestadas o las
apartadas por una reserva aprobada.

    GET  /api/libros?texto=                       -> libros (el personal recibe también los desactivados)
    GET  /api/catalogos?tipo=...                  -> sugerencias para el formulario
    PUT  /api/libros?id=5&accion=copias  {cantidad} -> agrega copias (personal)
    PUT  /api/libros?id=5&accion=retiro  {cantidad} -> retira copias disponibles (personal)
    POST /api/libros            LibroFormDTO      -> crea un libro (administrador)
    PUT  /api/libros?id=5       LibroFormDTO      -> edita sus datos (administrador)
    PUT  /api/libros?id=5&accion=activo {activo}  -> activa o desactiva (administrador)

Se puede abrir con Inventario.jsp?id=5 (resalta ese libro) o con
Inventario.jsp?id=5&editar=1 (abre su formulario): así llegan los botones
"Gestionar ejemplares" y "Editar" del catálogo y del detalle.
*/

// Libros que mandó el servidor con la búsqueda actual
let librosInventario = [];

function avisarInventario(tipo, mensaje) {
    ocultarError("inventario-exito");
    ocultarError("inventario-error");
    if (tipo === "exito") {
        mostrarExito("inventario-exito", mensaje);
    } else {
        mostrarError("inventario-error", mensaje);
    }
}

// ---- Métricas y tabla ----

/* Libros que se muestran: los desactivados solo si se marca la casilla. */
function librosVisibles() {
    const mostrarDesactivados = document.getElementById("mostrar-desactivados").checked;
    return librosInventario.filter((libro) => libro.activo || mostrarDesactivados);
}

function pintarMetricas(lista) {
    const total = lista.reduce((suma, libro) => suma + libro.stockTotal, 0);
    const disponibles = lista.reduce((suma, libro) => suma + libro.stockDisponible, 0);
    document.getElementById("metrica-titulos").textContent = lista.length;
    document.getElementById("metrica-copias-totales").textContent = total;
    document.getElementById("metrica-copias-disponibles").textContent = disponibles;
    document.getElementById("metrica-copias-prestadas").textContent = total - disponibles;
}

function crearFilaLibro(libro) {
    const fila = document.createElement("tr");
    fila.dataset.idLibro = libro.id;

    fila.innerHTML = `
        <td>
            <span class="celda-con-icono">
                <img class="miniatura-libro" src="${escaparHtml(urlPortada(libro.portadaUrl))}" alt=""
                     onerror="portadaRota(this)">
                <span>
                    ${escaparHtml(libro.titulo)}
                    <small class="d-block">${escaparHtml(libro.autores.join(", "))}</small>
                </span>
            </span>
        </td>
        <td>${escaparHtml(libro.categoria)}</td>
        <td>${libro.stockTotal}</td>
        <td>${libro.stockDisponible}</td>
        <td>${libro.stockTotal - libro.stockDisponible}</td>
        <td>${libro.activo
            ? '<span class="estado-usuario estado-positivo">Activo</span>'
            : '<span class="estado-usuario estado-neutro">Desactivado</span>'}</td>
        <td>
            <button type="button" class="btn btn-outline-primary btn-sm boton-agregar-copias">+ Copias</button>
            <button type="button" class="btn btn-outline-secondary btn-sm boton-retirar-copias"
                    ${libro.stockDisponible === 0 ? "disabled" : ""}>− Copias</button>
            <button type="button" class="btn btn-outline-secondary btn-sm boton-editar-libro" data-rol="administrador" hidden>Editar</button>
            <button type="button" class="btn btn-sm boton-activo-libro ${libro.activo ? "btn-outline-danger" : "btn-outline-success"}"
                    data-rol="administrador" hidden>${libro.activo ? "Desactivar" : "Activar"}</button>
        </td>
    `;
    mostrarSegunRol(fila);

    if (libro.stockDisponible === 0) {
        fila.querySelector(".boton-retirar-copias").title = "Solo se pueden retirar copias disponibles (no prestadas ni apartadas).";
    }
    fila.querySelector(".boton-agregar-copias").addEventListener("click", (e) => cambiarCopias(libro, "copias", e.currentTarget));
    fila.querySelector(".boton-retirar-copias").addEventListener("click", (e) => cambiarCopias(libro, "retiro", e.currentTarget));
    fila.querySelector(".boton-editar-libro").addEventListener("click", () => abrirFormulario(libro));
    fila.querySelector(".boton-activo-libro").addEventListener("click", (e) => cambiarActivo(libro, e.currentTarget));
    return fila;
}

function pintarInventario() {
    const lista = librosVisibles();
    pintarMetricas(lista);

    const cuerpo = document.getElementById("cuerpo-inventario");
    cuerpo.innerHTML = "";
    if (lista.length === 0) {
        cuerpo.innerHTML = '<tr><td colspan="7">No hay libros con esa búsqueda.</td></tr>';
        return;
    }
    lista.forEach((libro) => {
        cuerpo.appendChild(crearFilaLibro(libro));
    });
}

async function cargarInventario() {
    const texto = document.getElementById("buscar-libro-inventario").value.trim();
    try {
        librosInventario = await api.get("/libros" + (texto ? "?texto=" + encodeURIComponent(texto) : ""));
        pintarInventario();
    } catch (error) {
        avisarInventario("error", error.message);
    }
}

// Igual que en Usuarios: se espera 300 ms desde la última tecla antes de pedir
let temporizadorBusqueda = null;
function buscarAlEscribir() {
    clearTimeout(temporizadorBusqueda);
    temporizadorBusqueda = setTimeout(cargarInventario, 300);
}

// ---- Copias (personal) ----

/* accion: "copias" (agregar) o "retiro" (retirar). */
async function cambiarCopias(libro, accion, boton) {
    const pregunta = accion === "copias"
        ? `¿Cuántas copias nuevas de "${libro.titulo}" llegaron?`
        : `¿Cuántas copias de "${libro.titulo}" se retiran? (hay ${libro.stockDisponible} disponibles)`;
    const respuesta = prompt(pregunta, "1");
    if (respuesta === null) {
        return; // canceló el cuadro
    }
    const cantidad = Number(respuesta.trim());
    if (!Number.isInteger(cantidad) || cantidad < 1) {
        avisarInventario("error", "La cantidad debe ser un número entero mayor que cero.");
        return;
    }

    boton.disabled = true;
    try {
        const actualizado = await api.put("/libros?id=" + libro.id + "&accion=" + accion, { cantidad: cantidad });
        avisarInventario("exito", `"${actualizado.titulo}": ahora tiene ${actualizado.stockTotal} copias `
            + `(${actualizado.stockDisponible} disponibles).`);
        await cargarInventario();
    } catch (error) {
        // Por ejemplo: "Solo hay 1 copias disponibles; no se pueden retirar 3."
        avisarInventario("error", error.message);
        boton.disabled = false;
    }
}

// ---- Activar / desactivar (administrador) ----

async function cambiarActivo(libro, boton) {
    const aviso = libro.activo
        ? `¿Desactivar "${libro.titulo}"? Dejará de aparecer en el catálogo público y no se podrá reservar.`
        : `¿Activar "${libro.titulo}"? Volverá a aparecer en el catálogo.`;
    if (!confirm(aviso)) {
        return;
    }
    boton.disabled = true;
    try {
        // El mensaje se arma con lo que responde el servidor (el estado ya cambiado)
        const actualizado = await api.put("/libros?id=" + libro.id + "&accion=activo", { activo: !libro.activo });
        avisarInventario("exito", `"${actualizado.titulo}" quedó ${actualizado.activo ? "activado" : "desactivado"}.`);
        await cargarInventario();
    } catch (error) {
        avisarInventario("error", error.message);
        boton.disabled = false;
    }
}

// ---- Formulario de libro (administrador) ----

/* Llena un <datalist> con los nombres que manda /api/catalogos. */
async function llenarSugerencias(tipo, idLista) {
    try {
        const opciones = await api.get("/catalogos?tipo=" + tipo);
        const lista = document.getElementById(idLista);
        lista.innerHTML = "";
        opciones.forEach((opcion) => {
            const elemento = document.createElement("option");
            elemento.value = opcion.nombre;
            lista.appendChild(elemento);
        });
    } catch (error) {
        // Sin sugerencias el formulario funciona igual
        console.error("No se pudieron cargar las sugerencias de " + tipo + ":", error.message);
    }
}

function cargarSugerencias() {
    llenarSugerencias("categorias", "lista-categorias");
    llenarSugerencias("generos", "lista-generos");
    llenarSugerencias("editoriales", "lista-editoriales");
    llenarSugerencias("autores", "lista-autores");
}

/* Abre el formulario: vacío para un libro nuevo, o con los datos de "libro" para editarlo. */
function abrirFormulario(libro) {
    const editando = libro !== null;
    const valor = (id, dato) => { document.getElementById(id).value = dato ?? ""; };

    document.getElementById("titulo-panel-libro").textContent = editando ? `Editar "${libro.titulo}"` : "Nuevo libro";
    valor("libro-id", editando ? libro.id : "");
    valor("libro-titulo", editando ? libro.titulo : "");
    valor("libro-isbn", editando ? libro.isbn : "");
    valor("libro-autores", editando ? libro.autores.join(", ") : "");
    valor("libro-categoria", editando ? libro.categoria : "");
    valor("libro-genero", editando ? libro.genero : "");
    valor("libro-editorial", editando ? libro.editorial : "");
    valor("libro-fecha", editando ? libro.fechaPublicacion : "");
    valor("libro-ubicacion", editando ? libro.ubicacion : "");
    valor("libro-portada", editando ? libro.portadaUrl : "");
    valor("libro-descripcion", editando ? libro.descripcion : "");
    valor("libro-stock", "1");
    // El stock solo se fija al crear; después se usa + Copias / − Copias
    document.getElementById("grupo-stock-inicial").hidden = editando;

    const panel = document.getElementById("panel-libro");
    panel.hidden = false;
    document.getElementById("libro-titulo").focus();
    if (panel.scrollIntoView) {
        panel.scrollIntoView({ behavior: "smooth", block: "start" });
    }
}

function cerrarFormulario() {
    document.getElementById("panel-libro").hidden = true;
    document.getElementById("form-libro").reset();
}

async function guardarLibro(evento) {
    evento.preventDefault();
    const valor = (id) => document.getElementById(id).value.trim();
    const idLibro = valor("libro-id");

    // Los nombres deben coincidir con LibroFormDTO del backend
    const libro = {
        titulo: valor("libro-titulo"),
        isbn: valor("libro-isbn"),
        // "Gabriel García Márquez, Otro" -> ["Gabriel García Márquez", "Otro"]
        autores: valor("libro-autores").split(",").map((autor) => autor.trim()).filter((autor) => autor !== ""),
        categoria: valor("libro-categoria"),
        genero: valor("libro-genero"),
        editorial: valor("libro-editorial"),
        // Sin fecha se manda null (una cadena vacía no es una fecha válida)
        fechaPublicacion: valor("libro-fecha") || null,
        ubicacion: valor("libro-ubicacion"),
        portadaUrl: valor("libro-portada"),
        descripcion: valor("libro-descripcion"),
        stockInicial: Number(valor("libro-stock") || 0)
    };

    const boton = document.getElementById("boton-guardar-libro");
    boton.disabled = true;
    try {
        const guardado = idLibro
            ? await api.put("/libros?id=" + idLibro, libro)
            : await api.post("/libros", libro);
        avisarInventario("exito", idLibro
            ? `Se guardaron los cambios de "${guardado.titulo}".`
            : `Se creó "${guardado.titulo}" con ${guardado.stockTotal} copias.`);
        cerrarFormulario();
        cargarSugerencias(); // por si se creó una categoría, editorial o autor nuevo
        await cargarInventario();
    } catch (error) {
        // Por ejemplo: "Ya existe un libro con ese ISBN." o "El libro necesita al menos un autor."
        avisarInventario("error", error.message);
    }
    boton.disabled = false;
}

// ---- Llegada desde el catálogo (?id=5 o ?id=5&editar=1) ----

function atenderParametrosDeLaUrl() {
    const parametros = new URLSearchParams(window.location.search);
    const id = Number(parametros.get("id"));
    if (!id) {
        return;
    }
    const libro = librosInventario.find((l) => l.id === id);
    if (!libro) {
        avisarInventario("error", "No se encontró el libro indicado.");
        return;
    }
    if (!libro.activo) {
        // Para que su fila se vea aunque esté desactivado
        document.getElementById("mostrar-desactivados").checked = true;
        pintarInventario();
    }
    if (parametros.get("editar") === "1" && obtenerRolActual() === "administrador") {
        abrirFormulario(libro);
        return;
    }
    const fila = document.querySelector(`#cuerpo-inventario tr[data-id-libro="${id}"]`);
    fila.classList.add("table-warning");
    if (fila.scrollIntoView) {
        fila.scrollIntoView({ behavior: "smooth", block: "center" });
    }
}

// Espera a saber quién inició sesión (Auth.js) antes de pintar
sesionLista.then(async () => {
    if (!paginaPermitida(["bibliotecario", "administrador"], "inventario-error")) {
        return;
    }
    mostrarSegunRol();

    document.getElementById("buscar-libro-inventario").addEventListener("input", buscarAlEscribir);
    document.getElementById("mostrar-desactivados").addEventListener("change", pintarInventario);

    if (obtenerRolActual() === "administrador") {
        cargarSugerencias();
        document.getElementById("boton-nuevo-libro").addEventListener("click", () => abrirFormulario(null));
        document.getElementById("boton-cancelar-libro").addEventListener("click", cerrarFormulario);
        document.getElementById("form-libro").addEventListener("submit", guardarLibro);
    }

    await cargarInventario();
    atenderParametrosDeLaUrl();
});
