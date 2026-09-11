<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reportes · WiseBooks</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />

    <main class="main">
        <div class="page-header">
            <div>
                <h1 class="page-title">Reportes</h1>
                <p class="page-subtitle">Estadísticas y análisis de la biblioteca.</p>
            </div>
            <div class="page-actions">
                <button class="btn-outline"><i class="fas fa-file-pdf"></i> PDF</button>
                <button class="btn-outline"><i class="fas fa-file-excel"></i> Excel</button>
            </div>
        </div>

        <div class="table-wrap">
            <table>
                <thead>
                    <tr>
                        <th>Reporte</th>
                        <th>Descripción</th>
                        <th style="width: 160px; text-align: right;">Acción</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>Reporte de préstamos</td>
                        <td class="muted">Total, activos y vencidos</td>
                        <td>
                            <div class="table-actions">
                                <button class="btn-primary btn-sm">Generar</button>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>Reporte de devoluciones</td>
                        <td class="muted">Devoluciones por período</td>
                        <td>
                            <div class="table-actions">
                                <button class="btn-primary btn-sm">Generar</button>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>Libros más solicitados</td>
                        <td class="muted">Top 10 más prestados</td>
                        <td>
                            <div class="table-actions">
                                <button class="btn-primary btn-sm">Generar</button>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>Usuarios en mora</td>
                        <td class="muted">Usuarios con devoluciones pendientes</td>
                        <td>
                            <div class="table-actions">
                                <button class="btn-primary btn-sm">Generar</button>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>Libros perdidos</td>
                        <td class="muted">Reporte de ejemplares perdidos</td>
                        <td>
                            <div class="table-actions">
                                <button class="btn-primary btn-sm">Generar</button>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td>Reservas</td>
                        <td class="muted">Estadísticas de reservas</td>
                        <td>
                            <div class="table-actions">
                                <button class="btn-primary btn-sm">Generar</button>
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