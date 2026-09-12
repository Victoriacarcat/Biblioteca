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

            <section class="franja-titulo">
                <h1 class="titulo-catalogo">Catálogo de libros</h1>
                <p class="texto-introductorio">Explora nuestra colección y encuentra el libro que buscas.</p>
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

                    <!-- Aquí Catalogo.js inserta las cards de libros -->
                    <div id="contenedor-libros" class="contenedor-libros row"></div>

                </section>
            </div>
        </main>

        <script>
        const contextPath = "${pageContext.request.contextPath}";
        </script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Auth.js"></script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Header.js"></script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Data.js"></script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Catalogo.js"></script>
    </body>
</html>

