<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Categorías y Secciones - WiseBooks</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container module-page">
        <div class="module-header">
            <div>
                <h1><i class="fas fa-tags"></i> Categorías y Secciones</h1>
                <p class="module-subtitle">Organiza el catálogo temáticamente y por ubicación física.</p>
            </div>
            <div>
                <a href="#" class="btn-primary"><i class="fas fa-plus"></i> Nueva Categoría</a>
            </div>
        </div>
        
        <h2 class="section-title"><i class="fas fa-tag"></i> Categorías</h2>
        
        <div class="admin-grid">
            <div class="admin-card">
                <i class="fas fa-book"></i>
                <h3>Novela</h3>
                <p>42 libros</p>
                <div class="table-actions">
                    <a href="#" class="btn-outline btn-sm btn-warning"><i class="fas fa-edit"></i></a>
                    <a href="#" class="btn-outline btn-sm btn-danger" onclick="return confirm('¿Eliminar?')"><i class="fas fa-trash-alt"></i></a>
                </div>
            </div>
            <div class="admin-card">
                <i class="fas fa-rocket"></i>
                <h3>Ciencia Ficción</h3>
                <p>28 libros</p>
                <div class="table-actions">
                    <a href="#" class="btn-outline btn-sm btn-warning"><i class="fas fa-edit"></i></a>
                    <a href="#" class="btn-outline btn-sm btn-danger" onclick="return confirm('¿Eliminar?')"><i class="fas fa-trash-alt"></i></a>
                </div>
            </div>
            <div class="admin-card">
                <i class="fas fa-dragon"></i>
                <h3>Fantasía</h3>
                <p>35 libros</p>
                <div class="table-actions">
                    <a href="#" class="btn-outline btn-sm btn-warning"><i class="fas fa-edit"></i></a>
                    <a href="#" class="btn-outline btn-sm btn-danger" onclick="return confirm('¿Eliminar?')"><i class="fas fa-trash-alt"></i></a>
                </div>
            </div>
            <div class="admin-card">
                <i class="fas fa-child"></i>
                <h3>Infantil</h3>
                <p>51 libros</p>
                <div class="table-actions">
                    <a href="#" class="btn-outline btn-sm btn-warning"><i class="fas fa-edit"></i></a>
                    <a href="#" class="btn-outline btn-sm btn-danger" onclick="return confirm('¿Eliminar?')"><i class="fas fa-trash-alt"></i></a>
                </div>
            </div>
        </div>
        
        <h2 class="section-title"><i class="fas fa-map-marker-alt"></i> Ubicaciones Físicas</h2>
        
        <div class="table-responsive">
            <table class="table">
                <thead>
                    <tr>
                        <th>Sección</th>
                        <th>Estante</th>
                        <th>Categoría</th>
                        <th>Ejemplares</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>Sección A</td>
                        <td>A-01 a A-15</td>
                        <td>Novela</td>
                        <td>42</td>
                        <td>
                            <div class="table-actions">
                                <a href="#" class="btn-outline btn-sm btn-warning"><i class="fas fa-edit"></i></a>
                                <a href="#" class="btn-outline btn-sm btn-danger"><i class="fas fa-trash-alt"></i></a>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>Sección B</td>
                        <td>B-01 a B-10</td>
                        <td>Ciencia Ficción</td>
                        <td>28</td>
                        <td>
                            <div class="table-actions">
                                <a href="#" class="btn-outline btn-sm btn-warning"><i class="fas fa-edit"></i></a>
                                <a href="#" class="btn-outline btn-sm btn-danger"><i class="fas fa-trash-alt"></i></a>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>Sección C</td>
                        <td>C-01 a C-12</td>
                        <td>Fantasía</td>
                        <td>35</td>
                        <td>
                            <div class="table-actions">
                                <a href="#" class="btn-outline btn-sm btn-warning"><i class="fas fa-edit"></i></a>
                                <a href="#" class="btn-outline btn-sm btn-danger"><i class="fas fa-trash-alt"></i></a>
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