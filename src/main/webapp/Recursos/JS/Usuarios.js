/** Usuarios.js
Vista exclusiva del Administrador: cuentas registradas.

    GET  /api/usuarios?texto=&rol=LECTOR                -> lista (el servidor filtra texto y rol)
    PUT  /api/usuarios?id=5&accion=rol     {rol}        -> LECTOR o BIBLIOTECARIO
    PUT  /api/usuarios?id=5&accion=estado  {estado}     -> ACTIVO, SUSPENDIDO o INACTIVO
    POST /api/usuarios  {datos de registro, rol}        -> crea una cuenta de cualquier rol

Reglas (las aplica el backend; aquí solo se reflejan en la pantalla):
    1. Nadie puede cambiar su propio rol ni su estado.
    2. El administrador puede crear cuentas de cualquier rol, incluido
       otro administrador. Pero el rol y el estado de un administrador que
       ya existe no se cambian desde la web, ni se sube a alguien a
       administrador cambiándole el rol.
    3. Para reactivar a un suspendido, no puede tener sanciones pendientes.
El filtro de estado se aplica en el navegador sobre la lista recibida.
*/

// Usuarios que mandó el servidor con el texto y el rol actuales
let usuariosCargados = [];

function avisarUsuarios(tipo, mensaje) {
    ocultarError("usuarios-exito");
    ocultarError("usuarios-error");
    if (tipo === "exito") {
        mostrarExito("usuarios-exito", mensaje);
    } else {
        mostrarError("usuarios-error", mensaje);
    }
}

/* true si la fila es la del administrador que inició sesión (se compara el id, no el correo). */
function esUnoMismo(usuario) {
    return usuario.id === obtenerUsuarioActual().id;
}

/* Las cuentas propias y las de administradores no se pueden modificar desde aquí. */
function motivoBloqueo(usuario) {
    if (esUnoMismo(usuario)) {
        return "No puedes modificar tu propia cuenta.";
    }
    if (usuario.rol === "ADMINISTRADOR") {
        return "El rol y el estado de los administradores no se pueden cambiar desde la aplicación.";
    }
    return null;
}

// ---- Celdas ----

/* Select de rol (Lector / Bibliotecario). Para cuentas bloqueadas, solo el texto. */
function celdaRol(usuario) {
    if (motivoBloqueo(usuario)) {
        return escaparHtml(textoRol(usuario.rol));
    }
    return `
        <select class="form-select form-select-sm campo-rol-usuario" aria-label="Rol de ${escaparHtml(usuario.nombreCompleto)}">
            <option value="LECTOR" ${usuario.rol === "LECTOR" ? "selected" : ""}>Lector</option>
            <option value="BIBLIOTECARIO" ${usuario.rol === "BIBLIOTECARIO" ? "selected" : ""}>Bibliotecario</option>
        </select>
    `;
}

/* Botones de estado según el estado actual. data-estado es el estado al que se pasa. */
function celdaAcciones(usuario) {
    if (motivoBloqueo(usuario)) {
        return "—";
    }
    const botones = [];
    if (usuario.estado !== "ACTIVO") {
        botones.push('<button type="button" class="btn btn-outline-success btn-sm boton-estado" data-estado="ACTIVO">Activar</button>');
    }
    if (usuario.estado === "ACTIVO") {
        botones.push('<button type="button" class="btn btn-outline-danger btn-sm boton-estado" data-estado="SUSPENDIDO">Suspender</button>');
    }
    if (usuario.estado !== "INACTIVO") {
        botones.push('<button type="button" class="btn btn-outline-secondary btn-sm boton-estado" data-estado="INACTIVO">Desactivar</button>');
    }
    return botones.join(" ");
}

function crearFilaUsuario(usuario) {
    const fila = document.createElement("tr");
    fila.innerHTML = `
        <td>
            <span class="celda-con-icono">
                <span class="icono-avatar-tabla">${obtenerIconoSvg("persona-circulo")}</span>
                ${escaparHtml(usuario.nombreCompleto)}
            </span>
        </td>
        <td>
            ${escaparHtml(usuario.correo)}
            <small class="d-block">${escaparHtml(usuario.tipoDocumento)} ${escaparHtml(usuario.documento)}</small>
        </td>
        <td>${celdaRol(usuario)}</td>
        <td>${pastillaEstado("usuario", usuario.estado)}</td>
        <td>${formatearFecha(usuario.creadoEn)}</td>
        <td>${celdaAcciones(usuario)}</td>
    `;

    const bloqueo = motivoBloqueo(usuario);
    if (bloqueo) {
        fila.title = bloqueo;
        return fila;
    }

    const selectRol = fila.querySelector(".campo-rol-usuario");
    selectRol.addEventListener("change", () => cambiarRol(usuario, selectRol));
    fila.querySelectorAll(".boton-estado").forEach((boton) => {
        boton.addEventListener("click", () => cambiarEstado(usuario, boton.dataset.estado, boton));
    });
    return fila;
}

// ---- Cargar y pintar ----

function pintarUsuarios() {
    const estadoElegido = document.getElementById("filtro-estado-usuario").value;
    const lista = usuariosCargados.filter((usuario) => !estadoElegido || usuario.estado === estadoElegido);

    const cuerpo = document.getElementById("cuerpo-usuarios");
    cuerpo.innerHTML = "";
    if (lista.length === 0) {
        cuerpo.innerHTML = '<tr><td colspan="6">No hay usuarios con esos filtros.</td></tr>';
        return;
    }
    lista.forEach((usuario) => {
        cuerpo.appendChild(crearFilaUsuario(usuario));
    });
}

async function cargarUsuarios() {
    const parametros = new URLSearchParams();
    const texto = document.getElementById("buscar-usuario-admin").value.trim();
    const rol = document.getElementById("filtro-rol-usuario").value;
    if (texto) {
        parametros.set("texto", texto);
    }
    if (rol) {
        parametros.set("rol", rol);
    }
    try {
        usuariosCargados = await api.get("/usuarios?" + parametros.toString());
        pintarUsuarios();
    } catch (error) {
        avisarUsuarios("error", error.message);
    }
}

/*
 El buscador pide al servidor mientras se escribe, pero espera 300 ms
 desde la última tecla para no hacer una petición por cada letra.
*/
let temporizadorBusqueda = null;
function buscarAlEscribir() {
    clearTimeout(temporizadorBusqueda);
    temporizadorBusqueda = setTimeout(cargarUsuarios, 300);
}

// ---- Acciones ----

async function cambiarRol(usuario, select) {
    const nuevoRol = select.value;
    const aviso = nuevoRol === "BIBLIOTECARIO"
        ? `¿Dar el rol Bibliotecario a ${usuario.nombreCompleto}? Podrá gestionar préstamos, reservas y sanciones.`
        : `¿Cambiar a ${usuario.nombreCompleto} al rol Lector? Perderá el acceso a la gestión.`;
    if (!confirm(aviso)) {
        select.value = usuario.rol; // deja el select como estaba
        return;
    }
    select.disabled = true;
    try {
        await api.put("/usuarios?id=" + usuario.id + "&accion=rol", { rol: nuevoRol });
        avisarUsuarios("exito", `${usuario.nombreCompleto} ahora es ${textoRol(nuevoRol)}.`);
        await cargarUsuarios();
    } catch (error) {
        avisarUsuarios("error", error.message);
        select.value = usuario.rol;
        select.disabled = false;
    }
}

async function cambiarEstado(usuario, nuevoEstado, boton) {
    const textoNuevo = textoEstado("usuario", nuevoEstado);
    if (!confirm(`¿Cambiar el estado de la cuenta de ${usuario.nombreCompleto} a "${textoNuevo}"?`)) {
        return;
    }
    boton.disabled = true;
    try {
        await api.put("/usuarios?id=" + usuario.id + "&accion=estado", { estado: nuevoEstado });
        avisarUsuarios("exito", `La cuenta de ${usuario.nombreCompleto} ahora está en estado "${textoNuevo}".`);
        await cargarUsuarios();
    } catch (error) {
        // Por ejemplo: "No se puede reactivar: tiene 2 sanciones pendientes."
        avisarUsuarios("error", error.message);
        boton.disabled = false;
    }
}

// ---- Crear cuenta (cualquier rol) ----

function mostrarPanelUsuario(mostrar) {
    document.getElementById("panel-crear-usuario").hidden = !mostrar;
    if (mostrar) {
        document.getElementById("nuevo-nombre").focus();
    } else {
        document.getElementById("form-crear-usuario").reset();
    }
}

async function crearUsuario(evento) {
    evento.preventDefault();
    const valor = (id) => document.getElementById(id).value.trim();
    const rol = document.getElementById("nuevo-rol").value;

    // Un administrador tiene acceso total y su rol no se podrá cambiar después desde la web
    if (rol === "ADMINISTRADOR"
            && !confirm("¿Crear una cuenta de ADMINISTRADOR? Tendrá acceso total al sistema "
                + "y su rol no se podrá cambiar después desde la aplicación.")) {
        return;
    }

    // Los nombres deben coincidir con RegistroDTO; "rol" lo separa el controller
    const datos = {
        nombre: valor("nuevo-nombre"),
        apellidos: valor("nuevo-apellidos"),
        tipoDocumento: document.getElementById("nuevo-tipo-documento").value,
        documento: valor("nuevo-documento"),
        telefono: valor("nuevo-telefono"),
        correo: valor("nuevo-correo"),
        // La contraseña no se recorta: los espacios pueden ser parte de ella
        contrasena: document.getElementById("nuevo-contrasena").value,
        rol: rol
    };

    const boton = document.getElementById("boton-confirmar-usuario");
    boton.disabled = true;
    try {
        const creado = await api.post("/usuarios", datos);
        avisarUsuarios("exito", `Se creó la cuenta de ${creado.nombreCompleto} (${creado.correo}) `
            + `con rol ${textoRol(creado.rol)}.`);
        mostrarPanelUsuario(false);
        await cargarUsuarios();
    } catch (error) {
        // Las validaciones (campos, formatos, correo o documento repetido) las hace el servidor
        avisarUsuarios("error", error.message);
    }
    boton.disabled = false;
}

// Espera a saber quién inició sesión (Auth.js) antes de pintar
sesionLista.then(() => {
    if (!paginaPermitida(["administrador"], "usuarios-error")) {
        return;
    }
    document.getElementById("buscar-usuario-admin").addEventListener("input", buscarAlEscribir);
    document.getElementById("filtro-rol-usuario").addEventListener("change", cargarUsuarios);
    document.getElementById("filtro-estado-usuario").addEventListener("change", pintarUsuarios);

    document.getElementById("boton-crear-usuario").addEventListener("click", () => mostrarPanelUsuario(true));
    document.getElementById("boton-cancelar-usuario").addEventListener("click", () => mostrarPanelUsuario(false));
    document.getElementById("form-crear-usuario").addEventListener("submit", crearUsuario);

    cargarUsuarios();
});
