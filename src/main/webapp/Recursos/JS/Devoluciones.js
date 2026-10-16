/** Devoluciones.js
Vista de Bibliotecario/Administrador: préstamos activos que faltan por
devolver, para registrar la devolución o la pérdida del libro.

    GET /api/prestamos?estado=ACTIVO                          -> préstamos sin devolver
    PUT /api/prestamos?id=5&accion=devolver                   -> registra la devolución
    PUT /api/prestamos?id=5&accion=perdido {importe, detalle} -> el lector perdió el libro

Al devolver tarde, el backend genera solo la sanción por mora (500 COP por
día de retraso). Al marcar como perdido, genera la sanción con el valor
indicado. Si el libro vuelve dañado, la sanción se registra en Sanciones.
*/

// Préstamos activos que mandó el servidor
let prestamosPorDevolver = [];

function avisarDevoluciones(tipo, mensaje) {
    ocultarError("devoluciones-exito");
    ocultarError("devoluciones-error");
    if (tipo === "exito") {
        mostrarExito("devoluciones-exito", mensaje);
    } else {
        mostrarError("devoluciones-error", mensaje);
    }
}

/*
 Días de retraso entre la fecha límite y la de devolución, contando días
 de calendario como el backend (devolver al día siguiente = 1 día).
 Se usan solo las fechas "2026-10-15" en hora UTC para que el cambio de
 hora no altere la cuenta.
*/
function diasDeRetraso(fechaLimite, fechaDevolucion) {
    const limite = Date.parse(fechaLimite.substring(0, 10));
    const devolucion = Date.parse(fechaDevolucion.substring(0, 10));
    return Math.round((devolucion - limite) / (24 * 60 * 60 * 1000));
}

function crearFilaDevolucion(prestamo) {
    const fila = document.createElement("tr");

    fila.innerHTML = `
        <td>
            ${celdaUsuarioConIcono(prestamo.nombreUsuario)}
            <small class="d-block">${escaparHtml(prestamo.correoUsuario)}</small>
        </td>
        <td>${celdaLibroConPortada(prestamo.tituloLibro, prestamo.portadaLibro)}</td>
        <td>${formatearFecha(prestamo.fechaLimite)}</td>
        <td>${pastillaEstado("prestamo", estadoVisiblePrestamo(prestamo))}</td>
        <td>
            <button type="button" class="btn btn-primary btn-sm boton-registrar-devolucion">Registrar devolución</button>
            <button type="button" class="btn btn-outline-danger btn-sm boton-marcar-perdido">Perdido</button>
        </td>
    `;

    fila.querySelector(".boton-registrar-devolucion").addEventListener("click", (evento) => {
        registrarDevolucion(prestamo, evento.currentTarget);
    });
    fila.querySelector(".boton-marcar-perdido").addEventListener("click", (evento) => {
        marcarPerdido(prestamo, evento.currentTarget);
    });
    return fila;
}

function pintarDevoluciones() {
    const texto = document.getElementById("buscar-prestamo").value.trim().toLowerCase();
    const lista = prestamosPorDevolver.filter((prestamo) =>
        (prestamo.nombreUsuario + " " + prestamo.correoUsuario + " " + prestamo.tituloLibro)
            .toLowerCase().includes(texto));

    const cuerpo = document.getElementById("cuerpo-devoluciones");
    cuerpo.innerHTML = "";
    if (lista.length === 0) {
        cuerpo.innerHTML = '<tr><td colspan="5">No hay préstamos pendientes de devolución.</td></tr>';
        return;
    }
    lista.forEach((prestamo) => {
        cuerpo.appendChild(crearFilaDevolucion(prestamo));
    });
}

async function cargarDevoluciones() {
    try {
        prestamosPorDevolver = await api.get("/prestamos?estado=ACTIVO");
        // Los más urgentes primero: fecha límite más cercana (o ya pasada)
        prestamosPorDevolver.sort((a, b) => a.fechaLimite.localeCompare(b.fechaLimite));
        pintarDevoluciones();
    } catch (error) {
        avisarDevoluciones("error", error.message);
    }
}

async function registrarDevolucion(prestamo, boton) {
    if (!confirm(`¿Registrar la devolución de "${prestamo.tituloLibro}" de ${prestamo.nombreUsuario}?`)) {
        return;
    }
    boton.disabled = true;
    try {
        const devuelto = await api.put("/prestamos?id=" + prestamo.id + "&accion=devolver");
        const dias = diasDeRetraso(devuelto.fechaLimite, devuelto.fechaDevolucion);
        let mensaje = `Devolución registrada: "${devuelto.tituloLibro}".`;
        if (dias > 0) {
            mensaje += ` Se devolvió con ${dias} ${dias === 1 ? "día" : "días"} de retraso: `
                + "se generó la sanción por mora (puedes verla en Sanciones).";
        }
        avisarDevoluciones("exito", mensaje);
        await cargarDevoluciones();
    } catch (error) {
        avisarDevoluciones("error", error.message);
        boton.disabled = false;
    }
}

async function marcarPerdido(prestamo, boton) {
    const importe = prompt(`"${prestamo.tituloLibro}" se marcará como perdido y saldrá del inventario.\n`
        + `Valor a cobrar a ${prestamo.nombreUsuario} (COP):`);
    if (importe === null) {
        return; // canceló el cuadro
    }
    // Se acepta coma decimal ("15000,50"); si no es un número, se avisa sin llamar al servidor
    const importeLimpio = importe.trim().replace(",", ".");
    if (importeLimpio === "" || isNaN(Number(importeLimpio))) {
        avisarDevoluciones("error", "Escribe el valor a cobrar como un número, por ejemplo 45000.");
        return;
    }
    const detalle = prompt("Detalle (opcional):", "") ?? "";

    boton.disabled = true;
    try {
        // El importe va como texto: el backend lo convierte a BigDecimal y valida que sea mayor que cero
        await api.put("/prestamos?id=" + prestamo.id + "&accion=perdido",
            { importe: importeLimpio, detalle: detalle });
        avisarDevoluciones("exito", `"${prestamo.tituloLibro}" quedó como perdido y se generó la sanción.`);
        await cargarDevoluciones();
    } catch (error) {
        avisarDevoluciones("error", error.message);
        boton.disabled = false;
    }
}

// Espera a saber quién inició sesión (Auth.js) antes de pintar
sesionLista.then(() => {
    if (!paginaPermitida(["bibliotecario", "administrador"], "devoluciones-error")) {
        return;
    }
    document.getElementById("buscar-prestamo").addEventListener("input", pintarDevoluciones);
    cargarDevoluciones();
});
