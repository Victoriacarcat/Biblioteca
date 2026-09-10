<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Crear cuenta - Wisebooks</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Bootstrap 5.3.3 (CDN) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Estilos propios -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Recursos/CSS/Styles.css">
</head>
<body>

    <div id="header-placeholder"></div>

    <main class="contenedor-auth container">
        <div class="tarjeta-auth tarjeta-auth-ancha">

            <div class="auth-logo">
                <div class="brand-mark">W</div>
                <span class="brand-name">WiseBooks</span>
            </div>

            <h2 class="auth-titulo">Crear una cuenta</h2>

            <div class="error-form" id="registro-error" hidden></div>

            <form id="registro-form" novalidate>

                <h4 class="titulo-seccion-form">Información personal</h4>

                <div class="fila-campos">
                    <div class="campo">
                        <label for="nombre">Nombre</label>
                        <input type="text" id="nombre" class="form-control" required placeholder="Ana">
                    </div>
                    <div class="campo">
                        <label for="apellido">Apellido</label>
                        <input type="text" id="apellido" class="form-control" required placeholder="Rodríguez">
                    </div>
                </div>

                <div class="fila-campos">
                    <div class="campo">
                        <label for="tipo-documento">Tipo de documento</label>
                        <select id="tipo-documento" class="form-select" required>
                            <option value="" disabled selected>Seleccionar ▾</option>
                            <option value="CC">Cédula de ciudadanía</option>
                            <option value="TI">Tarjeta de identidad</option>
                            <option value="CE">Cédula de extranjería</option>
                            <option value="PA">Pasaporte</option>
                        </select>
                    </div>
                    <div class="campo">
                        <label for="documento">Número de documento</label>
                        <input type="text" id="documento" class="form-control" required placeholder="1 234 567 890">
                    </div>
                </div>

                <div class="campo">
                    <label for="telefono">Teléfono</label>
                    <input type="tel" id="telefono" class="form-control" required placeholder="300 000 0000">
                </div>

                <h4 class="titulo-seccion-form">Información de acceso</h4>

                <div class="campo">
                    <label for="correo-reg">Correo electrónico</label>
                    <input type="email" id="correo-reg" class="form-control" required placeholder="nombre@correo.com">
                </div>

                <div class="fila-campos">
                    <div class="campo">
                        <label for="contrasena-reg">Contraseña</label>
                        <input type="password" id="contrasena-reg" class="form-control" required placeholder="••••••••">
                    </div>
                    <div class="campo">
                        <label for="confirmar-contrasena">Confirmar contraseña</label>
                        <input type="password" id="confirmar-contrasena" class="form-control" required placeholder="••••••••">
                    </div>
                </div>

                <div class="campo-checkbox">
                    <input type="checkbox" id="terminos" required>
                    <label for="terminos">Acepto los <a href="#">términos y condiciones</a></label>
                </div>

                <button type="submit" class="btn btn-primary btn-block w-100">Crear cuenta</button>
            </form>

            <p class="texto-enlace-auth">
                ¿Ya tienes una cuenta? <a href="#"><!-- Login.jsp -->Iniciar sesión</a>
            </p>
        </div>
    </main>

    <script src="${pageContext.request.contextPath}/Recursos/JS/Auth.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Header.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Registro.js"></script>
</body>
</html>

