/** Login.js
Lógica exclusiva de Login.jsp: inicio de sesión real contra el backend.

POST /api/login {correo, contrasena}
    - Si los datos son correctos, el servidor abre la sesión (cookie
      JSESSIONID) y se redirige al inicio.
    - Si no, responde con un error ("Correo o contraseña incorrectos.",
      "Tu cuenta está desactivada...") que se muestra tal cual.
El rol ya no se elige aquí: lo trae la cuenta del usuario.
*/

const inputContrasena = document.getElementById("contrasena");
document.getElementById("mostrar-contrasena").addEventListener("change", (e) => {
    inputContrasena.type = e.target.checked ? "text" : "password";
});

// Si se llega desde CrearCuenta (Login.jsp?registro=ok), se avisa que la cuenta ya existe
if (new URLSearchParams(window.location.search).get("registro") === "ok") {
    const cajaExito = document.getElementById("login-exito");
    cajaExito.textContent = "Tu cuenta fue creada. Ya puedes iniciar sesión.";
    cajaExito.hidden = false;
}

const formularioLogin = document.getElementById("login-form");
const botonIniciarSesion = document.getElementById("boton-iniciar-sesion");

formularioLogin.addEventListener("submit", async (e) => {
    e.preventDefault();
    ocultarError("login-error");

    const correo = document.getElementById("correo").value.trim();
    // La contraseña no se recorta: los espacios pueden ser parte de ella
    const contrasena = document.getElementById("contrasena").value;

    // Validación mínima en el navegador; la validación real la hace el servidor
    if (!correo || !contrasena) {
        mostrarError("login-error", "Escribe tu correo y tu contraseña.");
        return;
    }

    // Se deshabilita el botón para que un doble clic no envíe dos peticiones
    botonIniciarSesion.disabled = true;
    try {
        await api.post("/login", { correo: correo, contrasena: contrasena });
        // Tras iniciar sesión, Home.jsp es lo primero que ve el usuario
        window.location.href = contextPath + "/FrontEnd/Home/Home.jsp";
    } catch (error) {
        mostrarError("login-error", error.message);
        botonIniciarSesion.disabled = false;
    }
});
