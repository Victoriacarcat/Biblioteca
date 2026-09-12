/**Historial.js
Vista del Lector: pinta el historial de préstamos ya devueltos
(arreglo "historial" de Data.js). Es de solo lectura, sin acciones.
*/
function claseEstadoHistorial(estado) {
    return estado.includes("retraso") ? "estado-alerta" : "estado-positivo";
}

function crearFilaHistorial(registro) {
    const fila = document.createElement("tr");

    fila.innerHTML = `
        <td>${celdaLibroConPortada(registro.libro)}</td>
        <td>${registro.fechaPrestamo}</td>
        <td>${registro.fechaDevolucion}</td>
        <td><span class="estado-historial ${claseEstadoHistorial(registro.estado)}">${registro.estado}</span></td>
    `;

    return fila;
}

function pintarHistorial() {
    const cuerpo = document.getElementById("cuerpo-historial");
    cuerpo.innerHTML = "";
    historial.forEach((registro) => {
        cuerpo.appendChild(crearFilaHistorial(registro));
    });
}

document.addEventListener("DOMContentLoaded", () => {
    pintarHistorial();
});
