/**Registro.js
// Lógica exclusiva de CrearCuenta.jsp

Importante: el registro NO inicia sesión automáticamente. Solo valida
el formulario y, si todo está correcto, redirige a Login.jsp para que
el usuario inicie sesión aparte (igual que en un flujo real).
TODO: cuando exista el backend, aquí se hará un INSERT vía DAO
(con validación de duplicados de correo/documento).*/

const formularioRegistro = document.getElementById("registro-form");
const cajaErrorRegistro = document.getElementById("registro-error");

formularioRegistro.addEventListener("submit", (e) => {
    e.preventDefault();

    const camposTexto = [
        "nombre", "apellido", "documento", "telefono",
        "correo-reg", "contrasena-reg", "confirmar-contrasena"
    ];
    const hayVacios = camposTexto.some((id) => document.getElementById(id).value.trim() === "");
    const tipoDocVacio = document.getElementById("tipo-documento").value === "";
    const contrasena = document.getElementById("contrasena-reg").value;
    const confirmacion = document.getElementById("confirmar-contrasena").value;
    const aceptaTerminos = document.getElementById("terminos").checked;

    if (hayVacios || tipoDocVacio) {
        cajaErrorRegistro.textContent = "Completa todos los campos obligatorios.";
        cajaErrorRegistro.hidden = false;
        return;
    }
    if (contrasena !== confirmacion) {
        cajaErrorRegistro.textContent = "Las contraseñas no coinciden.";
        cajaErrorRegistro.hidden = false;
        return;
    }
    if (!aceptaTerminos) {
        cajaErrorRegistro.textContent = "Debes aceptar los términos y condiciones para continuar.";
        cajaErrorRegistro.hidden = false;
        return;
    }

    cajaErrorRegistro.hidden = true;

    // TODO: ruta temporal ("#"). Login.jsp vive dentro de WEB-INF y no es
    // alcanzable directo por el navegador. Cuando exista el servlet
    // controlador, reemplazar por la URL limpia real (ej. "/login").
    window.location.href = "#";
});
