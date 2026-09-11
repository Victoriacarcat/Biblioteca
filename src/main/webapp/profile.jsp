<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mi Cuenta - WiseBooks</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container form-page">
        <div class="back-link">
            <a href="${pageContext.request.contextPath}/index.jsp"><i class="fas fa-arrow-left"></i> Volver al Inicio</a>
        </div>
        
        <div class="form-container">
            <h1><i class="fas fa-user-circle"></i> Mi Cuenta</h1>
            <p class="form-subtitle">Administra tu información personal y credenciales.</p>
            
            <form action="#" method="POST">
                <h3 class="form-section-title"><i class="fas fa-id-card"></i> Información Personal</h3>
                
                <div class="form-group">
                    <label>Nombre Completo</label>
                    <input type="text" name="fullName" value="Gabriel Sánchez" required>
                </div>
                <div class="form-group">
                    <label>Documento</label>
                    <input type="text" name="document" value="1234567890">
                </div>
                <div class="form-group">
                    <label>Correo Electrónico</label>
                    <input type="email" name="email" value="gabriel@wisebooks.com" required>
                </div>
                <div class="form-group">
                    <label>Usuario</label>
                    <input type="text" value="gabriel" readonly>
                </div>
                <div class="form-group">
                    <label>Rol Actual</label>
                    <input type="text" value="ADMINISTRADOR" disabled>
                </div>
                
                <h3 class="form-section-title"><i class="fas fa-key"></i> Cambiar Contraseña</h3>
                
                <div class="form-group">
                    <label>Contraseña Actual</label>
                    <input type="password" name="currentPassword" placeholder="••••••••">
                </div>
                <div class="form-group">
                    <label>Nueva Contraseña</label>
                    <input type="password" name="newPassword" placeholder="••••••••">
                </div>
                <div class="form-group">
                    <label>Confirmar Nueva Contraseña</label>
                    <input type="password" name="confirmPassword" placeholder="••••••••">
                </div>
                
                <div class="form-actions">
                    <button type="submit" class="btn-primary">Guardar Cambios</button>
                    <a href="${pageContext.request.contextPath}/index.jsp" class="btn-outline">Cancelar</a>
                </div>
            </form>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>