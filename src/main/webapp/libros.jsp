<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Libros · WiseBooks</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />

    <main class="main">
        <div class="page-header">
            <div>
                <h1 class="page-title">Catálogo de libros</h1>
                <p class="page-subtitle">Administra los títulos registrados en la biblioteca.</p>
            </div>
            <div class="page-actions">
                <button class="btn-primary"><i class="fas fa-plus"></i> Agregar libro</button>
            </div>
        </div>

        <div class="search-bar">
            <i class="fas fa-search"></i>
            <input type="text" placeholder="Buscar por título, autor o ISBN">
        </div>

        <div class="table-wrap">
            <table>
                <thead>
                    <tr>
                        <th style="width: 60px;">Portada</th>
                        <th>Título</th>
                        <th>Autor</th>
                        <th>Categoría</th>
                        <th>Año</th>
                        <th>Disponibilidad</th>
                        <th style="width: 180px; text-align: right;">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td><img src="https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=80" class="cover-thumb" alt=""></td>
                        <td>Cien años de soledad</td>
                        <td class="muted">Gabriel García Márquez</td>
                        <td><span class="badge badge-neutral">Novela</span></td>
                        <td class="muted">1967</td>
                        <td><span class="badge badge-success">4 de 5</span></td>
                        <td>
                            <div class="table-actions">
                                <button class="btn-outline btn-sm">Editar</button>
                                <button class="btn-outline btn-sm btn-danger">Eliminar</button>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td><img src="https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=80" class="cover-thumb" alt=""></td>
                        <td>1984</td>
                        <td class="muted">George Orwell</td>
                        <td><span class="badge badge-neutral">Ciencia ficción</span></td>
                        <td class="muted">1949</td>
                        <td><span class="badge badge-success">2 de 4</span></td>
                        <td>
                            <div class="table-actions">
                                <button class="btn-outline btn-sm">Editar</button>
                                <button class="btn-outline btn-sm btn-danger">Eliminar</button>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td><img src="https://images.unsplash.com/photo-1512820790803-83ca734da794?w=80" class="cover-thumb" alt=""></td>
                        <td>El principito</td>
                        <td class="muted">Antoine de Saint-Exupéry</td>
                        <td><span class="badge badge-neutral">Infantil</span></td>
                        <td class="muted">1943</td>
                        <td><span class="badge badge-danger">0 de 3</span></td>
                        <td>
                            <div class="table-actions">
                                <button class="btn-outline btn-sm">Editar</button>
                                <button class="btn-outline btn-sm btn-danger">Eliminar</button>
                            </div>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </main>

    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>