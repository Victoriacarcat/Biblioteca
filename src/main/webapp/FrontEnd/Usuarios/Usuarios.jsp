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

        <div class="error-form" id="usuarios-error" hidden></div>

        <div class="bloque-buscador row">
            <div class="col-md-6">
                <input type="text" id="buscar-usuario-admin" class="form-control" placeholder="Buscar por nombre o correo...">
            </div>
            <div class="col-md-3">
                <select id="filtro-rol-usuario" class="form-select campo-filtro">
                    <option value="">Todos los roles</option>
                    <option value="lector">Lector</option>
                    <option value="bibliotecario">Bibliotecario</option>
                    <option value="administrador">Administrador</option>
                </select>
            </div>
            <div class="col-md-3">
                <select id="filtro-estado-usuario" class="form-select campo-filtro">
                    <option value="">Todos los estados</option>
                    <option value="Activo">Activo</option>
                    <option value="Suspendido">Suspendido</option>
                </select>
            </div>
        </div>

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
    <script src="${pageContext.request.contextPath}/Recursos/JS/Auth.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Header.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Iconos.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Data.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Usuarios.js"></script>
</body>
</html>
