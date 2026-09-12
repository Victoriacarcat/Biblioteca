<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Panel principal - Wisebooks</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <!-- Bootstrap 5.3.3 (CDN) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Estilos propios -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Recursos/CSS/Styles.css">
</head>
<body>

    <!-- Aquí Header.js inserta el header según el rol -->
    <div id="header-placeholder"></div>

    <main class="contenedor-dashboard container">

        <!-- Visitante-->
        <section class="bloque-dashboard-visitante" data-rol="visitante" hidden>
            <div class="bloque-bienvenida">
                <h1 class="titulo-bienvenida">Bienvenido a WiseBooks</h1>
                <p class="texto-bienvenida">
                    Tu biblioteca digital: explora el catálogo, reserva libros y lleva
                    el control de tus préstamos desde un solo lugar.
                </p>
                <div class="bloque-acciones-visitante">
                    <a href="${pageContext.request.contextPath}/FrontEnd/Catalogo/Catalogo.jsp" class="btn btn-primary">Ver catálogo</a>
                    <a href="${pageContext.request.contextPath}/FrontEnd/Auth/Login.jsp" class="btn btn-outline-primary">Iniciar sesión</a>
                    <a href="${pageContext.request.contextPath}/FrontEnd/Auth/CrearCuenta.jsp" class="btn btn-outline-secondary">Crear cuenta</a>
                </div>
            </div>

            <div class="bloque-info-general row">
                <div class="col-md-6 tarjeta-metrica">
                    <div class="icono-circulo icono-circulo-sm">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="icono-svg" aria-hidden="true">
                            <path d="M1 2.828c.885-.37 2.154-.769 3.388-.893 1.33-.134 2.458.063 3.112.752v9.746c-.935-.53-2.12-.603-3.213-.493-1.18.12-2.37.461-3.287.811zm7.5-.141c.654-.689 1.782-.886 3.112-.752 1.234.124 2.503.523 3.388.893v9.923c-.918-.35-2.107-.692-3.287-.81-1.094-.111-2.278-.039-3.213.492zM8 1.783C7.015.936 5.587.81 4.287.94c-1.514.153-3.042.672-3.994 1.105A.5.5 0 0 0 0 2.5v11a.5.5 0 0 0 .707.455c.882-.4 2.303-.881 3.68-1.02 1.409-.142 2.59.087 3.223.877a.5.5 0 0 0 .78 0c.633-.79 1.814-1.019 3.222-.877 1.378.139 2.8.62 3.681 1.02A.5.5 0 0 0 16 13.5v-11a.5.5 0 0 0-.293-.455c-.952-.433-2.48-.952-3.994-1.105C10.413.809 8.985.936 8 1.783"/>
                        </svg>
                    </div>
                    <h2 class="valor-metrica" id="metrica-total-libros">0</h2>
                    <p class="etiqueta-metrica">Libros disponibles en el catálogo</p>
                </div>
                <div class="col-md-6 tarjeta-metrica">
                    <div class="icono-circulo icono-circulo-sm">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="icono-svg" aria-hidden="true">
                            <path d="M15 14s1 0 1-1-1-4-5-4-5 3-5 4 1 1 1 1zm-7.978-1L7 12.996c.001-.264.167-1.03.76-1.72C8.312 10.629 9.282 10 11 10c1.717 0 2.687.63 3.24 1.276.593.69.758 1.457.76 1.72l-.008.002-.014.002zM11 7a2 2 0 1 0 0-4 2 2 0 0 0 0 4m3-2a3 3 0 1 1-6 0 3 3 0 0 1 6 0M6.936 9.28a6 6 0 0 0-1.23-.247A7 7 0 0 0 5 9c-4 0-5 3-5 4q0 1 1 1h4.216A2.24 2.24 0 0 1 5 13c0-1.01.377-2.042 1.09-2.904.243-.294.526-.569.846-.816M4.92 10A5.5 5.5 0 0 0 4 13H1c0-.26.164-1.03.76-1.724.545-.636 1.492-1.256 3.16-1.275ZM1.5 5.5a3 3 0 1 1 6 0 3 3 0 0 1-6 0m3-2a2 2 0 1 0 0 4 2 2 0 0 0 0-4"/>
                        </svg>
                    </div>
                    <h2 class="valor-metrica" id="metrica-total-usuarios">0</h2>
                    <p class="etiqueta-metrica">Usuarios registrados</p>
                </div>
            </div>
        </section>

        <!-- Lector -->
        <section class="bloque-dashboard-lector panel-metricas" data-rol="lector" hidden>

            <div class="panel-metricas-encabezado">
                <div class="panel-metricas-titulo">
                    <span class="panel-metricas-fecha" id="panel-lector-fecha"></span>
                    <h1>Mi panel</h1>
                    <p>Tus préstamos, reservas y próximas devoluciones, todo en un vistazo.</p>
                </div>
                <div class="panel-metricas-acciones">
                    <a href="${pageContext.request.contextPath}/FrontEnd/Catalogo/Catalogo.jsp" class="btn-panel-metricas btn-panel-metricas--dorado">Explorar catálogo</a>
                </div>
            </div>

            <!-- Mis métricas (personales) + generales de la biblioteca, para
                 que el lector también tenga contexto de la comunidad. -->
            <div class="panel-metricas-kpis" id="panel-lector-kpis"></div>

            <div class="panel-metricas-fila">
                <section class="tarjeta-panel">
                    <h2>Mis próximos vencimientos</h2>
                    <div class="lista-vencimientos" id="lista-vencimientos-lector"></div>
                </section>

                <section class="tarjeta-panel">
                    <h2>Categorías más pedidas</h2>
                    <div class="lista-categorias-pedidas" id="lista-categorias-lector"></div>
                </section>
            </div>

            <section class="seccion-recomendados">
                <h2>Libros recomendados</h2>
                <div id="lista-recomendados" class="row"></div>
            </section>
        </section>

        <!-- Bibliotecario-->
        <section class="bloque-dashboard-bibliotecario panel-metricas" data-rol="bibliotecario" hidden>

            <div class="panel-metricas-encabezado">
                <div class="panel-metricas-titulo">
                    <span class="panel-metricas-fecha" id="panel-biblio-fecha"></span>
                    <h1>Panel del bibliotecario</h1>
                    <p>La operación diaria de la biblioteca, resumida.</p>
                </div>
                <div class="panel-metricas-acciones">
                    <a href="${pageContext.request.contextPath}/FrontEnd/Prestamos/Prestamos.jsp" class="btn-panel-metricas">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="icono-svg" aria-hidden="true">
                            <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14m0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16"/>
                            <path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4"/>
                        </svg>
                        Registrar préstamo
                    </a>
                    <a href="${pageContext.request.contextPath}/FrontEnd/Devoluciones/Devoluciones.jsp" class="btn-panel-metricas btn-panel-metricas--dorado">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="icono-svg" aria-hidden="true">
                            <path fill-rule="evenodd" d="M14.5 1.5a.5.5 0 0 1 .5.5v4.8a2.5 2.5 0 0 1-2.5 2.5H2.707l3.347 3.346a.5.5 0 0 1-.708.708l-4.2-4.2a.5.5 0 0 1 0-.708l4-4a.5.5 0 1 1 .708.708L2.707 8.3H12.5A1.5 1.5 0 0 0 14 6.8V2a.5.5 0 0 1 .5-.5"/>
                        </svg>
                        Registrar devolución
                    </a>
                </div>
            </div>

            <div class="panel-metricas-kpis" id="panel-biblio-kpis"></div>

            <div class="panel-metricas-fila">
                <section class="tarjeta-panel">
                    <h2>Próximas devoluciones</h2>
                    <div class="lista-vencimientos" id="lista-vencimientos-biblio"></div>
                </section>

                <section class="tarjeta-panel">
                    <h2>Categorías más pedidas</h2>
                    <div class="lista-categorias-pedidas" id="lista-categorias-biblio"></div>
                </section>
            </div>

            <div class="panel-metricas-fila">
                <section class="tarjeta-panel">
                    <h2>Estado de los ejemplares</h2>
                    <div class="franja-acervo" id="franja-ejemplares-biblio"></div>
                    <div class="leyenda-acervo" id="leyenda-ejemplares-biblio"></div>
                </section>

                <section class="tarjeta-alertas">
                    <div class="tarjeta-alertas-titulo">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="icono-svg" aria-hidden="true">
                            <path d="M7.938 2.016A.13.13 0 0 1 8.002 2a.13.13 0 0 1 .063.016.15.15 0 0 1 .054.057l6.857 11.667c.036.06.035.124.002.183a.2.2 0 0 1-.054.06.1.1 0 0 1-.066.017H1.146a.1.1 0 0 1-.066-.017.2.2 0 0 1-.054-.06.18.18 0 0 1 .002-.183L7.884 2.073a.15.15 0 0 1 .054-.057m1.044-.45a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767z"/>
                            <path d="M7.002 12a1 1 0 1 1 2 0 1 1 0 0 1-2 0M7.1 5.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0z"/>
                        </svg>
                        <h2>Requiere atención</h2>
                    </div>
                    <div class="lista-alertas" id="lista-alertas-biblio"></div>
                </section>
            </div>
        </section>

        <!-- ================= ADMINISTRADOR ================= -->
        <section class="bloque-dashboard-administrador panel-metricas" data-rol="administrador" hidden>

            <div class="panel-metricas-encabezado">
                <div class="panel-metricas-titulo">
                    <span class="panel-metricas-fecha" id="panel-admin-fecha"></span>
                    <h1>Panel de administración</h1>
                    <p>Visión global de la biblioteca: acervo, usuarios y circulación.</p>
                </div>
                <div class="panel-metricas-acciones">
                    <div class="panel-metricas-rango" id="panel-admin-rango" role="group" aria-label="Rango de fechas">
                        <button type="button" data-rango="7">7 días</button>
                        <button type="button" data-rango="30" class="is-activo">30 días</button>
                    </div>
                    <a href="${pageContext.request.contextPath}/FrontEnd/Inventario/Inventario.jsp" class="btn-panel-metricas">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="icono-svg" aria-hidden="true">
                            <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14m0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16"/>
                            <path d="M8 4a.5.5 0 0 1 .5.5v3h3a.5.5 0 0 1 0 1h-3v3a.5.5 0 0 1-1 0v-3h-3a.5.5 0 0 1 0-1h3v-3A.5.5 0 0 1 8 4"/>
                        </svg>
                        Nuevo libro
                    </a>
                </div>
            </div>

            <div class="panel-metricas-kpis" id="panel-admin-kpis"></div>

            <div class="panel-metricas-fila">
                <section class="tarjeta-panel">
                    <div class="tarjeta-panel-encabezado">
                        <h2>Circulación por semana</h2>
                        <span class="panel-metricas-leyenda">
                            <span><span class="punto-leyenda punto-leyenda--prestamos"></span>Préstamos</span>
                            <span><span class="punto-leyenda punto-leyenda--devoluciones"></span>Devoluciones</span>
                        </span>
                    </div>
                    <div class="grafico-semanal" id="grafico-semanal-admin"></div>
                </section>

                <section class="tarjeta-panel">
                    <h2>Categorías más pedidas</h2>
                    <div class="lista-categorias-pedidas" id="lista-categorias-admin"></div>
                </section>
            </div>

            <div class="panel-metricas-fila">
                <section class="tarjeta-panel">
                    <h2>Estado general del acervo</h2>
                    <div class="franja-acervo" id="franja-acervo-admin"></div>
                    <div class="leyenda-acervo" id="leyenda-acervo-admin"></div>
                    <div class="indicadores-circulacion" id="indicadores-circulacion-admin"></div>
                </section>

                <section class="tarjeta-alertas">
                    <div class="tarjeta-alertas-titulo">
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="icono-svg" aria-hidden="true">
                            <path d="M7.938 2.016A.13.13 0 0 1 8.002 2a.13.13 0 0 1 .063.016.15.15 0 0 1 .054.057l6.857 11.667c.036.06.035.124.002.183a.2.2 0 0 1-.054.06.1.1 0 0 1-.066.017H1.146a.1.1 0 0 1-.066-.017.2.2 0 0 1-.054-.06.18.18 0 0 1 .002-.183L7.884 2.073a.15.15 0 0 1 .054-.057m1.044-.45a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767z"/>
                            <path d="M7.002 12a1 1 0 1 1 2 0 1 1 0 0 1-2 0M7.1 5.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0z"/>
                        </svg>
                        <h2>Requiere atención</h2>
                    </div>
                    <div class="lista-alertas" id="lista-alertas-admin"></div>
                    <a href="${pageContext.request.contextPath}/FrontEnd/Sanciones/Sanciones.jsp" class="btn-panel-metricas btn-panel-metricas--dorado">Gestionar mora</a>
                </section>
            </div>
        </section>

    </main>

    <script>
    const contextPath = "${pageContext.request.contextPath}";
    </script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Auth.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Header.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Iconos.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Data.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Dashboard.js"></script>
</body>
</html>
