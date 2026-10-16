<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Detalle del libro - Wisebooks</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Recursos/CSS/Styles.css">
</head>
<body>

    <div id="header-placeholder"></div>

    <main class="contenedor-detalle-libro">

        <!-- Si el libro no existe o falla la carga, Detallelibro.js lo explica aquí -->
        <div class="container">
            <div class="error-form mt-4" id="detalle-error" hidden></div>
            <div class="exito-form mt-4" id="detalle-exito" hidden></div>
        </div>

        <!-- Detallelibro.js rellena cada elemento con id con los datos de GET /api/libros?id= -->
        <div id="contenido-libro" hidden>
            <section class="ficha-libro">
                <div class="container fila-ficha">

                    <div class="columna-portada-ficha">
                        <img id="libro-portada" class="portada-ficha" src="" alt="Portada del libro"
                             onerror="portadaRota(this)">
                    </div>

                    <div class="columna-info-ficha">
                        <p class="categoria-ficha" id="libro-categoria"></p>
                        <h1 class="titulo-ficha" id="libro-titulo"></h1>
                        <p class="autor-ficha" id="libro-autores"></p>

                        <div class="datos-libro">
                            <div class="fila-dato-libro">
                                <span class="etiqueta-dato">Editorial</span>
                                <span class="valor-dato" id="libro-editorial"></span>
                            </div>
                            <div class="fila-dato-libro">
                                <span class="etiqueta-dato">Año</span>
                                <span class="valor-dato" id="libro-anio"></span>
                            </div>
                            <div class="fila-dato-libro">
                                <span class="etiqueta-dato">ISBN</span>
                                <span class="valor-dato" id="libro-isbn"></span>
                            </div>
                        </div>

                        <div class="bloque-acciones-libro">
                            <span class="sello-disponibilidad" id="libro-disponibilidad"></span>

                            <a class="btn btn-secondary boton-iniciar-sesion-reservar" data-rol="visitante" hidden
                               href="${pageContext.request.contextPath}/FrontEnd/Auth/Login.jsp">
                                Iniciar sesión para reservar
                            </a>

                            <button type="button" id="boton-reservar" class="btn btn-primary boton-reservar" data-rol="lector" hidden>
                                Reservar
                            </button>
                            <button type="button" id="boton-favorito" class="btn btn-outline-secondary boton-favoritos" data-rol="lector" hidden>
                                Añadir a favoritos
                            </button>

                            <!-- Llevan a Inventario; Detallelibro.js les pone el id del libro en el href -->
                            <a id="enlace-gestionar-copias" class="btn btn-outline-secondary boton-gestionar-ejemplares"
                               data-rol="bibliotecario,administrador" hidden href="#">
                                Gestionar copias
                            </a>

                            <a id="enlace-editar-libro" class="btn btn-outline-secondary boton-editar-libro"
                               data-rol="administrador" hidden href="#">
                                Editar libro
                            </a>
                        </div>
                    </div>
                </div>
            </section>

            <div class="container">

                <section class="seccion-descripcion-libro">
                    <h2 class="titulo-seccion-ficha">Descripción</h2>
                    <p class="texto-descripcion" id="libro-descripcion"></p>
                </section>

                <section class="seccion-ejemplar-ficha">
                    <h2 class="titulo-seccion-ficha">Información del ejemplar</h2>

                    <div class="datos-libro">
                        <div class="fila-dato-libro">
                            <span class="etiqueta-dato">Ejemplares totales</span>
                            <span class="valor-dato" id="libro-stock-total"></span>
                        </div>
                        <div class="fila-dato-libro">
                            <span class="etiqueta-dato">Disponibles</span>
                            <span class="valor-dato" id="libro-stock-disponible"></span>
                        </div>
                        <div class="fila-dato-libro">
                            <span class="etiqueta-dato">Prestados o apartados</span>
                            <span class="valor-dato" id="libro-stock-prestados"></span>
                        </div>
                        <div class="fila-dato-libro">
                            <span class="etiqueta-dato">Ubicación</span>
                            <span class="valor-dato" id="libro-ubicacion"></span>
                        </div>
                        <div class="fila-dato-libro">
                            <span class="etiqueta-dato">Género</span>
                            <span class="valor-dato" id="libro-genero"></span>
                        </div>
                    </div>
                </section>
            </div>
        </div>

    </main>

    <script>
        const contextPath = "${pageContext.request.contextPath}";
    </script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Api.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Auth.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Utilidades.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Header.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Detallelibro.js"></script>
</body>
</html>
