/** DetalleLibro.js
// Esta pantalla es un MOCKUP estático (el contenido del libro está
// escrito directamente en el JSP, no viene de Data.js todavía).
//
// Lo único dinámico aquí es mostrar/ocultar los botones de acción
// según el rol actual, con el mismo patrón que Catalogo.js.
*/
 
function aplicarVisibilidadPorRolEnDetalle() {
    const rolActual = obtenerRolActual();
 
    document.querySelectorAll("[data-rol]").forEach((elemento) => {
        const rolesPermitidos = elemento.getAttribute("data-rol").split(",");
        if (rolesPermitidos.includes(rolActual)) {
            elemento.hidden = false;
        }
    });
}
 
document.addEventListener("DOMContentLoaded", () => {
    aplicarVisibilidadPorRolEnDetalle();
});

