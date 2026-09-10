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
    const rol = document.getElementById("rol").value;

    // Validación mínima de frontend. La validación real (credenciales
    // contra la BD) llegará con el backend (Servlet + DAO).
    if (!correo || !contrasena || !rol) {
        cajaErrorLogin.textContent = "Completa correo, contraseña y selecciona un rol para continuar.";
        cajaErrorLogin.hidden = false;
        return;
    }

    cajaErrorLogin.hidden = true;

    iniciarSesionSimulada(rol, correo);

    // TODO: esta ruta es temporal. Cuando exista un servlet controlador,
    // el flujo real será: Servlet valida credenciales -> forward a Catalogo.jsp.
    window.location.href = "../Catalogo/Catalogo.jsp";
});

