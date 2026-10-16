/* Reservas.js
Una sola página con dos bloques: "Mis reservas" (Lector) y "Todas las
reservas" (Bibliotecario/Administrador). Se muestra solo el bloque que
corresponde al rol actual (atributo data-rol, ver mostrarSegunRol).

Bloque del Lector (datos reales):
    GET /api/reservas                        -> sus reservas, la más reciente primero
    PUT /api/reservas?id=5&accion=cancelar   -> cancela una PENDIENTE o APROBADA

Bloque del personal:
    GET  /api/reservas?estado=PENDIENTE      -> todas (el servidor filtra por estado)
    PUT  /api/reservas?id=5&accion=aprobar   -> aparta una copia (plazo de retiro: 3 días)
    PUT  /api/reservas?id=5&accion=rechazar {observaciones}
    POST /api/prestamos {idReserva}          -> entrega el libro de una reserva APROBADA
*/

// ---- Vista Lector ----

/* Una reserva se puede cancelar mientras está pendiente o aprobada. */
function sePuedeCancelar(reserva) {
    return reserva.estado === "PENDIENTE" || reserva.estado === "APROBADA";
}

/* Estado de la reserva; si está aprobada, recuerda hasta cuándo se puede retirar el libro. */
function celdaEstadoReserva(reserva) {
    let html = pastillaEstado("reserva", reserva.estado);
    if (reserva.estado === "APROBADA" && reserva.fechaLimiteRetiro) {
        html += `<small class="d-block">Retíralo antes del ${formatearFechaHora(reserva.fechaLimiteRetiro)}</small>`;
    }
    if (reserva.observaciones) {
        html += `<small class="d-block">${escaparHtml(reserva.observaciones)}</small>`;
    }
    return html;
}

function crearFilaMiReserva(reserva) {
    const fila = document.createElement("tr");
    fila.innerHTML = `
        <td>${celdaLibroConPortada(reserva.tituloLibro)}</td>
        <td>${formatearFecha(reserva.fechaReserva)}</td>
        <td>${celdaEstadoReserva(reserva)}</td>
        <td>
            <button type="button" class="btn btn-outline-danger btn-sm boton-cancelar-reserva"
                    ${sePuedeCancelar(reserva) ? "" : "disabled"}>Cancelar</button>
        </td>
    `;

    fila.querySelector(".boton-cancelar-reserva").addEventListener("click", (evento) => {
        cancelarReserva(reserva, evento.currentTarget);
    });
    return fila;
}

async function cancelarReserva(reserva, boton) {
    if (!confirm(`¿Cancelar tu reserva de "${reserva.tituloLibro}"?`)) {
        return;
    }
    ocultarError("reservas-exito");
    ocultarError("reservas-error");
    boton.disabled = true;
    try {
        await api.put("/reservas?id=" + reserva.id + "&accion=cancelar");
        mostrarExito("reservas-exito", `Cancelaste tu reserva de "${reserva.tituloLibro}".`);
        await cargarMisReservas();
    } catch (error) {
        mostrarError("reservas-error", error.message);
        boton.disabled = false;
    }
}

async function cargarMisReservas() {
    const cuerpo = document.getElementById("cuerpo-mis-reservas");
    try {
        const misReservas = await api.get("/reservas");
        cuerpo.innerHTML = "";
        if (misReservas.length === 0) {
            cuerpo.innerHTML = '<tr><td colspan="4">No tienes reservas. Puedes reservar desde el catálogo.</td></tr>';
            return;
        }
        misReservas.forEach((reserva) => {
            cuerpo.appendChild(crearFilaMiReserva(reserva));
        });
    } catch (error) {
        mostrarError("reservas-error", error.message);
    }
}

// ---- Vista Bibliotecario/Administrador ----

/* Avisos para el personal (usa las mismas cajas que el lector). */
function avisarReservas(tipo, mensaje) {
    ocultarError("reservas-exito");
    ocultarError("reservas-error");
    if (tipo === "exito") {
        mostrarExito("reservas-exito", mensaje);
    } else {
        mostrarError("reservas-error", mensaje);
    }
}

/*
 Botones según el estado: una PENDIENTE se aprueba o rechaza; una APROBADA
 (copia apartada) se entrega al lector, lo que crea el préstamo.
 Las demás ya están cerradas.
*/
function botonesReservaStaff(reserva) {
    if (reserva.estado === "PENDIENTE") {
        return `
            <button type="button" class="btn btn-success btn-sm boton-aprobar-reserva">Aprobar</button>
            <button type="button" class="btn btn-outline-danger btn-sm boton-rechazar-reserva">Rechazar</button>
        `;
    }
    if (reserva.estado === "APROBADA") {
        return '<button type="button" class="btn btn-primary btn-sm boton-entregar-reserva">Entregar</button>';
    }
    return "—";
}

function crearFilaReservaStaff(reserva) {
    const fila = document.createElement("tr");
    fila.innerHTML = `
        <td>${celdaUsuarioConIcono(reserva.nombreUsuario)}</td>
        <td>${celdaLibroConPortada(reserva.tituloLibro)}</td>
        <td>${formatearFecha(reserva.fechaReserva)}</td>
        <td>${celdaEstadoReserva(reserva)}</td>
        <td>${botonesReservaStaff(reserva)}</td>
    `;

    const botonAprobar = fila.querySelector(".boton-aprobar-reserva");
    if (botonAprobar) {
        botonAprobar.addEventListener("click", () => aprobarReserva(reserva, botonAprobar));
        const botonRechazar = fila.querySelector(".boton-rechazar-reserva");
        botonRechazar.addEventListener("click", () => rechazarReserva(reserva, botonRechazar));
    }
    const botonEntregar = fila.querySelector(".boton-entregar-reserva");
    if (botonEntregar) {
        botonEntregar.addEventListener("click", () => entregarReserva(reserva, botonEntregar));
    }
    return fila;
}

/* Pide las reservas con el estado del filtro (el servidor filtra) y las pinta. */
async function cargarReservasStaff() {
    const estado = document.getElementById("filtro-estado-reserva").value;
    const cuerpo = document.getElementById("cuerpo-reservas");
    try {
        const lista = await api.get("/reservas" + (estado ? "?estado=" + estado : ""));
        cuerpo.innerHTML = "";
        if (lista.length === 0) {
            cuerpo.innerHTML = '<tr><td colspan="5">No hay reservas con ese estado.</td></tr>';
            return;
        }
        lista.forEach((reserva) => {
            cuerpo.appendChild(crearFilaReservaStaff(reserva));
        });
    } catch (error) {
        avisarReservas("error", error.message);
    }
}

/*
 Las tres acciones siguen el mismo patrón: deshabilitar el botón, llamar al
 servidor, avisar y volver a cargar la lista.
*/
async function ejecutarAccionReserva(boton, peticion, mensajeExito) {
    boton.disabled = true;
    try {
        await peticion();
        avisarReservas("exito", mensajeExito);
        await cargarReservasStaff();
    } catch (error) {
        // Por ejemplo: "No hay copias disponibles. La reserva sigue pendiente..."
        avisarReservas("error", error.message);
        boton.disabled = false;
    }
}

function aprobarReserva(reserva, boton) {
    ejecutarAccionReserva(boton,
        () => api.put("/reservas?id=" + reserva.id + "&accion=aprobar"),
        `Aprobaste la reserva de "${reserva.tituloLibro}". Se apartó una copia para ${reserva.nombreUsuario}.`);
}

function rechazarReserva(reserva, boton) {
    const observaciones = prompt(`Rechazar la reserva de "${reserva.tituloLibro}".\nMotivo para el lector (opcional):`, "");
    if (observaciones === null) {
        return; // canceló el cuadro
    }
    ejecutarAccionReserva(boton,
        () => api.put("/reservas?id=" + reserva.id + "&accion=rechazar", { observaciones: observaciones }),
        `Rechazaste la reserva de "${reserva.tituloLibro}".`);
}

function entregarReserva(reserva, boton) {
    if (!confirm(`¿Entregar "${reserva.tituloLibro}" a ${reserva.nombreUsuario}? Se registrará el préstamo.`)) {
        return;
    }
    ejecutarAccionReserva(boton,
        () => api.post("/prestamos", { idReserva: reserva.id }),
        `Entregaste "${reserva.tituloLibro}" a ${reserva.nombreUsuario}. El préstamo quedó registrado.`);
}

// Espera a saber quién inició sesión (Auth.js) antes de pintar
sesionLista.then(() => {
    if (!paginaPermitida(["lector", "bibliotecario", "administrador"], "reservas-error")) {
        return;
    }
    // Muestra solo el bloque del rol actual (Utilidades.js)
    mostrarSegunRol();

    if (obtenerRolActual() === "lector") {
        cargarMisReservas();
    } else {
        document.getElementById("filtro-estado-reserva").addEventListener("change", cargarReservasStaff);
        cargarReservasStaff();
    }
});
