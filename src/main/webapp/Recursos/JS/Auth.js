/** Auth.js
Sesión del usuario, compartida por todas las páginas.

La sesión vive en el servidor (cookie JSESSIONID). Al cargar la página se
pregunta a GET /api/login quién inició sesión y se guarda aquí, en memoria.
Ya no se usa localStorage: el rol lo decide el servidor, no el navegador.

Como la respuesta tarda un momento, cada página debe esperar a "sesionLista"
antes de pintar algo que dependa del rol:
    sesionLista.then(() => {
        ... aquí ya se puede usar obtenerRolActual() ...
    });

Requiere Api.js (cargado antes) y la constante global "contextPath".
*/

// Usuario que inició sesión (UsuarioDTO del backend), o null si es un visitante
let usuarioActual = null;

/*
 Pregunta al servidor quién inició sesión. Si la petición falla (servidor
 caído, por ejemplo) se sigue como visitante para que la página cargue igual.
*/
async function cargarSesion() {
    try {
        const respuesta = await api.get("/login");
        usuarioActual = respuesta.usuario;
    } catch (error) {
        console.error("No se pudo consultar la sesión:", error.message);
        usuarioActual = null;
    }
    return usuarioActual;
}

// Se pide una sola vez, apenas se carga este archivo
const sesionLista = cargarSesion();

/* El usuario que inició sesión ({id, nombre, apellidos, nombreCompleto, correo, rol, estado...}) o null. */
function obtenerUsuarioActual() {
    return usuarioActual;
}

/*
 Rol en minúscula ("lector", "bibliotecario", "administrador") o "visitante"
 si no hay sesión. Se pasa a minúscula porque así están escritos los
 atributos data-rol de las JSP (el backend lo manda como "LECTOR").
*/
function obtenerRolActual() {
    return usuarioActual ? usuarioActual.rol.toLowerCase() : "visitante";
}

/* Correo del usuario que inició sesión, o "" si es un visitante. */
function obtenerCorreoActual() {
    return usuarioActual ? usuarioActual.correo : "";
}

/* Cierra la sesión en el servidor y vuelve al inicio. */
async function cerrarSesion() {
    try {
        await api.post("/logout");
    } catch (error) {
        // Aunque falle, se vuelve al inicio: allí se consulta de nuevo la sesión
        console.error("No se pudo cerrar la sesión:", error.message);
    }
    window.location.href = contextPath + "/FrontEnd/Home/Home.jsp";
}
