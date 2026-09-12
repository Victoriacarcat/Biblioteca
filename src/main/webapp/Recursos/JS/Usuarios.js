// Usuarios.js
// Vista exclusiva de Administrador: gestiona los usuarios registrados
// (arreglo "usuarios" de Data.js). Sin backend todavía: cambiar el rol
// o el estado solo actualiza el arreglo en memoria (se pierde al
// recargar la página).
//
// Reglas de negocio aplicadas aquí:
//   1. Un administrador no puede suspenderse ni cambiarse el rol a sí
//      mismo (evitaría que se quede sin acceso a esta pantalla). Se
//      identifica comparando el correo de la fila con el de la sesión
//      simulada (Auth.js).
//   2. Para reactivar a un usuario "Suspendido" primero debe estar al
//      día: no puede tener sanciones con estado "Pendiente" (arreglo
//      "sanciones" de Data.js).

function obtenerSancionesPendientes(correo) {
    return sanciones.filter((sancion) => sancion.usuario === correo && sancion.estado === "Pendiente");
}

function esUsuarioDeLaSesionActual(correo) {
    return correo === obtenerCorreoActual();
}

// Color de la pastilla de estado: "Activo" verde, "Suspendido" rojo.
function claseEstadoUsuario(estado) {
    return estado === "Activo" ? "estado-positivo" : "estado-negativo";
}

function mostrarErrorUsuarios(mensaje) {
    const caja = document.getElementById("usuarios-error");
    caja.textContent = mensaje;
    caja.hidden = false;
}

function ocultarErrorUsuarios() {
    document.getElementById("usuarios-error").hidden = true;
}

function crearFilaUsuario(usuario) {
    const fila = document.createElement("tr");
    const esUnoMismo = esUsuarioDeLaSesionActual(usuario.correo);
    const estaSuspendido = usuario.estado === "Suspendido";

    fila.innerHTML = `
        <td>
            <span class="celda-con-icono">
                <span class="icono-avatar-tabla">${obtenerIconoSvg("persona-circulo")}</span>
                ${usuario.nombre}
            </span>
        </td>
        <td>${usuario.correo}</td>
        <td>
            <select class="form-select form-select-sm campo-rol-usuario" ${esUnoMismo ? "disabled" : ""}>
                <option value="lector" ${usuario.rol === "lector" ? "selected" : ""}>Lector</option>
                <option value="bibliotecario" ${usuario.rol === "bibliotecario" ? "selected" : ""}>Bibliotecario</option>
                <option value="administrador" ${usuario.rol === "administrador" ? "selected" : ""}>Administrador</option>
            </select>
        </td>
        <td><span class="estado-usuario ${claseEstadoUsuario(usuario.estado)}">${usuario.estado}</span></td>
        <td>${usuario.fechaRegistro}</td>
        <td>
            <button
                type="button"
                class="btn btn-sm ${estaSuspendido ? "btn-outline-success boton-activar-usuario" : "btn-outline-danger boton-suspender-usuario"}"
                ${esUnoMismo ? "disabled" : ""}>
                ${estaSuspendido ? "Activar" : "Suspender"}
            </button>
        </td>
    `;

    if (esUnoMismo) {
        fila.title = "No puedes modificar tu propia cuenta desde aquí.";
    }

    fila.querySelector(".campo-rol-usuario").addEventListener("change", (evento) => {
        usuario.rol = evento.target.value;
        ocultarErrorUsuarios();
    });

    const botonEstado = fila.querySelector(".boton-suspender-usuario, .boton-activar-usuario");
    if (botonEstado) {
        botonEstado.addEventListener("click", () => {
            if (estaSuspendido) {
                const pendientes = obtenerSancionesPendientes(usuario.correo);
                if (pendientes.length > 0) {
                    mostrarErrorUsuarios(
                        `No se puede activar a ${usuario.nombre}: tiene ${pendientes.length} sanción(es) pendiente(s) de pago.`
                    );
                    return;
                }
                usuario.estado = "Activo";
            } else {
                usuario.estado = "Suspendido";
            }
            ocultarErrorUsuarios();
            filtrarUsuarios();
        });
    }

    return fila;
}

function pintarUsuarios(listaUsuarios) {
    const cuerpo = document.getElementById("cuerpo-usuarios");
    cuerpo.innerHTML = "";
    listaUsuarios.forEach((usuario) => {
        cuerpo.appendChild(crearFilaUsuario(usuario));
    });
}

function filtrarUsuarios() {
    const texto = document.getElementById("buscar-usuario-admin").value.trim().toLowerCase();
    const rolElegido = document.getElementById("filtro-rol-usuario").value;
    const estadoElegido = document.getElementById("filtro-estado-usuario").value;

    const filtrados = usuarios.filter((usuario) => {
        const coincideTexto =
            usuario.nombre.toLowerCase().includes(texto) ||
            usuario.correo.toLowerCase().includes(texto);
        const coincideRol = !rolElegido || usuario.rol === rolElegido;
        const coincideEstado = !estadoElegido || usuario.estado === estadoElegido;
        return coincideTexto && coincideRol && coincideEstado;
    });

    pintarUsuarios(filtrados);
}

document.addEventListener("DOMContentLoaded", () => {
    pintarUsuarios(usuarios);

    document.getElementById("buscar-usuario-admin").addEventListener("input", filtrarUsuarios);
    document.getElementById("filtro-rol-usuario").addEventListener("change", filtrarUsuarios);
    document.getElementById("filtro-estado-usuario").addEventListener("change", filtrarUsuarios);
});
