// auth.js
// Módulo compartido de sesión simulada (sin backend todavía).
// El rol y el correo se guardan en localStorage mientras no exista base de datos.

const CLAVE_ROL = "wisebooks_rol";
const CLAVE_CORREO = "wisebooks_correo";

// Roles válidos del sistema (deben coincidir con los que usa el <select> de Login.jsp)
const ROLES_VALIDOS = ["lector", "bibliotecario", "administrador"];

/**
 * Devuelve el rol actual de la sesión simulada.
 * Si no hay ninguno guardado, se considera "visitante".
 */
function obtenerRolActual() {
    const rolGuardado = localStorage.getItem(CLAVE_ROL);
    return ROLES_VALIDOS.includes(rolGuardado) ? rolGuardado : "visitante";
}

/**
 * Guarda la sesión simulada (rol + correo) en localStorage.
 * La llamará Login.js cuando el usuario "inicie sesión".
 */
function iniciarSesionSimulada(rol, correo) {
    if (!ROLES_VALIDOS.includes(rol)) {
        console.error("Rol no válido:", rol);
        return;
    }
    localStorage.setItem(CLAVE_ROL, rol);
    localStorage.setItem(CLAVE_CORREO, correo);
}

/**
 * Cierra la sesión simulada y redirige al catálogo.
 * NOTA: sin servlet todavía no hay una URL limpia real a la que redirigir
 * (Catalogo.jsp vive dentro de WEB-INF, inalcanzable por el navegador).
 * Se deja "#" como placeholder, igual que en header.js, hasta que exista
 * la ruta de servlet (ej. "/catalogo").
 */
function cerrarSesion() {
    localStorage.removeItem(CLAVE_ROL);
    localStorage.removeItem(CLAVE_CORREO);
    window.location.href = "#"; // TODO: reemplazar por ruta de servlet, ej. "/catalogo"
}

/**
 * Devuelve el correo guardado de la sesión simulada (o "" si no hay sesión).
 * La usa header.js para mostrarlo junto al ícono de usuario.
 */
function obtenerCorreoActual() {
    return localStorage.getItem(CLAVE_CORREO) || "";
}