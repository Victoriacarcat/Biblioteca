/** Historial.js
Vista del Lector: sus préstamos ya cerrados (devueltos o perdidos).

    GET /api/prestamos -> todos sus préstamos; aquí se dejan los DEVUELTO y PERDIDO.

Los ANULADOS no se muestran: fueron registros hechos por error.
Es de solo lectura, sin acciones.
*/

/*
 true si se devolvió después del día límite. Se comparan solo las fechas
 ("2026-10-15"), como hace el backend al calcular la mora: devolver el
 mismo día de la fecha límite, a cualquier hora, es devolver a tiempo.
*/
function devueltoConRetraso(prestamo) {
    return prestamo.fechaDevolucion.substring(0, 10) > prestamo.fechaLimite.substring(0, 10);
}

/* La pastilla de la columna Estado. */
function estadoHistorial(prestamo) {
    if (prestamo.estado === "PERDIDO") {
        return pastillaEstado("prestamo", "PERDIDO");
    }
    return devueltoConRetraso(prestamo)
        ? '<span class="estado-historial estado-alerta">Devuelto con retraso</span>'
        : '<span class="estado-historial estado-positivo">Devuelto a tiempo</span>';
}

function crearFilaHistorial(prestamo) {
    const fila = document.createElement("tr");
    fila.innerHTML = `
        <td>${celdaLibroConPortada(prestamo.tituloLibro, prestamo.portadaLibro)}</td>
        <td>${formatearFecha(prestamo.fechaPrestamo)}</td>
        <td>${formatearFecha(prestamo.fechaDevolucion)}</td>
        <td>${estadoHistorial(prestamo)}</td>
    `;
    return fila;
}

function pintarHistorial(prestamosCerrados) {
    const cuerpo = document.getElementById("cuerpo-historial");
    cuerpo.innerHTML = "";

    if (prestamosCerrados.length === 0) {
        cuerpo.innerHTML = '<tr><td colspan="4">Todavía no has devuelto ningún libro.</td></tr>';
        return;
    }
    prestamosCerrados.forEach((prestamo) => {
        cuerpo.appendChild(crearFilaHistorial(prestamo));
    });
}

async function cargarHistorial() {
    try {
        // El backend ya los manda del más reciente al más antiguo
        const prestamos = await api.get("/prestamos");
        pintarHistorial(prestamos.filter((p) => p.estado === "DEVUELTO" || p.estado === "PERDIDO"));
    } catch (error) {
        mostrarError("historial-error", error.message);
    }
}

// Espera a saber quién inició sesión (Auth.js) antes de pintar
sesionLista.then(() => {
    // Para el personal, GET /api/prestamos trae los de todos: esta página es solo del lector
    if (paginaPermitida(["lector"], "historial-error")) {
        cargarHistorial();
    }
});
