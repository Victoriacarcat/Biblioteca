/** Sanciones.js
Igual que Reservas.js: un bloque para el Lector (sus propias sanciones,
solo lectura) y otro para Bibliotecario/Administrador.

Bloque del Lector:
    GET /api/sanciones -> sus sanciones, la más reciente primero

Bloque del personal:
    GET  /api/sanciones?estado=PENDIENTE                -> todas (el servidor filtra por estado)
    POST /api/sanciones {idUsuario, tipo, importe, detalle} -> sanción a mano (LIBRO_DANADO u OTRO)
    PUT  /api/sanciones?id=5&accion=pagar               -> marca como pagada
    PUT  /api/sanciones?id=5&accion=anular {motivo}     -> anula (solo administrador)
La mora y la pérdida no se registran aquí: las genera el backend al devolver.
*/

// ---- Compartido ----

/*
 Motivo de la sanción: el tipo y, si está ligada a un préstamo, el libro.
 Debajo, el detalle que guardó el sistema o el personal (por ejemplo,
 "Devolución con 3 días de retraso.").
*/
function celdaMotivoSancion(sancion) {
    let html = escaparHtml(textoTipoSancion(sancion.tipo));
    if (sancion.tituloLibro) {
        html += " · " + escaparHtml(sancion.tituloLibro);
    }
    if (sancion.detalle) {
        html += `<small class="d-block">${escaparHtml(sancion.detalle)}</small>`;
    }
    return html;
}

function avisarSanciones(tipo, mensaje) {
    ocultarError("sanciones-exito");
    ocultarError("sanciones-error");
    if (tipo === "exito") {
        mostrarExito("sanciones-exito", mensaje);
    } else {
        mostrarError("sanciones-error", mensaje);
    }
}

// ---- Vista Lector ----

function crearFilaMiSancion(sancion) {
    const fila = document.createElement("tr");
    fila.innerHTML = `
        <td>
            ${celdaMotivoSancion(sancion)}
            <small class="d-block">Emitida el ${formatearFecha(sancion.fechaEmision)}</small>
        </td>
        <td>${pastillaEstado("sancion", sancion.estado)}</td>
        <td>${formatearDinero(sancion.importe)}</td>
    `;
    return fila;
}

async function cargarMisSanciones() {
    const cuerpo = document.getElementById("cuerpo-mis-sanciones");
    try {
        const misSanciones = await api.get("/sanciones");
        cuerpo.innerHTML = "";
        if (misSanciones.length === 0) {
            cuerpo.innerHTML = '<tr><td colspan="3">No tienes sanciones.</td></tr>';
            return;
        }
        misSanciones.forEach((sancion) => {
            cuerpo.appendChild(crearFilaMiSancion(sancion));
        });
    } catch (error) {
        avisarSanciones("error", error.message);
    }
}

// ---- Vista Bibliotecario/Administrador ----

/* Solo las PENDIENTES se pueden pagar o anular. Anular es exclusivo del administrador. */
function botonesSancionStaff(sancion) {
    if (sancion.estado !== "PENDIENTE") {
        return "—";
    }
    return `
        <button type="button" class="btn btn-outline-success btn-sm boton-marcar-pagada">Marcar como pagada</button>
        <button type="button" class="btn btn-outline-danger btn-sm boton-anular-sancion" data-rol="administrador" hidden>Anular</button>
    `;
}

function crearFilaSancionStaff(sancion) {
    const fila = document.createElement("tr");
    fila.innerHTML = `
        <td>
            ${celdaUsuarioConIcono(sancion.nombreUsuario)}
            <small class="d-block">${escaparHtml(sancion.correoUsuario)}</small>
        </td>
        <td>${celdaMotivoSancion(sancion)}</td>
        <td>${formatearFecha(sancion.fechaEmision)}</td>
        <td>${pastillaEstado("sancion", sancion.estado)}</td>
        <td>${formatearDinero(sancion.importe)}</td>
        <td>${botonesSancionStaff(sancion)}</td>
    `;
    mostrarSegunRol(fila);

    const botonPagar = fila.querySelector(".boton-marcar-pagada");
    if (botonPagar) {
        botonPagar.addEventListener("click", () => pagarSancion(sancion, botonPagar));
        const botonAnular = fila.querySelector(".boton-anular-sancion");
        botonAnular.addEventListener("click", () => anularSancion(sancion, botonAnular));
    }
    return fila;
}

async function cargarSancionesStaff() {
    const estado = document.getElementById("filtro-estado-sancion").value;
    const cuerpo = document.getElementById("cuerpo-sanciones");
    try {
        const lista = await api.get("/sanciones" + (estado ? "?estado=" + estado : ""));
        cuerpo.innerHTML = "";
        if (lista.length === 0) {
            cuerpo.innerHTML = '<tr><td colspan="6">No hay sanciones con ese estado.</td></tr>';
            return;
        }
        lista.forEach((sancion) => {
            cuerpo.appendChild(crearFilaSancionStaff(sancion));
        });
    } catch (error) {
        avisarSanciones("error", error.message);
    }
}

async function pagarSancion(sancion, boton) {
    if (!confirm(`¿Registrar el pago de ${formatearDinero(sancion.importe)} de ${sancion.nombreUsuario}?`)) {
        return;
    }
    boton.disabled = true;
    try {
        await api.put("/sanciones?id=" + sancion.id + "&accion=pagar");
        avisarSanciones("exito", `Pago registrado: ${formatearDinero(sancion.importe)} de ${sancion.nombreUsuario}.`);
        await cargarSancionesStaff();
    } catch (error) {
        avisarSanciones("error", error.message);
        boton.disabled = false;
    }
}

async function anularSancion(sancion, boton) {
    const motivo = prompt(`Motivo para anular la sanción de ${sancion.nombreUsuario} (${formatearDinero(sancion.importe)}):`);
    if (motivo === null) {
        return; // canceló el cuadro
    }
    boton.disabled = true;
    try {
        await api.put("/sanciones?id=" + sancion.id + "&accion=anular", { motivo: motivo });
        avisarSanciones("exito", `Se anuló la sanción de ${sancion.nombreUsuario}.`);
        await cargarSancionesStaff();
    } catch (error) {
        avisarSanciones("error", error.message);
        boton.disabled = false;
    }
}

// ---- Registrar sanción a mano ----

function mostrarPanelSancion(mostrar) {
    document.getElementById("panel-registrar-sancion").hidden = !mostrar;
    if (mostrar) {
        document.getElementById("buscar-lector-sancion").focus();
    } else {
        document.getElementById("form-registrar-sancion").reset();
    }
}

async function registrarSancion(evento) {
    evento.preventDefault();
    const idUsuario = document.getElementById("select-lector-sancion").value;
    // Se acepta coma decimal ("15000,50")
    const importe = document.getElementById("importe-sancion").value.trim().replace(",", ".");
    if (!idUsuario) {
        avisarSanciones("error", "Busca y elige al lector.");
        return;
    }
    if (importe === "" || isNaN(Number(importe))) {
        avisarSanciones("error", "Escribe el valor de la sanción como un número, por ejemplo 15000.");
        return;
    }

    const datos = {
        idUsuario: Number(idUsuario),
        tipo: document.getElementById("tipo-sancion").value,
        // Como texto: el backend lo convierte a BigDecimal (sin errores de redondeo)
        importe: importe,
        detalle: document.getElementById("detalle-sancion").value.trim()
    };

    const boton = document.getElementById("boton-confirmar-sancion");
    boton.disabled = true;
    try {
        const sancion = await api.post("/sanciones", datos);
        avisarSanciones("exito", `Sanción registrada: ${textoTipoSancion(sancion.tipo)} de `
            + `${formatearDinero(sancion.importe)} a ${sancion.nombreUsuario}.`);
        mostrarPanelSancion(false);
        await cargarSancionesStaff();
    } catch (error) {
        // Por ejemplo: "Describe el motivo de la sanción."
        avisarSanciones("error", error.message);
    }
    boton.disabled = false;
}

// Espera a saber quién inició sesión (Auth.js) antes de pintar
sesionLista.then(() => {
    if (!paginaPermitida(["lector", "bibliotecario", "administrador"], "sanciones-error")) {
        return;
    }
    // Muestra solo el bloque del rol actual (Utilidades.js)
    mostrarSegunRol();

    if (obtenerRolActual() === "lector") {
        cargarMisSanciones();
        return;
    }
    document.getElementById("filtro-estado-sancion").addEventListener("change", cargarSancionesStaff);
    conectarBuscadorLectores("buscar-lector-sancion", "boton-buscar-lector-sancion",
        "select-lector-sancion", "sanciones-error");
    document.getElementById("boton-registrar-sancion").addEventListener("click", () => mostrarPanelSancion(true));
    document.getElementById("boton-cancelar-sancion").addEventListener("click", () => mostrarPanelSancion(false));
    document.getElementById("form-registrar-sancion").addEventListener("submit", registrarSancion);
    cargarSancionesStaff();
});
