<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Usuarios · WiseBooks</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />

    <main class="main">
        <div class="page-header">
            <div>
                <h1 class="page-title">Usuarios</h1>
                <p class="page-subtitle">Cuentas registradas y roles del sistema.</p>
            </div>
            <div class="page-actions">
                <button class="btn-primary"><i class="fas fa-plus"></i> Nuevo usuario</button>
            </div>
        </div>

        <div class="search-bar">
            <i class="fas fa-search"></i>
            <input type="text" placeholder="Buscar por nombre, correo o documento">
        </div>

        <div class="table-wrap">
            <table>
                <thead>
                    <tr>
                        <th>Nombre</th>
                        <th>Documento</th>
                        <th>Correo</th>
                        <th>Rol</th>
                        <th>Estado</th>
                        <th style="width: 200px; text-align: right;">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>Victoria Rodríguez</td>
                        <td class="mono">1001</td>
                        <td class="muted">victoria@wisebooks.com</td>
                        <td><span class="badge badge-neutral">Lector</span></td>
                        <td><span class="badge badge-success">Activo</span></td>
                        <td>
                            <div class="table-actions">
                                <button class="btn-outline btn-sm">Editar</button>
                                <button class="btn-outline btn-sm btn-danger">Desactivar</button>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>Samantha López</td>
                        <td class="mono">1002</td>
                        <td class="muted">samantha@wisebooks.com</td>
                        <td><span class="badge badge-warning">Bibliotecario</span></td>
                        <td><span class="badge badge-success">Activo</span></td>
                        <td>
                            <div class="table-actions">
                                <button class="btn-outline btn-sm">Editar</button>
                                <button class="btn-outline btn-sm btn-danger">Desactivar</button>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>Gabriel Sánchez</td>
                        <td class="mono">1003</td>
                        <td class="muted">gabriel@wisebooks.com</td>
                        <td><span class="badge badge-neutral">Administrador</span></td>
                        <td><span class="badge badge-success">Activo</span></td>
                        <td>
                            <div class="table-actions">
                                <button class="btn-outline btn-sm">Editar</button>
                                <button class="btn-outline btn-sm btn-danger">Desactivar</button>
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