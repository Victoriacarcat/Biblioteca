<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Usuarios - Wisebooks</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Recursos/CSS/Styles.css">
</head>
<body>
    <!-- Vista exclusiva de Administrador -->
    <div id="header-placeholder"></div>
    <main class="contenedor-usuarios container">
        <section class="franja-titulo">
            <div class="icono-circulo">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="icono-svg" aria-hidden="true">
                    <path d="M15 14s1 0 1-1-1-4-5-4-5 3-5 4 1 1 1 1zm-7.978-1L7 12.996c.001-.264.167-1.03.76-1.72C8.312 10.629 9.282 10 11 10c1.717 0 2.687.63 3.24 1.276.593.69.758 1.457.76 1.72l-.008.002-.014.002zM11 7a2 2 0 1 0 0-4 2 2 0 0 0 0 4m3-2a3 3 0 1 1-6 0 3 3 0 0 1 6 0M6.936 9.28a6 6 0 0 0-1.23-.247A7 7 0 0 0 5 9c-4 0-5 3-5 4q0 1 1 1h4.216A2.24 2.24 0 0 1 5 13c0-1.01.377-2.042 1.09-2.904.243-.294.526-.569.846-.816M4.92 10A5.5 5.5 0 0 0 4 13H1c0-.26.164-1.03.76-1.724.545-.636 1.492-1.256 3.16-1.275ZM1.5 5.5a3 3 0 1 1 6 0 3 3 0 0 1-6 0m3-2a2 2 0 1 0 0 4 2 2 0 0 0 0-4"/>
                </svg>
            </div>
            <h1 class="titulo-usuarios">Usuarios</h1>
            <p class="texto-introductorio">Gestiona el rol y el estado de las cuentas registradas.</p>
        </section>

        <!-- Avisos de Usuarios.js -->
        <div class="exito-form" id="usuarios-exito" hidden></div>
        <div class="error-form" id="usuarios-error" hidden></div>

        <div class="bloque-buscador row g-2">
            <div class="col-md-4">
                <input type="text" id="buscar-usuario-admin" class="form-control" placeholder="Buscar por nombre, correo o documento...">
            </div>
            <div class="col-md-3">
                <!-- Los value son los roles del backend (Rol) -->
                <select id="filtro-rol-usuario" class="form-select campo-filtro">
                    <option value="">Todos los roles</option>
                    <option value="LECTOR">Lector</option>
                    <option value="BIBLIOTECARIO">Bibliotecario</option>
                    <option value="ADMINISTRADOR">Administrador</option>
                </select>
            </div>
            <div class="col-md-2">
                <!-- Los value son los estados del backend (EstadoUsuario) -->
                <select id="filtro-estado-usuario" class="form-select campo-filtro">
                    <option value="">Todos los estados</option>
                    <option value="ACTIVO">Activo</option>
                    <option value="SUSPENDIDO">Suspendido</option>
                    <option value="INACTIVO">Inactivo</option>
                </select>
            </div>
            <div class="col-md-3">
                <button type="button" id="boton-crear-usuario" class="btn btn-primary w-100">Crear usuario</button>
            </div>
        </div>

        <!-- Cuenta nueva de cualquier rol (lector, bibliotecario o administrador) -->
        <section id="panel-crear-usuario" class="tarjeta-tabla p-3 mb-3" hidden>
            <h2 class="h5">Crear cuenta</h2>
            <p class="small mb-3">
                Entrega la contraseña temporal a la persona y pídele que la cambie al entrar.
                El rol de un administrador no se podrá cambiar después desde la aplicación.
            </p>
            <form id="form-crear-usuario" novalidate>
                <div class="mb-2">
                    <label for="nuevo-rol" class="form-label">Rol</label>
                    <!-- Los value son los roles del backend (Rol) -->
                    <select id="nuevo-rol" class="form-select">
                        <option value="LECTOR">Lector</option>
                        <option value="BIBLIOTECARIO" selected>Bibliotecario</option>
                        <option value="ADMINISTRADOR">Administrador</option>
                    </select>
                </div>
                <div class="row g-2 mb-2">
                    <div class="col-md-6">
                        <label for="nuevo-nombre" class="form-label">Nombre</label>
                        <input type="text" id="nuevo-nombre" class="form-control" maxlength="100">
                    </div>
                    <div class="col-md-6">
                        <label for="nuevo-apellidos" class="form-label">Apellidos</label>
                        <input type="text" id="nuevo-apellidos" class="form-control" maxlength="100">
                    </div>
                </div>
                <div class="row g-2 mb-2">
                    <div class="col-md-4">
                        <label for="nuevo-tipo-documento" class="form-label">Tipo de documento</label>
                        <select id="nuevo-tipo-documento" class="form-select">
                            <option value="CC">Cédula de ciudadanía</option>
                            <option value="TI">Tarjeta de identidad</option>
                            <option value="CE">Cédula de extranjería</option>
                            <option value="PA">Pasaporte</option>
                        </select>
                    </div>
                    <div class="col-md-4">
                        <label for="nuevo-documento" class="form-label">Número de documento</label>
                        <input type="text" id="nuevo-documento" class="form-control" maxlength="20">
                    </div>
                    <div class="col-md-4">
                        <label for="nuevo-telefono" class="form-label">Teléfono</label>
                        <input type="tel" id="nuevo-telefono" class="form-control" maxlength="20">
                    </div>
                </div>
                <div class="row g-2 mb-3">
                    <div class="col-md-6">
                        <label for="nuevo-correo" class="form-label">Correo</label>
                        <input type="email" id="nuevo-correo" class="form-control" maxlength="150" autocomplete="off">
                    </div>
                    <div class="col-md-6">
                        <label for="nuevo-contrasena" class="form-label">Contraseña temporal (mínimo 8 caracteres)</label>
                        <input type="text" id="nuevo-contrasena" class="form-control" autocomplete="off">
                    </div>
                </div>
                <button type="submit" id="boton-confirmar-usuario" class="btn btn-primary">Crear cuenta</button>
                <button type="button" id="boton-cancelar-usuario" class="btn btn-outline-secondary">Cancelar</button>
            </form>
        </section>

        <div class="tarjeta-tabla">
            <table class="table table-hover tabla-usuarios">
                <thead>
                    <tr>
                        <th>Nombre</th>
                        <th>Correo</th>
                        <th>Rol</th>
                        <th>Estado</th>
                        <th>Registrado el</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody id="cuerpo-usuarios"></tbody>
            </table>
        </div>

    </main>
    <script>
    const contextPath = "${pageContext.request.contextPath}";
    </script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Api.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Auth.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Utilidades.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Header.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Iconos.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Usuarios.js"></script>
</body>
</html>
