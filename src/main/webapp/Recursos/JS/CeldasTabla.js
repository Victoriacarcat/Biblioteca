// CeldasTabla.js
// Helpers de presentación compartidos por las vistas con tablas
// (Prestamos, Devoluciones, Reservas, Sanciones, Historial,
// MisPrestamos, Inventario, Usuarios): arman una celda "usuario" con
// avatar o una celda "libro" con la miniatura de portada, en vez de
// dejar la fila como puro texto plano.
//
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
