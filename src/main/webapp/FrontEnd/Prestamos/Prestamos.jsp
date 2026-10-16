<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Préstamos - Wisebooks</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Recursos/CSS/Styles.css">
</head>
<body>

    <!-- Vista de Bibliotecario y Administrador -->
    <div id="header-placeholder"></div>

    <main class="contenedor-prestamos container">

        <section class="franja-titulo">
            <div class="icono-circulo">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="icono-svg" aria-hidden="true">
                    <path d="M1 2.828c.885-.37 2.154-.769 3.388-.893 1.33-.134 2.458.063 3.112.752v9.746c-.935-.53-2.12-.603-3.213-.493-1.18.12-2.37.461-3.287.811zm7.5-.141c.654-.689 1.782-.886 3.112-.752 1.234.124 2.503.523 3.388.893v9.923c-.918-.35-2.107-.692-3.287-.81-1.094-.111-2.278-.039-3.213.492zM8 1.783C7.015.936 5.587.81 4.287.94c-1.514.153-3.042.672-3.994 1.105A.5.5 0 0 0 0 2.5v11a.5.5 0 0 0 .707.455c.882-.4 2.303-.881 3.68-1.02 1.409-.142 2.59.087 3.223.877a.5.5 0 0 0 .78 0c.633-.79 1.814-1.019 3.222-.877 1.378.139 2.8.62 3.681 1.02A.5.5 0 0 0 16 13.5v-11a.5.5 0 0 0-.293-.455c-.952-.433-2.48-.952-3.994-1.105C10.413.809 8.985.936 8 1.783"/>
                </svg>
            </div>
            <h1 class="titulo-prestamos">Préstamos</h1>
            <p class="texto-introductorio">Consulta y registra los préstamos de la biblioteca.</p>
        </section>

        <!-- Avisos de Prestamos.js -->
        <div class="exito-form" id="prestamos-exito" hidden></div>
        <div class="error-form" id="prestamos-error" hidden></div>

        <div class="bloque-buscador row g-2">
            <div class="col-md-3">
                <input type="text" id="buscar-usuario" class="form-control campo-buscar-usuario" placeholder="Buscar por lector...">
            </div>
            <div class="col-md-3">
                <input type="text" id="buscar-libro" class="form-control campo-buscar-libro" placeholder="Buscar por libro...">
            </div>
            <div class="col-md-3">
                <!-- "VENCIDO" no es un estado del backend: son los ACTIVOS con la fecha límite pasada -->
                <select id="filtro-estado-prestamo" class="form-select">
                    <option value="">Todos los estados</option>
                    <option value="ACTIVO" selected>Activos</option>
                    <option value="VENCIDO">Vencidos</option>
                    <option value="DEVUELTO">Devueltos</option>
                    <option value="PERDIDO">Perdidos</option>
                    <option value="ANULADO">Anulados</option>
                </select>
            </div>
            <div class="col-md-3">
                <button type="button" id="boton-registrar-prestamo" class="btn btn-primary boton-registrar-prestamo w-100">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="icono-svg" aria-hidden="true">
                        <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14m0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16"/>
                        <path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4"/>
                    </svg>
                    Registrar préstamo
                </button>
            </div>
        </div>

        <!-- Formulario de préstamo en el mostrador (sin reserva). Se muestra con "Registrar préstamo". -->
        <section id="panel-registrar-prestamo" class="tarjeta-tabla p-3 mb-3" hidden>
            <h2 class="h5">Registrar préstamo en el mostrador</h2>
            <p class="small mb-3">
                Para libros sin reserva previa. Si el lector reservó el libro, entrégalo desde Reservas.
            </p>
            <form id="form-registrar-prestamo" novalidate>
                <div class="row g-2 mb-2">
                    <div class="col-md-8">
                        <label for="buscar-lector-prestamo" class="form-label">Lector</label>
                        <input type="text" id="buscar-lector-prestamo" class="form-control"
                               placeholder="Nombre, correo o documento (mínimo 3 letras)">
                    </div>
                    <div class="col-md-4 d-flex align-items-end">
                        <button type="button" id="boton-buscar-lector-prestamo" class="btn btn-outline-primary w-100">Buscar</button>
                    </div>
                </div>
                <select id="select-lector-prestamo" class="form-select mb-3" aria-label="Lector elegido"></select>

                <label for="select-libro-prestamo" class="form-label">Libro (solo los que tienen copias disponibles)</label>
                <select id="select-libro-prestamo" class="form-select mb-3"></select>

                <button type="submit" id="boton-confirmar-prestamo" class="btn btn-primary">Registrar préstamo</button>
                <button type="button" id="boton-cancelar-prestamo" class="btn btn-outline-secondary">Cancelar</button>
            </form>
        </section>

        <div class="tarjeta-tabla">
            <table class="table table-hover tabla-prestamos">
                <thead>
                    <tr>
                        <th>Lector</th>
                        <th>Libro</th>
                        <th>Fecha de préstamo</th>
                        <th>Fecha límite</th>
                        <th>Estado</th>
                        <th data-rol="administrador" hidden>Acciones</th>
                    </tr>
                </thead>
                <tbody id="cuerpo-prestamos"></tbody>
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
    <script src="${pageContext.request.contextPath}/Recursos/JS/BuscadorLectores.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/CeldasTabla.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Prestamos.js"></script>
</body>
</html>
