// Login.js
// Lógica exclusiva de Login.jsp

const inputContrasena = document.getElementById("contrasena");
document.getElementById("mostrar-contrasena").addEventListener("change", (e) => {
    inputContrasena.type = e.target.checked ? "text" : "password";
});

const formularioLogin = document.getElementById("login-form");
const cajaErrorLogin = document.getElementById("login-error");

formularioLogin.addEventListener("submit", (e) => {
    e.preventDefault();

    const correo = document.getElementById("correo").value.trim();
    const contrasena = document.getElementById("contrasena").value.trim();
    // Normalizado a minúscula para que coincida con ROLES_VALIDOS de auth.js,
    // sin importar si el <select> quedó en mayúscula por error.
    const rol = document.getElementById("rol").value.trim().toLowerCase();

    // Validación mínima de frontend. La validación real (credenciales
    // contra la BD) llegará con el backend (Servlet + DAO).
    if (!correo || !contrasena || !rol) {
        cajaErrorLogin.textContent = "Completa correo, contraseña y selecciona un rol para continuar.";
        cajaErrorLogin.hidden = false;
        return;
    }

    cajaErrorLogin.hidden = true;

    iniciarSesionSimulada(rol, correo);

    // Redirección real: FrontEnd ya no vive bajo WEB-INF, así que
    // Catalogo.jsp es alcanzable directo por el navegador.
    window.location.href = contextPath + "/FrontEnd/Catalogo/Catalogo.jsp";
});
