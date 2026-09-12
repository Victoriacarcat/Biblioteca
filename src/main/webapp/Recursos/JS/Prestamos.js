// Prestamos.js
// Vista de Bibliotecario/Administrador: lista todos los préstamos
// (arreglo "prestamos" de Data.js) y permite filtrar escribiendo en los
// buscadores de usuario y de libro. "Registrar préstamo" es solo visual
// por ahora: no hay backend ni formulario de creación real todavía.

// Color de la pastilla de estado: "Vencido" alerta en rojo, "Devuelto"
// queda neutro (ya cerrado) y cualquier otro ("Activo") en verde.
function claseEstadoPrestamo(estado) {
    if (estado === "Vencido") {
        return "estado-negativo";
    }
    if (estado === "Devuelto") {
        return "estado-neutro";
    }
    return "estado-positivo";
}

function crearFilaPrestamo(prestamo) {
    const fila = document.createElement("tr");

    fila.innerHTML = `
        <td>${celdaUsuarioConIcono(prestamo.usuario)}</td>
        <td>${celdaLibroConPortada(prestamo.libro)}</td>
        <td>${prestamo.fechaPrestamo}</td>
        <td>${prestamo.fechaLimite}</td>
        <td><span class="estado-prestamo ${claseEstadoPrestamo(prestamo.estado)}">${prestamo.estado}</span></td>
    `;

    return fila;
}

function pintarPrestamos(listaPrestamos) {
    const cuerpo = document.getElementById("cuerpo-prestamos");
    cuerpo.innerHTML = "";
    listaPrestamos.forEach((prestamo) => {
        cuerpo.appendChild(crearFilaPrestamo(prestamo));
    });
}

function filtrarPrestamos() {
    const textoUsuario = document.getElementById("buscar-usuario").value.trim().toLowerCase();
    const textoLibro = document.getElementById("buscar-libro").value.trim().toLowerCase();

    const prestamosFiltrados = prestamos.filter((prestamo) =>
        prestamo.usuario.toLowerCase().includes(textoUsuario) &&
        prestamo.libro.toLowerCase().includes(textoLibro)
    );

    pintarPrestamos(prestamosFiltrados);
}

document.addEventListener("DOMContentLoaded", () => {
    pintarPrestamos(prestamos);

    document.getElementById("buscar-usuario").addEventListener("input", filtrarPrestamos);
    document.getElementById("buscar-libro").addEventListener("input", filtrarPrestamos);

    document.getElementById("boton-registrar-prestamo").addEventListener("click", () => {
        alert("Registro de préstamo simulado: esta acción no está conectada a un backend todavía.");
    });
});
