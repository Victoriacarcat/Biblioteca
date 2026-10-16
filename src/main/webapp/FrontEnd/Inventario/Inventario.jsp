<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title>Inventario - Wisebooks</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

        <!-- Estilos propios -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/Recursos/CSS/Styles.css">
    </head>

    <body>

        <div id="header-placeholder"></div>

        <main class="contenedor-inventario">

            <!-- Franja de título a todo el ancho -->
            <section class="franja-titulo-foto-fondo franja-titulo-foto-fondo--pizarra"
                     style="background-image:url('${pageContext.request.contextPath}/Recursos/Imagenes/Layout1.jpg')">

                <div class="container">

                    <div class="icono-circulo icono-circulo-pizarra">
                        <svg xmlns="http://www.w3.org/2000/svg"
                             viewBox="0 0 16 16"
                             fill="currentColor"
                             class="icono-svg"
                             aria-hidden="true">

                        <path d="M8.186 1.113a.5.5 0 0 0-.372 0L1.846 3.5l2.404.961L10.404 2zm3.564 1.426L5.596 5 8 5.961 14.154 3.5zm3.25 1.7-6.5 2.6v7.922l6.5-2.6V4.24zM7.5 14.762V6.838L1 4.239v7.923zM7.443.184a1.5 1.5 0 0 1 1.114 0l7.129 2.852A.5.5 0 0 1 16 3.5v8.662a1 1 0 0 1-.629.928l-7.185 2.874a.5.5 0 0 1-.372 0L.63 13.09a1 1 0 0 1-.63-.928V3.5a.5.5 0 0 1 .314-.464z"/>
                        </svg>
                    </div>

                    <h1 class="titulo-inventario">Inventario</h1>

                    <p class="texto-introductorio">
                        Copias de cada libro del catálogo y sus datos.
                    </p>

                </div>
            </section>


            <!-- Contenido de inventario -->
            <div class="container">

                <!-- Métricas: Inventario.js las calcula con los libros recibidos -->
                <div class="fila-metricas row">

                    <div class="col-md-3 tarjeta-metrica">
                        <h2 class="valor-metrica" id="metrica-titulos">0</h2>
                        <p class="etiqueta-metrica">Títulos</p>
                    </div>

                    <div class="col-md-3 tarjeta-metrica">
                        <h2 class="valor-metrica" id="metrica-copias-totales">0</h2>
                        <p class="etiqueta-metrica">Copias totales</p>
                    </div>

                    <div class="col-md-3 tarjeta-metrica">
                        <h2 class="valor-metrica" id="metrica-copias-disponibles">0</h2>
                        <p class="etiqueta-metrica">Copias disponibles</p>
                    </div>

                    <div class="col-md-3 tarjeta-metrica">
                        <h2 class="valor-metrica" id="metrica-copias-prestadas">0</h2>
                        <p class="etiqueta-metrica">Prestadas o apartadas</p>
                    </div>

                </div>


                <!-- Avisos de Inventario.js -->
                <div class="exito-form" id="inventario-exito" hidden></div>
                <div class="error-form" id="inventario-error" hidden></div>


                <!-- Buscador, filtro y botón de libro nuevo -->
                <div class="bloque-buscador row g-2 align-items-center">

                    <div class="col-md-6">
                        <input type="text"
                               id="buscar-libro-inventario"
                               class="form-control"
                               placeholder="Buscar por título, autor o ISBN...">
                    </div>

                    <div class="col-md-3">
                        <div class="form-check">
                            <input type="checkbox" id="mostrar-desactivados" class="form-check-input">
                            <label for="mostrar-desactivados" class="form-check-label">Mostrar desactivados</label>
                        </div>
                    </div>

                    <div class="col-md-3">
                        <button type="button" id="boton-nuevo-libro" class="btn btn-primary w-100"
                                data-rol="administrador" hidden>
                            Nuevo libro
                        </button>
                    </div>

                </div>


                <!-- Formulario de libro (solo administrador): sirve para crear y para editar -->
                <section id="panel-libro" class="tarjeta-tabla p-3 mb-3" hidden>

                    <h2 class="h5" id="titulo-panel-libro">Nuevo libro</h2>
                    <p class="small mb-3">
                        Si la categoría, el género, la editorial o un autor no existen, se crean al guardar.
                    </p>

                    <form id="form-libro" novalidate>
                        <!-- id del libro que se edita; vacío al crear uno nuevo -->
                        <input type="hidden" id="libro-id">

                        <div class="row g-2 mb-2">
                            <div class="col-md-8">
                                <label for="libro-titulo" class="form-label">Título *</label>
                                <input type="text" id="libro-titulo" class="form-control" maxlength="255">
                            </div>
                            <div class="col-md-4">
                                <label for="libro-isbn" class="form-label">ISBN</label>
                                <input type="text" id="libro-isbn" class="form-control" maxlength="20">
                            </div>
                        </div>

                        <div class="mb-2">
                            <label for="libro-autores" class="form-label">Autores * (separados por coma)</label>
                            <input type="text" id="libro-autores" class="form-control" list="lista-autores"
                                   placeholder="Gabriel García Márquez, Otro Autor">
                        </div>

                        <div class="row g-2 mb-2">
                            <div class="col-md-4">
                                <label for="libro-categoria" class="form-label">Categoría *</label>
                                <input type="text" id="libro-categoria" class="form-control" list="lista-categorias" maxlength="100">
                            </div>
                            <div class="col-md-4">
                                <label for="libro-genero" class="form-label">Género</label>
                                <input type="text" id="libro-genero" class="form-control" list="lista-generos" maxlength="100">
                            </div>
                            <div class="col-md-4">
                                <label for="libro-editorial" class="form-label">Editorial</label>
                                <input type="text" id="libro-editorial" class="form-control" list="lista-editoriales" maxlength="150">
                            </div>
                        </div>

                        <div class="row g-2 mb-2">
                            <div class="col-md-4">
                                <label for="libro-fecha" class="form-label">Fecha de publicación</label>
                                <input type="date" id="libro-fecha" class="form-control">
                            </div>
                            <div class="col-md-4">
                                <label for="libro-ubicacion" class="form-label">Ubicación</label>
                                <input type="text" id="libro-ubicacion" class="form-control" maxlength="100" placeholder="Estante A3">
                            </div>
                            <!-- El stock solo se fija al crear; después se cambia con Agregar/Retirar copias -->
                            <div class="col-md-4" id="grupo-stock-inicial">
                                <label for="libro-stock" class="form-label">Copias iniciales</label>
                                <input type="number" id="libro-stock" class="form-control" min="0" value="1">
                            </div>
                        </div>

                        <div class="mb-2">
                            <label for="libro-portada" class="form-label">Dirección de la portada</label>
                            <input type="text" id="libro-portada" class="form-control" maxlength="500"
                                   placeholder="https://... o /Recursos/Imagenes/portada.jpg">
                        </div>

                        <div class="mb-3">
                            <label for="libro-descripcion" class="form-label">Descripción</label>
                            <textarea id="libro-descripcion" class="form-control" rows="3" maxlength="5000"></textarea>
                        </div>

                        <button type="submit" id="boton-guardar-libro" class="btn btn-primary">Guardar</button>
                        <button type="button" id="boton-cancelar-libro" class="btn btn-outline-secondary">Cancelar</button>
                    </form>

                    <!-- Sugerencias para no crear duplicados (vienen de /api/catalogos) -->
                    <datalist id="lista-categorias"></datalist>
                    <datalist id="lista-generos"></datalist>
                    <datalist id="lista-editoriales"></datalist>
                    <datalist id="lista-autores"></datalist>

                </section>


                <!-- Tabla -->
                <div class="tarjeta-tabla">

                    <table class="table table-hover tabla-inventario">

                        <thead>
                            <tr>
                                <th>Libro</th>
                                <th>Categoría</th>
                                <th>Copias</th>
                                <th>Disponibles</th>
                                <th>Prestadas o apartadas</th>
                                <th>Estado</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>

                        <tbody id="cuerpo-inventario"></tbody>

                    </table>

                </div>

            </div>

        </main>


        <!-- Scripts -->
        <script>
        const contextPath = "${pageContext.request.contextPath}";
        </script>

        <script src="${pageContext.request.contextPath}/Recursos/JS/Api.js"></script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Auth.js"></script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Utilidades.js"></script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Header.js"></script>
        <script src="${pageContext.request.contextPath}/Recursos/JS/Inventario.js"></script>

    </body>
</html>
