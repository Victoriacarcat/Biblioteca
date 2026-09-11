<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inventario · WiseBooks</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />

    <main class="main">
        <div class="page-header">
            <div>
                <h1 class="page-title">Inventario</h1>
                <p class="page-subtitle">Estado general de la colección de la biblioteca.</p>
            </div>
        </div>

        <div class="stats">
            <div class="stat-card">
                <div class="stat-label">Total ejemplares</div>
                <div class="stat-value">284</div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Disponibles</div>
                <div class="stat-value">210</div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Prestados</div>
                <div class="stat-value">45</div>
            </div>
            <div class="stat-card">
                <div class="stat-label">Dañados / Reparación</div>
                <div class="stat-value">29</div>
            </div>
        </div>

        <div class="search-bar">
            <i class="fas fa-search"></i>
            <input type="text" placeholder="Buscar por código, título o ubicación">
        </div>

        <div class="table-wrap">
            <table>
                <thead>
                    <tr>
                        <th>Código</th>
                        <th>Título</th>
                        <th>Estado</th>
                        <th>Ubicación</th>
                        <th>Última revisión</th>
                        <th style="width: 120px; text-align: right;">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td class="mono">EJ-001</td>
                        <td>Cien años de soledad</td>
                        <td><span class="badge badge-success">Disponible</span></td>
                        <td class="muted">Estante A-12</td>
                        <td class="muted">2025-01-15</td>
                        <td>
                            <div class="table-actions">
                                <button class="btn-outline btn-sm">Ver</button>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td class="mono">EJ-002</td>
                        <td>1984</td>
                        <td><span class="badge badge-warning">Prestado</span></td>
                        <td class="muted">Estante B-03</td>
                        <td class="muted">2025-01-20</td>
                        <td>
                            <div class="table-actions">
                                <button class="btn-outline btn-sm">Ver</button>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td class="mono">EJ-003</td>
                        <td>El principito</td>
                        <td><span class="badge badge-danger">En reparación</span></td>
                        <td class="muted">Taller</td>
                        <td class="muted">2025-02-01</td>
                        <td>
                            <div class="table-actions">
                                <button class="btn-outline btn-sm">Ver</button>
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