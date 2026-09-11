<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi cuenta · WiseBooks</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />

    <main class="main">
        <div class="page-header">
            <div>
                <h1 class="page-title">Mi cuenta</h1>
                <p class="page-subtitle">Información personal y credenciales de acceso.</p>
            </div>
        </div>

        <div class="form-card">
            <div class="form-section">
                <h3 class="form-section-title">Información personal</h3>
                <div class="form-grid">
                    <div class="form-group full">
                        <label class="form-label">Nombre completo</label>
                        <input type="text" class="form-input" value="Gabriel Sánchez">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Documento</label>
                        <input type="text" class="form-input" value="1234567890">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Usuario</label>
                        <input type="text" class="form-input" value="gabriel" readonly>
                    </div>
                    <div class="form-group full">
                        <label class="form-label">Correo electrónico</label>
                        <input type="email" class="form-input" value="gabriel@wisebooks.com">
                    </div>
                    <div class="form-group full">
                        <label class="form-label">Rol</label>
                        <input type="text" class="form-input" value="Administrador" disabled>
                    </div>
                </div>
            </div>

            <div class="form-section">
                <h3 class="form-section-title">Cambiar contraseña</h3>
                <div class="form-grid">
                    <div class="form-group full">
                        <label class="form-label">Contraseña actual</label>
                        <input type="password" class="form-input">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Nueva contraseña</label>
                        <input type="password" class="form-input">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Confirmar contraseña</label>
                        <input type="password" class="form-input">
                    </div>
                </div>
            </div>

            <div class="form-actions">
                <button class="btn-primary">Guardar cambios</button>
                <button class="btn-outline">Cancelar</button>
            </div>
        </div>
    </main>

    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>