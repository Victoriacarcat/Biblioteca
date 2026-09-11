<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String role = (String) session.getAttribute("role");
    if (!"EMPLOYEE".equals(role)) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Employee Panel - WiseBooks</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container module-page">
        <div class="module-header">
            <div>
                <h1><i class="fas fa-user-tie" style="color: #394eff;"></i> Employee Panel</h1>
                <p class="module-subtitle">Manage loans and returns.</p>
            </div>
            <div class="role-badge" style="background:#fef3e2;color:#f39c12;">EMPLOYEE</div>
        </div>
        
        <div class="employee-grid">
            <a href="#" class="employee-card">
                <i class="fas fa-hand-holding-heart"></i>
                <h3>Loans</h3>
                <p>Register loans</p>
            </a>
            <a href="#" class="employee-card">
                <i class="fas fa-undo-alt"></i>
                <h3>Returns</h3>
                <p>Process returns</p>
            </a>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>