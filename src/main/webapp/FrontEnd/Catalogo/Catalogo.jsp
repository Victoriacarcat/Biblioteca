<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Catálogo de libros - Wisebooks</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <!-- Bootstrap -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

        <!-- Estilos propios -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Recursos/CSS/Styles.css">
    </head>
    <body>
        <!-- Aquí Header.js inserta el header según el rol -->
        <div id="header-placeholder"></div>

        <main class="contenedor-catalogo container">

            <section class="seccion-hero-catalogo">
                <div class="hero-catalogo-split">
                    <div class="hero-catalogo-texto">
                        <span class="etiqueta-seccion">
                            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16" fill="currentColor" class="icono-svg" aria-hidden="true">
                                <path d="M1 2.828c.885-.37 2.154-.769 3.388-.893 1.33-.134 2.458.063 3.112.752v9.746c-.935-.53-2.12-.603-3.213-.493-1.18.12-2.37.461-3.287.811zm7.5-.141c.654-.689 1.782-.886 3.112-.752 1.234.124 2.503.523 3.388.893v9.923c-.918-.35-2.107-.692-3.287-.81-1.094-.111-2.278-.039-3.213.492zM8 1.783C7.015.936 5.587.81 4.287.94c-1.514.153-3.042.672-3.994 1.105A.5.5 0 0 0 0 2.5v11a.5.5 0 0 0 .707.455c.882-.4 2.303-.881 3.68-1.02 1.409-.142 2.59.087 3.223.877a.5.5 0 0 0 .78 0c.633-.79 1.814-1.019 3.222-.877 1.378.139 2.8.62 3.681 1.02A.5.5 0 0 0 16 13.5v-11a.5.5 0 0 0-.293-.455c-.952-.433-2.48-.952-3.994-1.105C10.413.809 8.985.936 8 1.783"/>
                            </svg>
                            Catálogo completo
                        </span>
                        <h1 class="titulo-catalogo">Encontrá tu próxima lectura</h1>
                        <p class="texto-introductorio">Buscá por título, autor o categoría entre los libros disponibles en WiseBooks.</p>
                    </div>
                    <div class="pila-libros-decorativa" aria-hidden="true">
                        <img class="libro-apilado" src="${pageContext.request.contextPath}/Recursos/Imagenes/portada-1984.jpg" alt="">
                        <img class="libro-apilado" src="${pageContext.request.contextPath}/Recursos/Imagenes/PORTADA-breveHistoriaDelTiempo.jpg" alt="">
                        <img class="libro-apilado" src="${pageContext.request.contextPath}/Recursos/Imagenes/portada-cienAñosdeSoledad.jpg" alt="">
                    </div>
                </div>
            </section>

            <!-- Buscador -->
            <div class="bloque-buscador">
                <input
                    type="text"
                    id="campo-busqueda"
                    class="campo-busqueda"
                    placeholder="Buscar por título, autor, ISBN...">
                <button id="boton-buscar" class="btn btn-primary boton-buscar">Buscar</button>
            </div>

            <div class="fila-catalogo row">

                <!-- Sidebar de filtros -->
                <aside class="sidebar-filtros col-md-3">
                    <h2 class="titulo-filtros">Filtrar por</h2>

                    <div class="grupo-filtro">
                        <label for="filtro-categoria">Categoría</label>
                        <select id="filtro-categoria" class="campo-filtro">
                            <option value="">Todas</option>
                        </select>
                    </div>

                    <div class="grupo-filtro">
                        <label for="filtro-autor">Autor</label>
                        <select id="filtro-autor" class="campo-filtro">
                            <option value="">Todos</option>
                        </select>
                    </div>

                    <div class="grupo-filtro">
                        <label for="filtro-genero">Género</label>
                        <select id="filtro-genero" class="campo-filtro">
                            <option value="">Todos</option>
                        </select>
                    </div>

                    <div class="grupo-filtro">
                        <label for="filtro-disponibilidad">Disponibilidad</label>
                        <select id="filtro-disponibilidad" class="campo-filtro">
                            <option value="">Todas</option>
                            <option value="disponible">Disponible</option>
                            <option value="no-disponible">No disponible</option>
                        </select>
                    </div>

                    <div class="grupo-filtro">
                        <label for="filtro-formato">Formato</label>
                        <select id="filtro-formato" class="campo-filtro">
                            <option value="">Todos</option>
                        </select>
                    </div>

                    <div class="grupo-filtro">
                        <label for="filtro-fecha">Año de publicación</label>
                        <select id="filtro-fecha" class="campo-filtro">
                            <option value="">Todos</option>
                        </select>
                    </div>

                    <button id="boton-limpiar-filtros" class="btn btn-outline-secondary boton-limpiar-filtros">
                        Limpiar filtros
                    </button>
                </aside>

                <!-- Resultados -->
                <section class="seccion-resultados col-md-9">

                    <div class="barra-orden">
                        <label for="orden-resultados">Ordenar por:</label>
                        <select id="orden-resultados" class="campo-orden">
                            <option value="relevantes">Más relevantes</option>
                            <option value="titulo-asc">Título A-Z</option>
                            <option value="titulo-desc">Título Z-A</option>
                            <option value="recientes">Más recientes</option>
                            <option value="antiguos">Más antiguos</option>
                        </select>
                    </div>

                    <!--Catalogo.js inserta las cards de libros -->
                    <div id="contenedor-libros" class="contenedor-libros row"></div>

                </section>
            </div>
        </main>

        <script>
        const contextPath = "${pageContext.request.contextPath}";
        </script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Auth.js"></script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Header.js"></script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Iconos.js"></script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Data.js"></script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Catalogo.js"></script>
    </body>
</html>

