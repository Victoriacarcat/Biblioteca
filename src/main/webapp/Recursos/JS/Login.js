// login.js
// Lógica del formulario de login. No hay backend todavía: "iniciar sesión"
// solo valida que los campos estén completos y guarda una sesión simulada
// (rol + correo) usando las funciones de auth.js.

const formularioLogin = document.getElementById("login-form");
const campoCorreo = document.getElementById("correo");
const campoContrasena = document.getElementById("contrasena");
const campoRol = document.getElementById("rol");
const mensajeErrorLogin = document.getElementById("login-error");
const checkboxMostrarContrasena = document.getElementById("mostrar-contrasena");

// Oculto el mensaje de error hasta que realmente haga falta mostrarlo.
mensajeErrorLogin.hidden = true;

// Alternar visibilidad de la contraseña.
checkboxMostrarContrasena.addEventListener("change", function () {
    campoContrasena.type = checkboxMostrarContrasena.checked ? "text" : "password";
});

formularioLogin.addEventListener("submit", function (evento) {
    evento.preventDefault();

    const correo = campoCorreo.value.trim();
    const contrasena = campoContrasena.value;
    const rol = campoRol.value;

    if (!correo || !contrasena || !rol) {
        mensajeErrorLogin.hidden = false;
        return;
    }

    mensajeErrorLogin.hidden = true;
    iniciarSesionSimulada(rol, correo);
    window.location.href = "catalogo.html";
});


