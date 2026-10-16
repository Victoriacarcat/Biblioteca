/** Home.js
La página de bienvenida, visible para todos los roles (visitante, lector,
bibliotecario, administrador). El bloque del hero alterna con [data-rol].

    GET /api/libros?soloDisponibles=true -> las "Novedades del catálogo": los
                                            3 libros más recientes con copias disponibles

El formulario del boletín todavía no tiene backend: solo muestra un mensaje.
*/

// Cuántos libros se muestran en "Novedades del catálogo"
const CANTIDAD_NOVEDADES = 3;

function pintarSaludoUsuario() {
    const usuario = obtenerUsuarioActual();
    const elementoNombre = document.getElementById("home-nombre-usuario");
    if (elementoNombre && usuario) {
        // textContent: el nombre se muestra como texto
        elementoNombre.textContent = usuario.nombre;
    }
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
    pintarSaludoUsuario();
    pintarNovedades();

    const formularioBoletin = document.getElementById("form-boletin");
    if (formularioBoletin) {
        formularioBoletin.addEventListener("submit", manejarSuscripcionBoletin);
    }
});
