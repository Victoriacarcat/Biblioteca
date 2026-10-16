/* Prestamos.js
Vista de Bibliotecario/Administrador: todos los préstamos.

    GET  /api/prestamos                        -> todos los préstamos (personal)
    GET  /api/libros?soloDisponibles=true      -> libros para el préstamo en mostrador
    POST /api/prestamos {idUsuario, idLibro}   -> préstamo en el mostrador (sin reserva)
    PUT  /api/prestamos?id=5&accion=anular {motivo} -> anula uno (solo administrador)

Los buscadores y el filtro de estado trabajan en el navegador sobre la
lista ya recibida. "Vencido" es un préstamo ACTIVO con la fecha límite pasada.
*/

// Préstamos que mandó el servidor
let todosLosPrestamos = [];

// ---- Avisos ----

function avisarPrestamos(tipo, mensaje) {
    ocultarError("prestamos-exito");
    ocultarError("prestamos-error");
    if (tipo === "exito") {
        mostrarExito("prestamos-exito", mensaje);
    } else {
        mostrarError("prestamos-error", mensaje);
    }
}

// ---- Tabla ----

function crearFilaPrestamo(prestamo) {
    const fila = document.createElement("tr");
    const puedeAnular = prestamo.estado === "ACTIVO";

    fila.innerHTML = `
        <td>
            ${celdaUsuarioConIcono(prestamo.nombreUsuario)}
            <small class="d-block">${escaparHtml(prestamo.correoUsuario)}</small>
        </td>
        <td>${celdaLibroConPortada(prestamo.tituloLibro, prestamo.portadaLibro)}</td>
        <td>${formatearFecha(prestamo.fechaPrestamo)}</td>
        <td>${formatearFecha(prestamo.fechaLimite)}</td>
        <td>${pastillaEstado("prestamo", estadoVisiblePrestamo(prestamo))}</td>
        <td data-rol="administrador" hidden>
            ${puedeAnular ? '<button type="button" class="btn btn-outline-danger btn-sm boton-anular-prestamo">Anular</button>' : "—"}
        </td>
    `;
    mostrarSegunRol(fila);

    if (puedeAnular) {
        fila.querySelector(".boton-anular-prestamo").addEventListener("click", (evento) => {
            anularPrestamo(prestamo, evento.currentTarget);
        });
    }
    return fila;
}

/* Aplica los buscadores y el filtro de estado a todosLosPrestamos. */
function prestamosFiltrados() {
    const textoUsuario = document.getElementById("buscar-usuario").value.trim().toLowerCase();
    const textoLibro = document.getElementById("buscar-libro").value.trim().toLowerCase();
    const estado = document.getElementById("filtro-estado-prestamo").value;

    return todosLosPrestamos.filter((prestamo) => {
        const lector = (prestamo.nombreUsuario + " " + prestamo.correoUsuario).toLowerCase();
        const coincideUsuario = lector.includes(textoUsuario);
        const coincideLibro = prestamo.tituloLibro.toLowerCase().includes(textoLibro);
        // Un vencido también es ACTIVO: "Activos" los incluye, "Vencidos" muestra solo esos
        const coincideEstado = !estado
            || (estado === "VENCIDO" ? estaVencido(prestamo) : prestamo.estado === estado);
        return coincideUsuario && coincideLibro && coincideEstado;
    });
}

function pintarPrestamos() {
    const cuerpo = document.getElementById("cuerpo-prestamos");
    const lista = prestamosFiltrados();
    cuerpo.innerHTML = "";

    if (lista.length === 0) {
        cuerpo.innerHTML = '<tr><td colspan="6">No hay préstamos con esos filtros.</td></tr>';
        return;
    }
    lista.forEach((prestamo) => {
        cuerpo.appendChild(crearFilaPrestamo(prestamo));
    });
}

async function cargarPrestamos() {
    try {
        todosLosPrestamos = await api.get("/prestamos");
        pintarPrestamos();
    } catch (error) {
        avisarPrestamos("error", error.message);
    }
}

// ---- Anular (administrador) ----

async function anularPrestamo(prestamo, boton) {
    const motivo = prompt(`Motivo para anular el préstamo de "${prestamo.tituloLibro}" a ${prestamo.nombreUsuario}:`);
    if (motivo === null) {
        return; // canceló el cuadro
    }
    boton.disabled = true;
    try {
        await api.put("/prestamos?id=" + prestamo.id + "&accion=anular", { motivo: motivo });
        avisarPrestamos("exito", `Se anuló el préstamo de "${prestamo.tituloLibro}". La copia volvió al inventario.`);
        await cargarPrestamos();
    } catch (error) {
        avisarPrestamos("error", error.message);
        boton.disabled = false;
    }
}

// ---- Préstamo en el mostrador ----

/* Llena el select de libros con los que tienen copias disponibles. */
async function cargarLibrosDisponibles() {
    const select = document.getElementById("select-libro-prestamo");
    select.innerHTML = "";
    try {
        const libros = await api.get("/libros?soloDisponibles=true");
        if (libros.length === 0) {
            select.innerHTML = '<option value="">No hay libros con copias disponibles</option>';
            return;
        }
        libros.forEach((libro) => {
            const opcion = document.createElement("option");
            opcion.value = libro.id;
            opcion.textContent = `${libro.titulo} — ${libro.autores.join(", ")} (${libro.stockDisponible} disp.)`;
            select.appendChild(opcion);
        });
    } catch (error) {
        avisarPrestamos("error", "No se pudieron cargar los libros: " + error.message);
    }
}

function mostrarPanelPrestamo(mostrar) {
    document.getElementById("panel-registrar-prestamo").hidden = !mostrar;
    if (mostrar) {
        cargarLibrosDisponibles();
        document.getElementById("buscar-lector-prestamo").focus();
    }
}

async function registrarPrestamo(evento) {
    evento.preventDefault();
    const idUsuario = document.getElementById("select-lector-prestamo").value;
    const idLibro = document.getElementById("select-libro-prestamo").value;
    if (!idUsuario || !idLibro) {
        avisarPrestamos("error", "Elige el lector y el libro.");
        return;
    }

    const boton = document.getElementById("boton-confirmar-prestamo");
    boton.disabled = true;
    try {
        const prestamo = await api.post("/prestamos", { idUsuario: Number(idUsuario), idLibro: Number(idLibro) });
        avisarPrestamos("exito", `Préstamo registrado: "${prestamo.tituloLibro}" para ${prestamo.nombreUsuario}. `
            + `Debe devolverlo el ${formatearFecha(prestamo.fechaLimite)}.`);
        mostrarPanelPrestamo(false);
        await cargarPrestamos();
    } catch (error) {
        // Por ejemplo: "El lector tiene préstamos vencidos sin devolver."
        avisarPrestamos("error", error.message);
    }
    boton.disabled = false;
}

// Espera a saber quién inició sesión (Auth.js) antes de pintar
sesionLista.then(() => {
    if (!paginaPermitida(["bibliotecario", "administrador"], "prestamos-error")) {
        return;
    }
    mostrarSegunRol();

    document.getElementById("buscar-usuario").addEventListener("input", pintarPrestamos);
    document.getElementById("buscar-libro").addEventListener("input", pintarPrestamos);
    document.getElementById("filtro-estado-prestamo").addEventListener("change", pintarPrestamos);

    conectarBuscadorLectores("buscar-lector-prestamo", "boton-buscar-lector-prestamo",
        "select-lector-prestamo", "prestamos-error");
    document.getElementById("boton-registrar-prestamo").addEventListener("click", () => mostrarPanelPrestamo(true));
    document.getElementById("boton-cancelar-prestamo").addEventListener("click", () => mostrarPanelPrestamo(false));
    document.getElementById("form-registrar-prestamo").addEventListener("submit", registrarPrestamo);

    cargarPrestamos();
});
