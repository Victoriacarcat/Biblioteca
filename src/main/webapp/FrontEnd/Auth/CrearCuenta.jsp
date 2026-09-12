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

        <main class="contenedor-auth">

            <section class="franja-titulo">
                <h1>Crear cuenta</h1>
                <p>Regístrate para crear tu cuenta de Lector y empezar a reservar libros y gestionar tus préstamos.</p>
            </section>

            <section class="seccion-cuenta">
                <div class="container auth-split">

                    <div class="auth-intro">
                        <div class="tarjeta-foto-intro recorte-invertido">
                            <img class="foto-intro"
                                 src="${pageContext.request.contextPath}/Recursos/Imagenes/librería-img1.jpg"
                                 alt="Interior de una cafetería-biblioteca">
                            <svg class="sello-intro" viewBox="0 0 60 60" aria-hidden="true">
                            <circle cx="30" cy="30" r="27" fill="none" stroke="var(--verde)" stroke-width="2"/>
                            <path d="M14 24 C20 20 26 20 30 24 C34 20 40 20 46 24 V38 C40 34 34 34 30 38 C26 34 20 34 14 38 Z"
                                  fill="none" stroke="var(--verde)" stroke-width="2" stroke-linejoin="round"/>
                            </svg>
                        </div>
                        <span class="etiqueta-seccion">Registro</span>
                        <h2>Crea tu cuenta de Lector</h2>
                        <p>Regístrate con tus datos para empezar a reservar libros y gestionar tus préstamos.</p>
                    </div>

                    <div class="auth-panel-form">

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
                                    <input type="text" id="documento" class="form-control" required placeholder="1021387416">
                                </div>
                            </div>

                            <div class="campo">
                                <label for="telefono">Teléfono</label>
                                <input type="tel" id="telefono" class="form-control" required placeholder="300000 0000">
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

                            <button type="submit" class="btn btn-primary btn-block w-100">Registrarse</button>
                        </form>

                        <p class="texto-enlace-auth">
                            ¿Ya tienes una cuenta? <a href="Login.jsp">Iniciar sesión</a>
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
                            <li><a href="${pageContext.request.contextPath}/FrontEnd/Home/Home.jsp">Inicio</a></li>
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
        <script src="${pageContext.request.contextPath}/Recursos/JS/Registro.js"></script>
    </body>
</html>

