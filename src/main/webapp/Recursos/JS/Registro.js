/** Registro.js
Lógica exclusiva de CrearCuenta.jsp: registro real de un Lector.

POST /api/registro {nombre, apellidos, tipoDocumento, documento, telefono,
                    correo, contrasena, aceptaTerminos}
    - El servidor siempre crea la cuenta con rol LECTOR.
    - El registro NO inicia sesión: si sale bien, se lleva al usuario a
      Login.jsp para que entre con su correo y su contraseña.
    - Si algo falla (correo o documento ya registrado, contraseña corta,
      teléfono inválido...), el mensaje del servidor se muestra tal cual.
*/

const formularioRegistro = document.getElementById("registro-form");
const botonRegistrarse = document.getElementById("boton-registrarse");

/* Lee el valor de un campo del formulario, sin espacios al inicio ni al final. */
function valorCampo(id) {
    return document.getElementById(id).value.trim();
}

formularioRegistro.addEventListener("submit", async (e) => {
    e.preventDefault();
    ocultarError("registro-error");

    const camposTexto = [
        "nombre", "apellido", "documento", "telefono",
        "correo-reg", "contrasena-reg", "confirmar-contrasena"
    ];
    const hayVacios = camposTexto.some((id) => valorCampo(id) === "");
    const tipoDocVacio = document.getElementById("tipo-documento").value === "";
    // Las contraseñas no se recortan: los espacios pueden ser parte de ella
    const contrasena = document.getElementById("contrasena-reg").value;
    const confirmacion = document.getElementById("confirmar-contrasena").value;
    const aceptaTerminos = document.getElementById("terminos").checked;

    // Validaciones rápidas en el navegador; las reglas completas las revisa el servidor
    if (hayVacios || tipoDocVacio) {
        mostrarError("registro-error", "Completa todos los campos obligatorios.");
        return;
    }
    if (contrasena !== confirmacion) {
        mostrarError("registro-error", "Las contraseñas no coinciden.");
        return;
    }
    if (!aceptaTerminos) {
        mostrarError("registro-error", "Debes aceptar los términos y condiciones para continuar.");
        return;
    }

    // Los nombres de las propiedades deben coincidir con RegistroDTO del backend
    const registro = {
        nombre: valorCampo("nombre"),
        apellidos: valorCampo("apellido"),
        tipoDocumento: document.getElementById("tipo-documento").value,
        documento: valorCampo("documento"),
        telefono: valorCampo("telefono"),
        correo: valorCampo("correo-reg"),
        contrasena: contrasena,
        aceptaTerminos: aceptaTerminos
    };

    // Se deshabilita el botón para que un doble clic no cree la cuenta dos veces
    botonRegistrarse.disabled = true;
    try {
        await api.post("/registro", registro);
        window.location.href = contextPath + "/FrontEnd/Auth/Login.jsp?registro=ok";
    } catch (error) {
        mostrarError("registro-error", error.message);
        botonRegistrarse.disabled = false;
    }
});
