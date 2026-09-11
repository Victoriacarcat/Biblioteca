<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestión de Ejemplares - WiseBooks</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container module-page">
        <div class="module-header">
            <div>
                <h1><i class="fas fa-cubes"></i> Gestión de Ejemplares</h1>
                <p class="module-subtitle">Administra los ejemplares físicos y digitales de cada libro.</p>
            </div>
            <div>
                <a href="#" class="btn-primary"><i class="fas fa-plus"></i> Registrar Ejemplar</a>
            </div>
        </div>
        
        <div class="table-responsive">
            <table class="table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Código</th>
                        <th>Libro</th>
                        <th>Tipo</th>
                        <th>Estado</th>
                        <th>Ubicación</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>1</td>
                        <td>EJ-001</td>
                        <td>Cien años de soledad</td>
                        <td>Físico</td>
                        <td><span class="badge-active">Disponible</span></td>
                        <td>Estante A-12</td>
                        <td>
                            <div class="table-actions">
                                <a href="#" class="btn-outline btn-sm btn-warning"><i class="fas fa-edit"></i></a>
                                <a href="#" class="btn-outline btn-sm btn-danger" onclick="return confirm('¿Dar de baja?')"><i class="fas fa-trash-alt"></i></a>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>2</td>
                        <td>EJ-002</td>
                        <td>1984</td>
                        <td>Digital (PDF)</td>
                        <td><span class="badge-active">Disponible</span></td>
                        <td>Servidor Digital</td>
                        <td>
                            <div class="table-actions">
                                <a href="#" class="btn-outline btn-sm btn-warning"><i class="fas fa-edit"></i></a>
                                <a href="#" class="btn-outline btn-sm btn-danger" onclick="return confirm('¿Dar de baja?')"><i class="fas fa-trash-alt"></i></a>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>3</td>
                        <td>EJ-003</td>
                        <td>El principito</td>
                        <td>Físico</td>
                        <td><span class="badge-client">En Reparación</span></td>
                        <td>Taller</td>
                        <td>
                            <div class="table-actions">
                                <a href="#" class="btn-outline btn-sm btn-warning"><i class="fas fa-edit"></i></a>
                                <a href="#" class="btn-outline btn-sm btn-danger" onclick="return confirm('¿Dar de baja?')"><i class="fas fa-trash-alt"></i></a>
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