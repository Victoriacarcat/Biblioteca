<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Iniciar sesión - Wisebooks</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Bootstrap 5.3.3 (CDN) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Estilos propios -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Recursos/CSS/Styles.css">
</head>
<body>

    <div id="header-placeholder"></div>

    <main class="contenedor-auth container">
        <div class="tarjeta-auth">

            <div class="auth-logo">
                <span class="brand-name">WiseBooks</span>
            </div>

            <h2 class="auth-titulo">Iniciar sesión</h2>

            <div class="error-form" id="login-error" hidden>
                Correo o contraseña incorrectos. Verifica tus datos e intenta de nuevo.
            </div>

            <form id="login-form" novalidate>
                <div class="campo">
                    <label for="correo">Correo electrónico</label>
                    <input
                        type="email" id="correo" class="form-control"
                        required autocomplete="username" placeholder="nombre@correo.com">
                </div>

                <div class="campo">
                    <label for="contrasena">Contraseña</label>
                    <input
                        type="password" id="contrasena" class="form-control"
                        required autocomplete="current-password" placeholder="••••••••">
                </div>

                <div class="campo-checkbox">
                    <input type="checkbox" id="mostrar-contrasena">
                    <label for="mostrar-contrasena">Mostrar contraseña</label>
                </div>

                <div class="bloque-simulacion">
                    <span class="bloque-simulacion-titulo">Modo de acceso (simulación)</span>

                    <div class="campo">
                        <label for="rol">Seleccionar rol</label>
                        <select id="rol" class="form-select" required>
                            <option value="" disabled selected>Seleccionar rol ▾</option>
                            <option value="Lector">Lector</option>
                            <option value="Bibliotecario">Bibliotecario</option>
                            <option value="Administrador">Administrador</option>
                        </select>
                    </div>

                    <p class="texto-ayuda">
                        Esto es únicamente para probar las interfaces. No representa un inicio de
                        sesión real: cuando exista el backend, el rol vendrá de la cuenta del usuario.
                    </p>
                </div>

                <button type="submit" class="btn btn-primary btn-block w-100">Iniciar sesión</button>
            </form>

            <p class="texto-enlace-auth">
                ¿No tienes una cuenta? <a href="CrearCuenta.jsp">Crear cuenta</a>
            </p>
        </div>
    </main>

    <script src="${pageContext.request.contextPath}/Recursos/JS/Auth.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Header.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Login.js"></script>
</body>
</html>

