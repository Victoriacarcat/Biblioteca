<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Libros - WiseBooks</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container module-page">
        <div class="module-header">
            <div>
                <h1><i class="fas fa-book" style="color: #394eff;"></i> Gestión de Libros</h1>
                <p class="module-subtitle">Administra el catálogo completo de la biblioteca.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/books/new.jsp" class="btn-primary">
                    <i class="fas fa-plus"></i> Agregar Libro
                </a>
            </div>
        </div>
        
        <form action="#" method="GET" class="search-box">
            <input type="text" name="search" placeholder="Buscar por título, autor o ISBN...">
            <button type="submit"><i class="fas fa-search"></i> Buscar</button>
        </form>
        
        <div class="table-responsive">
            <table class="table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Portada</th>
                        <th>Título</th>
                        <th>Autor</th>
                        <th>Categoría</th>
                        <th>Año</th>
                        <th>Stock</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>1</td>
                        <td><img src="https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=100" class="table-cover" alt="Portada"></td>
                        <td><strong>Cien años de soledad</strong></td>
                        <td>Gabriel García Márquez</td>
                        <td><span class="badge-admin">Novela</span></td>
                        <td>1967</td>
                        <td><span class="badge-active">5 disponibles</span></td>
                        <td>
                            <div class="table-actions">
                                <a href="${pageContext.request.contextPath}/books/edit.jsp?id=1" class="btn-outline btn-sm btn-warning" title="Editar"><i class="fas fa-edit"></i></a>
                                <a href="${pageContext.request.contextPath}/admin/copies.jsp?bookId=1" class="btn-outline btn-sm" title="Ejemplares"><i class="fas fa-cubes"></i></a>
                                <a href="#" class="btn-outline btn-sm btn-danger" onclick="return confirm('¿Eliminar este libro?')" title="Eliminar"><i class="fas fa-trash-alt"></i></a>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>2</td>
                        <td><img src="https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=100" class="table-cover" alt="Portada"></td>
                        <td><strong>1984</strong></td>
                        <td>George Orwell</td>
                        <td><span class="badge-admin">Ciencia ficción</span></td>
                        <td>1949</td>
                        <td><span class="badge-active">4 disponibles</span></td>
                        <td>
                            <div class="table-actions">
                                <a href="${pageContext.request.contextPath}/books/edit.jsp?id=2" class="btn-outline btn-sm btn-warning" title="Editar"><i class="fas fa-edit"></i></a>
                                <a href="${pageContext.request.contextPath}/admin/copies.jsp?bookId=2" class="btn-outline btn-sm" title="Ejemplares"><i class="fas fa-cubes"></i></a>
                                <a href="#" class="btn-outline btn-sm btn-danger" onclick="return confirm('¿Eliminar este libro?')" title="Eliminar"><i class="fas fa-trash-alt"></i></a>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>3</td>
                        <td><img src="https://images.unsplash.com/photo-1512820790803-83ca734da794?w=100" class="table-cover" alt="Portada"></td>
                        <td><strong>El principito</strong></td>
                        <td>Antoine de Saint-Exupéry</td>
                        <td><span class="badge-admin">Infantil</span></td>
                        <td>1943</td>
                        <td><span class="badge-active">3 disponibles</span></td>
                        <td>
                            <div class="table-actions">
                                <a href="${pageContext.request.contextPath}/books/edit.jsp?id=3" class="btn-outline btn-sm btn-warning" title="Editar"><i class="fas fa-edit"></i></a>
                                <a href="${pageContext.request.contextPath}/admin/copies.jsp?bookId=3" class="btn-outline btn-sm" title="Ejemplares"><i class="fas fa-cubes"></i></a>
                                <a href="#" class="btn-outline btn-sm btn-danger" onclick="return confirm('¿Eliminar este libro?')" title="Eliminar"><i class="fas fa-trash-alt"></i></a>
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