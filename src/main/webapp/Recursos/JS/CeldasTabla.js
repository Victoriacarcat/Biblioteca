/** CeldasTabla.js
Celdas que se repiten en varias tablas (préstamos, reservas, sanciones,
devoluciones): el usuario con su ícono y el libro con su miniatura.

Depende de Iconos.js (obtenerIconoSvg) y de Utilidades.js (escaparHtml,
urlPortada): debe cargarse después de ambos.
*/

/* Usuario con el ícono de persona. "texto" puede ser el nombre o el correo. */
function celdaUsuarioConIcono(texto) {
    return `
        <span class="celda-con-icono">
            <span class="icono-avatar-tabla">${obtenerIconoSvg("persona-circulo")}</span>
            ${escaparHtml(texto)}
        </span>
    `;
}

/*
 Título del libro con su miniatura de portada.
 "portadaUrl" es la portada que manda el backend (por ejemplo, portadaLibro
 de un préstamo). Si no se pasa (las reservas y sanciones no la traen), se
 muestra solo el título. Si se pasa pero es null, se usa la de por defecto.
*/
function celdaLibroConPortada(tituloLibro, portadaUrl) {
    if (portadaUrl === undefined) {
        return escaparHtml(tituloLibro);
    }
    return `
        <span class="celda-con-icono">
            <img class="miniatura-libro" src="${escaparHtml(urlPortada(portadaUrl))}" alt=""
                 onerror="portadaRota(this)">
            ${escaparHtml(tituloLibro)}
        </span>
    `;
}
