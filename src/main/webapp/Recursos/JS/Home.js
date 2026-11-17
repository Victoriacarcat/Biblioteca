/** Home.js
La página de bienvenida, visible para todos los roles (visitante, lector,
bibliotecario, administrador). Lo que cambia según el rol se marca con
[data-rol] en Home.jsp: el hero, las secciones para lectores (categorías,
club, tips, testimonios, boletín) y las columnas del pie de página.

    GET /api/libros?soloDisponibles=true -> las "Novedades del catálogo": los
                                            3 libros más recientes con copias disponibles
    GET /api/estadisticas                -> "Pendientes de hoy" (solo bibliotecario y
                                            administrador; el mismo que usa el Dashboard)

El formulario del boletín todavía no tiene backend: solo muestra un mensaje.
*/

// Cuántos libros se muestran en "Novedades del catálogo"
const CANTIDAD_NOVEDADES = 3;

/* Pone el nombre en los saludos (el del lector y el del personal; solo se ve el del rol actual). */
function pintarSaludoUsuario() {
    const usuario = obtenerUsuarioActual();
    if (!usuario) {
        return;
    }
    document.querySelectorAll(".home-nombre-usuario").forEach((elemento) => {
        // textContent: el nombre se muestra como texto
        elemento.textContent = usuario.nombre;
    });
}

function crearTarjetaNovedad(libro) {
    const columna = document.createElement("div");
    columna.className = "col-md-4 tarjeta-libro-contenedor";
    // Todo dato del servidor pasa por escaparHtml antes de entrar al HTML
    columna.innerHTML = `
        <a class="tarjeta-libro d-block text-decoration-none text-reset"
           href="${contextPath}/FrontEnd/Catalogo/Detallelibro.jsp?id=${libro.id}">
            <img class="portada-libro" src="${escaparHtml(urlPortada(libro.portadaUrl))}"
                 alt="Portada de ${escaparHtml(libro.titulo)}" onerror="portadaRota(this)">
            <h3 class="titulo-libro">${escaparHtml(libro.titulo)}</h3>
            <p class="autor-libro">${escaparHtml(libro.autores.join(", "))}</p>
            <p class="categoria-libro">${escaparHtml(libro.categoria)}</p>
        </a>
    `;
    return columna;
}

async function pintarNovedades() {
    const contenedor = document.getElementById("home-libros-destacados");
    if (!contenedor) {
        return;
    }
    try {
        const libros = await api.get("/libros?soloDisponibles=true");
        // El id más alto es el libro agregado más recientemente
        const novedades = libros.sort((a, b) => b.id - a.id).slice(0, CANTIDAD_NOVEDADES);
        contenedor.innerHTML = "";
        if (novedades.length === 0) {
            contenedor.innerHTML = '<p class="col-12">Todavía no hay libros disponibles en el catálogo.</p>';
            return;
        }
        novedades.forEach((libro) => contenedor.appendChild(crearTarjetaNovedad(libro)));
    } catch (error) {
        // En el inicio no hay caja de error: se deja un texto en el lugar de las tarjetas
        contenedor.innerHTML = '<p class="col-12">No se pudieron cargar las novedades.</p>';
        console.error("No se pudieron cargar las novedades:", error.message);
    }
}

// ---- Pendientes de hoy (bibliotecario y administrador) ----

/*
 Tarjeta de un pendiente: {label, valor, nota, alerta, pagina}. Usa los mismos
 estilos que las tarjetas del Dashboard (tarjeta-kpi), pero es un enlace a la
 página donde se atiende. "alerta" = true la resalta (hay algo por hacer).
*/
function crearTarjetaPendiente(pendiente) {
    const tarjeta = document.createElement("a");
    tarjeta.className = "tarjeta-kpi text-decoration-none"
        + (pendiente.alerta ? " tarjeta-kpi--alerta" : "");
    tarjeta.href = contextPath + "/FrontEnd/" + pendiente.pagina;
    tarjeta.innerHTML = `
        <div class="tarjeta-kpi-encabezado">
            <span>${escaparHtml(pendiente.label)}</span>
        </div>
        <span class="tarjeta-kpi-valor">${escaparHtml(pendiente.valor)}</span>
        <span class="tarjeta-kpi-nota">${escaparHtml(pendiente.nota)}</span>
    `;
    return tarjeta;
}

/* Arma las 5 tarjetas con los números de GET /api/estadisticas. */
function construirPendientes(e) {
    return [
        { label: "Reservas pendientes", valor: e.reservasPendientes,
            nota: e.reservasPendientes > 0 ? "Por confirmar" : "Al día",
            alerta: e.reservasPendientes > 0, pagina: "Reservas/Reservas.jsp" },
        { label: "Préstamos vencidos", valor: e.prestamosVencidos,
            nota: e.prestamosVencidos > 0 ? "Por recibir" : "Al día",
            alerta: e.prestamosVencidos > 0, pagina: "Devoluciones/Devoluciones.jsp" },
        { label: "Sanciones pendientes", valor: e.sancionesPendientes,
            nota: e.sancionesPendientes > 0
                ? "Por cobrar: " + formatearDinero(e.montoSancionesPendientes)
                : "Al día",
            alerta: e.sancionesPendientes > 0, pagina: "Sanciones/Sanciones.jsp" },
        { label: "Préstamos de hoy", valor: e.prestamosHoy,
            nota: "Entregados hoy", alerta: false, pagina: "Prestamos/Prestamos.jsp" },
        { label: "Copias disponibles", valor: e.copiasDisponibles,
            nota: "de " + e.copiasTotales + " en total", alerta: false, pagina: "Inventario/Inventario.jsp" }
    ];
}

async function pintarPendientes() {
    const contenedor = document.getElementById("home-pendientes");
    try {
        const estadisticas = await api.get("/estadisticas");
        contenedor.innerHTML = "";
        construirPendientes(estadisticas)
            .forEach((pendiente) => contenedor.appendChild(crearTarjetaPendiente(pendiente)));
    } catch (error) {
        // Como en "Novedades": se deja un texto en el lugar de las tarjetas
        contenedor.innerHTML = "<p>No se pudieron cargar los pendientes.</p>";
        console.error("No se pudieron cargar los pendientes:", error.message);
    }
}

// Boletín: no hay backend todavía, así que "suscribirse" solo muestra
// un mensaje de confirmación.
function manejarSuscripcionBoletin(evento) {
    evento.preventDefault();

    const correo = document.getElementById("boletin-correo").value.trim();
    if (!correo) {
        return;
    }

    const mensaje = document.getElementById("boletin-mensaje");
    mensaje.textContent = `¡Gracias! Te vamos a escribir a ${correo} con las novedades.`;
    mensaje.hidden = false;

    document.getElementById("form-boletin").reset();
}

// Espera a saber quién inició sesión (Auth.js) antes de pintar
sesionLista.then(() => {
    // Muestra el hero del rol actual (Utilidades.js)
    mostrarSegunRol();
    // Las secciones aparecen suavemente al bajar. El hero no: ya está a la vista
    // y su fondo oscuro "parpadearía" al cargar
    animarAlAparecer(".contenedor-home > section:not(.seccion-hero-home)");
    pintarSaludoUsuario();
    pintarNovedades();

    // Solo el personal ve los pendientes (y el backend solo a él le da esos números)
    const rol = obtenerRolActual();
    if (rol === "bibliotecario" || rol === "administrador") {
        pintarPendientes();
    }

    const formularioBoletin = document.getElementById("form-boletin");
    if (formularioBoletin) {
        formularioBoletin.addEventListener("submit", manejarSuscripcionBoletin);
    }
});
