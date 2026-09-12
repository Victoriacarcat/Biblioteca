/** MisPrestamos.js
Vista del Lector: tabla de "mis préstamos" a partir del
arreglo "prestamos" (Data.js). El botón "Renovar" es solo visual por
ahora: no hay backend que actualice fechas todavía.

TODO: cuando exista el backend, esta lista se filtrará por el usuario
autenticado en vez de mostrar todos los préstamos de ejemplo.

"Vencido" alerta en rojo, "Devuelto"
 queda neutro (ya cerrado) y cualquier otro ("Activo") en verde. */
function claseEstadoPrestamo(estado) {
    if (estado === "Vencido") {
        return "estado-negativo";
    }
    if (estado === "Devuelto") {
        return "estado-neutro";
    }
    return "estado-positivo";
}

function crearFilaMisPrestamos(prestamo) {
    const fila = document.createElement("tr");
    // Regla de negocio: un préstamo vencido no se puede renovar, primero
    // hay que devolverlo (y pagar la sanción si corresponde).
    const noSePuedeRenovar = prestamo.estado === "Vencido";

    fila.innerHTML = `
        <td>${celdaLibroConPortada(prestamo.libro)}</td>
        <td>${prestamo.fechaPrestamo}</td>
        <td>${prestamo.fechaLimite}</td>
        <td><span class="estado-prestamo ${claseEstadoPrestamo(prestamo.estado)}">${prestamo.estado}</span></td>
        <td>
            <button type="button" class="btn btn-outline-primary btn-sm boton-renovar" ${noSePuedeRenovar ? "disabled" : ""}>
                Renovar
            </button>
        </td>
    `;

    if (noSePuedeRenovar) {
        fila.title = "Este préstamo está vencido: no se puede renovar hasta devolverlo.";
    }

    fila.querySelector(".boton-renovar").addEventListener("click", () => {
        alert("Renovación simulada: esta acción no está conectada a un backend todavía.");
    });

    return fila;
}

function pintarMisPrestamos() {
    const cuerpo = document.getElementById("cuerpo-mis-prestamos");
    cuerpo.innerHTML = "";
    prestamos.forEach((prestamo) => {
        cuerpo.appendChild(crearFilaMisPrestamos(prestamo));
    });
}

document.addEventListener("DOMContentLoaded", () => {
    pintarMisPrestamos();
});
