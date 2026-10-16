/** MisPrestamos.js
Vista del Lector: sus préstamos activos.

    GET /api/prestamos                    -> todos sus préstamos (activos e históricos)
    PUT /api/prestamos?id=5&accion=renovar -> renueva uno

Aquí se muestran solo los ACTIVOS; los devueltos o perdidos van en
Historial. "Vencido" no es un estado del backend: es un préstamo ACTIVO
con la fecha límite ya pasada (estadoVisiblePrestamo, en Utilidades.js).
*/

// Debe coincidir con ReglasBiblioteca.MAX_RENOVACIONES del backend.
// Solo se usa para mostrar "1 de 2" y deshabilitar el botón: quien decide es el servidor.
const MAX_RENOVACIONES = 2;

function crearFilaMisPrestamos(prestamo) {
    const fila = document.createElement("tr");
    const vencido = estaVencido(prestamo);
    const sinRenovaciones = prestamo.renovaciones >= MAX_RENOVACIONES;
    // Regla de negocio: un préstamo vencido no se puede renovar, primero hay que devolverlo
    const noSePuedeRenovar = vencido || sinRenovaciones;

    fila.innerHTML = `
        <td>${celdaLibroConPortada(prestamo.tituloLibro, prestamo.portadaLibro)}</td>
        <td>${formatearFecha(prestamo.fechaPrestamo)}</td>
        <td>${formatearFecha(prestamo.fechaLimite)}</td>
        <td>${pastillaEstado("prestamo", estadoVisiblePrestamo(prestamo))}</td>
        <td>
            <button type="button" class="btn btn-outline-primary btn-sm boton-renovar" ${noSePuedeRenovar ? "disabled" : ""}>
                Renovar
            </button>
            <small class="d-block">${prestamo.renovaciones} de ${MAX_RENOVACIONES} renovaciones</small>
        </td>
    `;

    if (vencido) {
        fila.title = "Este préstamo está vencido: no se puede renovar hasta devolverlo.";
    } else if (sinRenovaciones) {
        fila.title = "Ya usaste todas las renovaciones de este préstamo.";
    }

    fila.querySelector(".boton-renovar").addEventListener("click", (evento) => {
        renovarPrestamo(prestamo, evento.currentTarget);
    });

    return fila;
}

async function renovarPrestamo(prestamo, boton) {
    ocultarError("mis-prestamos-exito");
    ocultarError("mis-prestamos-error");
    boton.disabled = true;
    try {
        const renovado = await api.put("/prestamos?id=" + prestamo.id + "&accion=renovar");
        mostrarExito("mis-prestamos-exito",
            `Renovaste "${renovado.tituloLibro}". Nueva fecha límite: ${formatearFecha(renovado.fechaLimite)}.`);
        // Se vuelven a pedir los datos al servidor en vez de cambiarlos a mano
        await cargarMisPrestamos();
    } catch (error) {
        mostrarError("mis-prestamos-error", error.message);
        boton.disabled = false;
    }
}

function pintarMisPrestamos(prestamosActivos) {
    const cuerpo = document.getElementById("cuerpo-mis-prestamos");
    cuerpo.innerHTML = "";

    if (prestamosActivos.length === 0) {
        cuerpo.innerHTML = '<tr><td colspan="5">No tienes préstamos activos.</td></tr>';
        return;
    }
    prestamosActivos.forEach((prestamo) => {
        cuerpo.appendChild(crearFilaMisPrestamos(prestamo));
    });
}

async function cargarMisPrestamos() {
    try {
        const prestamos = await api.get("/prestamos");
        pintarMisPrestamos(prestamos.filter((prestamo) => prestamo.estado === "ACTIVO"));
    } catch (error) {
        mostrarError("mis-prestamos-error", error.message);
    }
}

// Espera a saber quién inició sesión (Auth.js) antes de pintar
sesionLista.then(() => {
    // Para el personal, GET /api/prestamos trae los de todos: esta página es solo del lector
    if (paginaPermitida(["lector"], "mis-prestamos-error")) {
        cargarMisPrestamos();
    }
});
