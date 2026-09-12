// Reservas.js
// Una sola página con dos bloques: "Mis reservas" (Lector) y "Todas las
// reservas" (Bibliotecario/Administrador). Se muestra solo el bloque que
// corresponde al rol actual, con el mismo patrón de data-rol que usan
// Header.js y Detallelibro.js. Todo viene del arreglo "reservas" de
// Data.js: como no hay backend todavía, cancelar/aprobar/rechazar solo
// cambian el estado en memoria (se pierde al recargar la página).

// Color de la pastilla de estado: "Aprobada" verde, "Vencida" roja,
// "Pendiente" ámbar y "Cancelada" gris.
function claseEstadoReserva(estado) {
    if (estado === "Aprobada") {
        return "estado-positivo";
    }
    if (estado === "Vencida") {
        return "estado-negativo";
    }
    if (estado === "Pendiente") {
        return "estado-alerta";
    }
    return "estado-neutro";
}

function mostrarBloqueDelRolActual() {
    const rolActual = obtenerRolActual();

    document.querySelectorAll("[data-rol]").forEach((elemento) => {
        const rolesPermitidos = elemento.getAttribute("data-rol").split(",");
        elemento.hidden = !rolesPermitidos.includes(rolActual);
    });
}

// ---- Vista Lector ----
function pintarMisReservas() {
    const cuerpo = document.getElementById("cuerpo-mis-reservas");
    if (!cuerpo) {
        return;
    }
    cuerpo.innerHTML = "";

    reservas.forEach((reserva) => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
            <td>${celdaLibroConPortada(reserva.libro)}</td>
            <td>${reserva.fecha}</td>
            <td><span class="estado-reserva ${claseEstadoReserva(reserva.estado)}">${reserva.estado}</span></td>
            <td><button type="button" class="btn btn-outline-danger btn-sm boton-cancelar-reserva">Cancelar</button></td>
        `;

        fila.querySelector(".boton-cancelar-reserva").addEventListener("click", () => {
            reserva.estado = "Cancelada";
            pintarMisReservas();
        });

        cuerpo.appendChild(fila);
    });
}

// ---- Vista Bibliotecario/Administrador ----
function pintarReservasStaff(listaReservas) {
    const cuerpo = document.getElementById("cuerpo-reservas");
    if (!cuerpo) {
        return;
    }
    cuerpo.innerHTML = "";

    listaReservas.forEach((reserva) => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
            <td>${celdaUsuarioConIcono(reserva.usuario)}</td>
            <td>${celdaLibroConPortada(reserva.libro)}</td>
            <td>${reserva.fecha}</td>
            <td><span class="estado-reserva ${claseEstadoReserva(reserva.estado)}">${reserva.estado}</span></td>
            <td>
                <button type="button" class="btn btn-success btn-sm boton-aprobar-reserva">Aprobar</button>
                <button type="button" class="btn btn-outline-danger btn-sm boton-rechazar-reserva">Rechazar</button>
            </td>
        `;

        fila.querySelector(".boton-aprobar-reserva").addEventListener("click", () => {
            reserva.estado = "Aprobada";
            filtrarReservasStaff();
        });
        fila.querySelector(".boton-rechazar-reserva").addEventListener("click", () => {
            reserva.estado = "Cancelada";
            filtrarReservasStaff();
        });

        cuerpo.appendChild(fila);
    });
}

function filtrarReservasStaff() {
    const filtro = document.getElementById("filtro-estado-reserva");
    if (!filtro) {
        return;
    }
    const estadoElegido = filtro.value;
    const reservasFiltradas = estadoElegido
        ? reservas.filter((reserva) => reserva.estado === estadoElegido)
        : reservas;

    pintarReservasStaff(reservasFiltradas);
}

document.addEventListener("DOMContentLoaded", () => {
    mostrarBloqueDelRolActual();
    pintarMisReservas();
    pintarReservasStaff(reservas);

    const filtroEstado = document.getElementById("filtro-estado-reserva");
    if (filtroEstado) {
        filtroEstado.addEventListener("change", filtrarReservasStaff);
    }
});
