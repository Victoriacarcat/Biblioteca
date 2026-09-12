<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Iniciar sesión - Wisebooks</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Recursos/CSS/Styles.css">
    </head>
    <body>

        <div id="header-placeholder"></div>

        <main class="contenedor-auth">

            <section class="franja-titulo">
                <h1>Iniciar sesión</h1>
                <p>Accede con tu correo y contraseña. Selecciona tu rol para entrar a tu entorno: Lector, Bibliotecario o Administrador.</p>
            </section>

            <section class="seccion-cuenta">
                <div class="container auth-split">

                    <div class="auth-intro">
                        <div class="tarjeta-foto-intro">
                            <img class="foto-intro"
                                 src="${pageContext.request.contextPath}/Recursos/Imagenes/librería-img1.jpg"
                                 alt="Interior de una biblioteca">
                            <svg class="sello-intro" viewBox="0 0 60 60" aria-hidden="true">
                                <circle cx="30" cy="30" r="27" fill="none" stroke="var(--verde)" stroke-width="2"/>
                                <path d="M14 24 C20 20 26 20 30 24 C34 20 40 20 46 24 V38 C40 34 34 34 30 38 C26 34 20 34 14 38 Z"
                                      fill="none" stroke="var(--verde)" stroke-width="2" stroke-linejoin="round"/>
                            </svg>
                        </div>

                        <span class="etiqueta-seccion">Acceso</span>
                        <h2>Accede a tu cuenta</h2>
                        <p>Selecciona tu rol para entrar a tu entorno. Este selector es solo para simular el acceso del frontend.</p>
                    </div>
                    <div class="auth-panel-form">
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
                                        <option value="" disabled selected>Seleccionar rol</option>
                                        <option value="lector">Lector</option>
                                        <option value="bibliotecario">Bibliotecario</option>
                                        <option value="administrador">Administrador</option>
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
                            ¿No tienes una cuenta? <a href="CrearCuenta.jsp">Crea una cuenta de Lector</a>
                        </p>
                    </div>

                </div>
            </section>

        </main>

        <footer class="pie-app">
            <div class="container">
                <div class="row gy-4">
                    <div class="col-6 col-md-3">
                        <span class="nombre-marca">WiseBooks</span>
                    </div>
                    <div class="col-6 col-md-3">
                        <h6>Explorar</h6>
                        <ul>
                            <li><a href="${pageContext.request.contextPath}/index.html">Inicio</a></li>
                            <li><a href="${pageContext.request.contextPath}/FrontEnd/Catalogo/Catalogo.jsp">Catálogo</a></li>
                        </ul>
                    </div>
                    <div class="col-6 col-md-3">
                        <h6>Cuenta</h6>
                        <ul>
                            <li><a href="Login.jsp">Iniciar sesión</a></li>
                            <li><a href="CrearCuenta.jsp">Crear cuenta</a></li>
                        </ul>
                    </div>
                    <div class="col-6 col-md-3">
                        <h6>¿Listo para explorar?</h6>
                        <p>Entra a tu cuenta y continúa donde lo dejaste.</p>
                        <a href="Login.jsp" class="btn btn-outline-light btn-sm">Iniciar sesión</a>
                    </div>
                </div>
            </div>
        </footer>

        <script>
        const contextPath = "${pageContext.request.contextPath}";
        </script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Auth.js"></script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Header.js"></script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Login.js"></script>
    </body>
</html>
