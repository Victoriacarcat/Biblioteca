/** Sanciones.js
 Igual que Reservas.js: un bloque para el Lector (sus propias
/sanciones, solo lectura) y otro para Bibliotecario/Administrador
(todas las sanciones, con botón para marcarlas como pagadas). Datos
de ejemplo en Data.js ("sanciones"): sin backend todavía, "marcar
 como pagada" solo cambia el estado en memoria.*/

function mostrarBloqueDelRolActual() {
    const rolActual = obtenerRolActual();

    document.querySelectorAll("[data-rol]").forEach((elemento) => {
        const rolesPermitidos = elemento.getAttribute("data-rol").split(",");
        elemento.hidden = !rolesPermitidos.includes(rolActual);
    });
}

function formatearValorSancion(valor) {
    return "$" + valor.toLocaleString("es-CO");
}

// Color de la pastilla de estado: "Pagada" verde, "Pendiente" ámbar.
function claseEstadoSancion(estado) {
    return estado === "Pagada" ? "estado-positivo" : "estado-alerta";
}

//Vista Lector
function pintarMisSanciones() {
    const cuerpo = document.getElementById("cuerpo-mis-sanciones");
    if (!cuerpo) {
        return;
    }
    cuerpo.innerHTML = "";

    sanciones.forEach((sancion) => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
            <td>${sancion.motivo}</td>
            <td><span class="estado-sancion ${claseEstadoSancion(sancion.estado)}">${sancion.estado}</span></td>
            <td>${formatearValorSancion(sancion.valor)}</td>
        `;
        cuerpo.appendChild(fila);
    });
}

// Vista Bibliotecario/Administrador
function pintarSancionesStaff() {
    const cuerpo = document.getElementById("cuerpo-sanciones");
    if (!cuerpo) {
        return;
    }
    cuerpo.innerHTML = "";

    sanciones.forEach((sancion) => {
        const fila = document.createElement("tr");
        fila.innerHTML = `
            <td>${celdaUsuarioConIcono(sancion.usuario)}</td>
            <td>${sancion.motivo}</td>
            <td>${sancion.fecha}</td>
            <td><span class="estado-sancion ${claseEstadoSancion(sancion.estado)}">${sancion.estado}</span></td>
            <td>${formatearValorSancion(sancion.valor)}</td>
            <td><button type="button" class="btn btn-outline-success btn-sm boton-marcar-pagada">Marcar como pagada</button></td>
        `;

        fila.querySelector(".boton-marcar-pagada").addEventListener("click", () => {
            sancion.estado = "Pagada";
            pintarSancionesStaff();
        });

        cuerpo.appendChild(fila);
    });
}

document.addEventListener("DOMContentLoaded", () => {
    mostrarBloqueDelRolActual();
    pintarMisSanciones();
    pintarSancionesStaff();
});
