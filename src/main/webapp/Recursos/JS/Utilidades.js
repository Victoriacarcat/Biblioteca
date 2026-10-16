/** Utilidades.js
Funciones pequeñas que usan varias vistas, para no repetirlas en cada archivo:
    - escaparHtml: hace seguro un texto antes de meterlo en innerHTML.
    - formatearFecha / formatearFechaHora / formatearDinero: cómo se muestran
      las fechas y los valores que manda el backend.
    - ESTADOS, textoEstado, pastillaEstado: texto y color de cada estado
      (los enums del backend: ACTIVO, PENDIENTE, PAGADA...).
    - urlPortada / PORTADA_POR_DEFECTO: la imagen de portada de un libro.
    - mostrarError / mostrarExito / ocultarError: las cajas de aviso de cada página.
    - mostrarSegunRol: muestra u oculta los elementos con data-rol.
    - paginaPermitida: avisa si la página no corresponde al rol actual.

Debe cargarse después de Auth.js (mostrarSegunRol usa obtenerRolActual)
y antes del JS de cada vista.
*/

// ---- Seguridad ----

/*
 Convierte los caracteres especiales de HTML (< > & " ') en texto normal.
 Usarla SIEMPRE con datos que vienen del servidor antes de ponerlos en
 innerHTML: un título o un nombre lo escribió un usuario y podría traer
 etiquetas HTML o <script>.
*/
function escaparHtml(texto) {
    if (texto === null || texto === undefined) {
        return "";
    }
    return String(texto)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#39;");
}

// ---- Fechas y dinero ----

/*
 El backend manda las fechas como texto: "2026-10-15" (LocalDate) o
 "2026-10-15T14:30:00" (LocalDateTime). Se toman las partes del texto en
 vez de usar new Date("2026-10-15"), porque esa forma la interpreta en
 hora UTC y en Colombia mostraría el día anterior.
 Devuelve "15/10/2026", o "—" si no hay fecha.
*/
function formatearFecha(fechaIso) {
    if (!fechaIso) {
        return "—";
    }
    const [anio, mes, dia] = fechaIso.substring(0, 10).split("-");
    return `${dia}/${mes}/${anio}`;
}

/* Igual que formatearFecha pero con la hora: "15/10/2026 14:30". */
function formatearFechaHora(fechaIso) {
    if (!fechaIso) {
        return "—";
    }
    const hora = fechaIso.length >= 16 ? fechaIso.substring(11, 16) : "";
    return (formatearFecha(fechaIso) + " " + hora).trim();
}

/*
 Valor en pesos colombianos: 1500 -> "$ 1.500" y 1500.5 -> "$ 1.500,50".
 Sin centavos no se muestran decimales; si los hay, siempre dos.
*/
function formatearDinero(valor) {
    if (valor === null || valor === undefined) {
        return "—";
    }
    const numero = Number(valor);
    const decimales = Number.isInteger(numero) ? 0 : 2;
    return numero.toLocaleString("es-CO", {
        style: "currency",
        currency: "COP",
        minimumFractionDigits: decimales,
        maximumFractionDigits: decimales
    });
}

// ---- Portadas ----

/*
 Imagen que se muestra cuando un libro no tiene portada o la dirección no
 carga. Es un SVG escrito aquí mismo, así no depende de otro archivo ni de
 internet.
*/
const PORTADA_POR_DEFECTO = "data:image/svg+xml;charset=utf-8," + encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" width="300" height="440" viewBox="0 0 300 440">'
    + '<rect width="300" height="440" fill="#EAF3EC"/>'
    + '<text x="150" y="228" font-family="sans-serif" font-size="22" fill="#2F6B4C" text-anchor="middle">Sin portada</text>'
    + '</svg>');

/*
 Dirección de la portada lista para usar en un <img>:
    - sin portada                       -> PORTADA_POR_DEFECTO
    - "https://..." (otra página)       -> se usa tal cual
    - "/Recursos/Imagenes/x.jpg" (local) -> se le antepone contextPath
*/
function urlPortada(portadaUrl) {
    if (!portadaUrl) {
        return PORTADA_POR_DEFECTO;
    }
    if (portadaUrl.startsWith("/")) {
        return contextPath + portadaUrl;
    }
    return portadaUrl;
}

/*
 Si la imagen de una portada no carga (dirección rota), pone la de por defecto.
 Se usa en el HTML así: <img src="..." onerror="portadaRota(this)">
*/
function portadaRota(imagen) {
    imagen.onerror = null; // evita un ciclo si también fallara la de por defecto
    imagen.src = PORTADA_POR_DEFECTO;
}

// ---- Estados ----

/*
 Texto y color (clase CSS) de cada estado que manda el backend, agrupados
 por tipo. El tipo coincide con la clase de la pastilla en Styles.css
 (estado-prestamo, estado-reserva, ...).
 Colores: positivo = verde, alerta = ámbar, negativo = rojo, neutro = gris.
*/
const ESTADOS = {
    prestamo: {
        ACTIVO: { texto: "Activo", clase: "estado-positivo" },
        // "VENCIDO" no existe en el backend: es un ACTIVO con la fecha límite
        // ya pasada (ver estadoVisiblePrestamo).
        VENCIDO: { texto: "Vencido", clase: "estado-negativo" },
        DEVUELTO: { texto: "Devuelto", clase: "estado-neutro" },
        PERDIDO: { texto: "Perdido", clase: "estado-negativo" },
        ANULADO: { texto: "Anulado", clase: "estado-neutro" }
    },
    reserva: {
        PENDIENTE: { texto: "Pendiente", clase: "estado-alerta" },
        APROBADA: { texto: "Aprobada", clase: "estado-positivo" },
        RECHAZADA: { texto: "Rechazada", clase: "estado-negativo" },
        CANCELADA: { texto: "Cancelada", clase: "estado-neutro" },
        VENCIDA: { texto: "Vencida", clase: "estado-negativo" },
        RETIRADA: { texto: "Retirada", clase: "estado-neutro" }
    },
    sancion: {
        PENDIENTE: { texto: "Pendiente", clase: "estado-alerta" },
        PAGADA: { texto: "Pagada", clase: "estado-positivo" },
        ANULADA: { texto: "Anulada", clase: "estado-neutro" }
    },
    usuario: {
        ACTIVO: { texto: "Activo", clase: "estado-positivo" },
        SUSPENDIDO: { texto: "Suspendido", clase: "estado-negativo" },
        INACTIVO: { texto: "Inactivo", clase: "estado-neutro" }
    }
};

/* Texto para mostrar de un estado. Si no se conoce, devuelve el valor tal cual. */
function textoEstado(tipo, estado) {
    const datos = ESTADOS[tipo] && ESTADOS[tipo][estado];
    return datos ? datos.texto : estado;
}

/* La "pastilla" de color con el estado, lista para meter en una celda. */
function pastillaEstado(tipo, estado) {
    const datos = ESTADOS[tipo] && ESTADOS[tipo][estado];
    const clase = datos ? datos.clase : "estado-neutro";
    return `<span class="estado-${tipo} ${clase}">${escaparHtml(textoEstado(tipo, estado))}</span>`;
}

/* true si el préstamo sigue ACTIVO y su fecha límite ya pasó. */
function estaVencido(prestamo) {
    return prestamo.estado === "ACTIVO" && new Date(prestamo.fechaLimite) < new Date();
}

/* Estado que se muestra de un préstamo: "VENCIDO" si está vencido, si no el del backend. */
function estadoVisiblePrestamo(prestamo) {
    return estaVencido(prestamo) ? "VENCIDO" : prestamo.estado;
}

/* Texto de los tipos de sanción del backend. */
const TIPOS_SANCION = {
    MORA: "Mora por retraso",
    LIBRO_DANADO: "Libro dañado",
    LIBRO_PERDIDO: "Libro perdido",
    OTRO: "Otro motivo"
};

function textoTipoSancion(tipo) {
    return TIPOS_SANCION[tipo] || tipo;
}

/* Texto de los roles del backend (LECTOR -> "Lector"). */
const ROLES = {
    LECTOR: "Lector",
    BIBLIOTECARIO: "Bibliotecario",
    ADMINISTRADOR: "Administrador"
};

function textoRol(rol) {
    return ROLES[rol] || rol;
}

// ---- Mensajes de error ----

/*
 Muestra un mensaje en la caja de error de la página (un elemento con
 "hidden" que ya existe en el JSP, por ejemplo <div id="usuarios-error">).
 Se usa textContent, no innerHTML: el mensaje se muestra como texto.
*/
function mostrarError(idCaja, mensaje) {
    const caja = document.getElementById(idCaja);
    if (!caja) {
        console.error("No existe la caja de error #" + idCaja, mensaje);
        return;
    }
    caja.textContent = mensaje;
    caja.hidden = false;
}

/*
 Igual que mostrarError, pero para avisos de que algo salió bien
 (la caja usa la clase "exito-form" en vez de "error-form").
*/
function mostrarExito(idCaja, mensaje) {
    mostrarError(idCaja, mensaje);
}

/* Oculta una caja de aviso (sirve tanto para las de error como para las de éxito). */
function ocultarError(idCaja) {
    const caja = document.getElementById(idCaja);
    if (caja) {
        caja.hidden = true;
    }
}

// ---- Roles ----

/*
 Muestra los elementos cuyo data-rol incluye el rol actual y oculta el
 resto. "raiz" permite aplicarlo solo a una parte de la página (por
 ejemplo, una card recién creada); si no se indica, se aplica a toda.
 Ojo: esto solo cambia lo que se ve. La seguridad real la aplica el backend.
*/
function mostrarSegunRol(raiz = document) {
    const rolActual = obtenerRolActual();
    raiz.querySelectorAll("[data-rol]").forEach((elemento) => {
        const rolesPermitidos = elemento.getAttribute("data-rol").split(",");
        elemento.hidden = !rolesPermitidos.includes(rolActual);
    });
}

/*
 Para el inicio de cada vista: devuelve true si el rol actual está entre
 los permitidos (por ejemplo ["lector"]). Si no, muestra un mensaje en la
 caja de error y devuelve false, para que la vista no pida datos.
 Es solo para la interfaz: el backend igual rechaza a quien no tenga permiso.
*/
function paginaPermitida(rolesPermitidos, idCajaError) {
    const rolActual = obtenerRolActual();
    if (rolesPermitidos.includes(rolActual)) {
        return true;
    }
    mostrarError(idCajaError, rolActual === "visitante"
        ? "Debes iniciar sesión para ver esta página."
        : "Esta página no está disponible para tu rol.");
    return false;
}
