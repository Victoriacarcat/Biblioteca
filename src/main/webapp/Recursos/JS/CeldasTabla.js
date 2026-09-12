//genera la logica js para las celdas en la gestion de crud disponibles de usuarios y libroa


// Depende de Iconos.js (obtenerIconoSvg) y Data.js (obtenerPortadaPorTitulo):
// debe cargarse después de ambos.

function celdaUsuarioConIcono(correo) {
    return `
        <span class="celda-con-icono">
            <span class="icono-avatar-tabla">${obtenerIconoSvg("persona-circulo")}</span>
            ${correo}
        </span>
    `;
}

function celdaLibroConPortada(tituloLibro) {
    const portada = obtenerPortadaPorTitulo(tituloLibro);
    if (!portada) {
        return tituloLibro;
    }
    return `
        <span class="celda-con-icono">
            <img class="miniatura-libro" src="${portada}" alt="">
            ${tituloLibro}
        </span>
    `;
}
