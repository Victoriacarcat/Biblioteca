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

        <section class="ficha-libro">
            <div class="container fila-ficha">

                <div class="columna-portada-ficha">
                    <img
                        class="portada-ficha"
                        src="https://via.placeholder.com/300x440?text=Portada"
                        alt="Portada del libro">
                </div>

                <div class="columna-info-ficha">
                    <p class="categoria-ficha">Categoría del libro</p>
                    <h1 class="titulo-ficha">Título del libro</h1>
                    <p class="autor-ficha">Nombre del autor</p>

                    <div class="datos-libro">
                        <div class="fila-dato-libro">
                            <span class="etiqueta-dato">Editorial</span>
                            <span class="valor-dato">Nombre de la editorial</span>
                        </div>
                        <div class="fila-dato-libro">
                            <span class="etiqueta-dato">Año</span>
                            <span class="valor-dato">0000</span>
                        </div>
                        <div class="fila-dato-libro">
                            <span class="etiqueta-dato">ISBN</span>
                            <span class="valor-dato">000-0000000000</span>
                        </div>
                    </div>

                    <div class="bloque-acciones-libro">
                        <span class="sello-disponibilidad disponible">Disponible</span>

                        <button class="btn btn-secondary boton-iniciar-sesion-reservar" data-rol="visitante" hidden>
                            Iniciar sesión para reservar
                        </button>

                        <button class="btn btn-primary boton-reservar" data-rol="lector" hidden>
                            Reservar
                        </button>
                        <button class="btn btn-outline-secondary boton-favoritos" data-rol="lector" hidden>
                            Añadir a favoritos
                        </button>

                        <button class="btn btn-outline-secondary boton-gestionar-ejemplares" data-rol="bibliotecario,administrador" hidden>
                            Gestionar ejemplares
                        </button>

                        <button class="btn btn-outline-secondary boton-editar-libro" data-rol="administrador" hidden>
                            Editar libro
                        </button>
                    </div>
                </div>
            </div>
        </section>

        <div class="container">

            <section class="seccion-descripcion-libro">
                <h2 class="titulo-seccion-ficha">Descripción</h2>
                <p class="texto-descripcion">
                    Sinopsis / descripción del libro...
                </p>
            </section>

            <section class="seccion-ejemplar-ficha">
                <h2 class="titulo-seccion-ficha">Información del ejemplar</h2>

                <div class="datos-libro">
                    <div class="fila-dato-libro">
                        <span class="etiqueta-dato">Ejemplares totales</span>
                        <span class="valor-dato">0</span>
                    </div>
                    <div class="fila-dato-libro">
                        <span class="etiqueta-dato">Disponibles</span>
                        <span class="valor-dato">0</span>
                    </div>
                    <div class="fila-dato-libro">
                        <span class="etiqueta-dato">Prestados</span>
                        <span class="valor-dato">0</span>
                    </div>
                    <div class="fila-dato-libro">
                        <span class="etiqueta-dato">Ubicación</span>
                        <span class="valor-dato">Ubicación del ejemplar</span>
                    </div>
                    <div class="fila-dato-libro">
                        <span class="etiqueta-dato">Estado</span>
                        <span class="valor-dato">Estado del ejemplar</span>
                    </div>
                </div>
            </section>
        </div>

    </main>

    <script>
        const contextPath = "${pageContext.request.contextPath}";
    </script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Auth.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Header.js"></script>
    <script src="${pageContext.request.contextPath}/Recursos/JS/Detallelibro.js"></script>
</body>
</html>