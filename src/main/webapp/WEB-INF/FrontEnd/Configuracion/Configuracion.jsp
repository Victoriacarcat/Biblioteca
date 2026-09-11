<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Configuración · WiseBooks</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />

    <main class="main">
        <div class="page-header">
            <div>
                <h1 class="page-title">Configuración</h1>
                <p class="page-subtitle">Reglas de negocio y parámetros del sistema.</p>
            </div>
        </div>

        <div class="form-card">
            <div class="form-section">
                <h3 class="form-section-title">Reglas de préstamo</h3>
                <div class="form-grid">
                    <div class="form-group">
                        <label class="form-label">Días máximos de préstamo</label>
                        <input type="number" class="form-input" value="15">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Máximo de libros por usuario</label>
                        <input type="number" class="form-input" value="3">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Días de tolerancia</label>
                        <input type="number" class="form-input" value="2">
                    </div>
                </div>
            </div>

            <div class="form-section">
                <h3 class="form-section-title">Multas</h3>
                <div class="form-grid">
                    <div class="form-group">
                        <label class="form-label">Multa por día de retraso</label>
                        <input type="number" class="form-input" value="1000">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Máximo acumulable</label>
                        <input type="number" class="form-input" value="20000">
                    </div>
                </div>
            </div>

            <div class="form-section">
                <h3 class="form-section-title">Días festivos y cierres</h3>
                <div class="form-grid">
                    <div class="form-group full">
                        <label class="form-label">Días festivos</label>
                        <input type="text" class="form-input" value="2025-01-01, 2025-12-25">
                        <span class="form-hint">Separados por coma</span>
                    </div>
                </div>
            </div>

            <div class="form-actions">
                <button class="btn-primary">Guardar configuración</button>
                <button class="btn-outline">Cancelar</button>
            </div>
        </div>
    </main>

    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>