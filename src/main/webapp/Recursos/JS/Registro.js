// registro.js
// Lógica del formulario de crear cuenta. No hay backend todavía: solo se
// valida que los campos estén completos, que las contraseñas coincidan y
// que se acepten los términos. No inicia sesión ni guarda nada: al validar
// correctamente, redirige a login.html para que el usuario inicie sesión.

const formularioRegistro = document.getElementById("registro-form");
const mensajeErrorRegistro = document.getElementById("registro-error");

const camposRequeridos = [
    document.getElementById("nombre"),
    document.getElementById("apellido"),
    document.getElementById("tipo-documento"),
    document.getElementById("documento"),
    document.getElementById("telefono"),
    document.getElementById("correo-reg"),
    document.getElementById("contrasena-reg"),
    document.getElementById("confirmar-contrasena"),
];

const campoContrasenaReg = document.getElementById("contrasena-reg");
const campoConfirmarContrasena = document.getElementById("confirmar-contrasena");
const checkboxTerminos = document.getElementById("terminos");

// Oculto el mensaje de error hasta que realmente haga falta mostrarlo.
mensajeErrorRegistro.hidden = true;

function mostrarErrorRegistro(mensaje) {
    mensajeErrorRegistro.textContent = mensaje;
    mensajeErrorRegistro.hidden = false;
}

formularioRegistro.addEventListener("submit", function (evento) {
    evento.preventDefault();

    const hayCampoVacio = camposRequeridos.some(function (campo) {
        return campo.value.trim() === "";
    });

    if (hayCampoVacio) {
        mostrarErrorRegistro("Completa todos los campos para continuar.");
        return;
    }

    if (campoContrasenaReg.value !== campoConfirmarContrasena.value) {
        mostrarErrorRegistro("Las contraseñas no coinciden.");
        return;
    }

    if (!checkboxTerminos.checked) {
        mostrarErrorRegistro("Debes aceptar los términos y condiciones.");
        return;
    }

    mensajeErrorRegistro.hidden = true;
    window.location.href = "login.html";
});


