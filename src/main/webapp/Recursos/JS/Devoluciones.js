/** Devoluciones.js
Vista de Bibliotecario/Administrador: lista los préstamos que aún no
se han devuelto (arreglo "prestamos" de Data.js, filtrando por
estado) y permite "registrar" su devolución. Como no hay backend
todavía, registrar la devolución solo cambia el estado en memoria
(se pierde al recargar la página).*/

function prestamosPendientesDeDevolucion() {
    return prestamos.filter((prestamo) => prestamo.estado !== "Devuelto");
}


function claseEstadoPrestamo(estado) {
    if (estado === "Vencido") {
        return "estado-negativo";
    }
    if (estado === "Devuelto") {
        return "estado-neutro";
    }
    return "estado-positivo";
}

function crearFilaDevolucion(prestamo) {
    const fila = document.createElement("tr");

    fila.innerHTML = `
        <td>${celdaUsuarioConIcono(prestamo.usuario)}</td>
        <td>${celdaLibroConPortada(prestamo.libro)}</td>
        <td>${prestamo.fechaLimite}</td>
        <td><span class="estado-prestamo ${claseEstadoPrestamo(prestamo.estado)}">${prestamo.estado}</span></td>
        <td><button type="button" class="btn btn-primary btn-sm boton-registrar-devolucion">Registrar devolución</button></td>
    `;

    fila.querySelector(".boton-registrar-devolucion").addEventListener("click", () => {
        prestamo.estado = "Devuelto";
        filtrarDevoluciones();
    });

    return fila;
}

function pintarDevoluciones(listaPrestamos) {
    const cuerpo = document.getElementById("cuerpo-devoluciones");
    cuerpo.innerHTML = "";
    listaPrestamos.forEach((prestamo) => {
        cuerpo.appendChild(crearFilaDevolucion(prestamo));
    });
}

function filtrarDevoluciones() {
    const texto = document.getElementById("buscar-prestamo").value.trim().toLowerCase();

    const pendientesFiltrados = prestamosPendientesDeDevolucion().filter((prestamo) =>
        prestamo.usuario.toLowerCase().includes(texto) ||
        prestamo.libro.toLowerCase().includes(texto)
    );

    pintarDevoluciones(pendientesFiltrados);
}

document.addEventListener("DOMContentLoaded", () => {
    pintarDevoluciones(prestamosPendientesDeDevolucion());
    document.getElementById("buscar-prestamo").addEventListener("input", filtrarDevoluciones);
});
