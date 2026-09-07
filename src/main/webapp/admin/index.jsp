<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String role = (String) session.getAttribute("role");
    if (!"ADMIN".equals(role)) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Panel - WiseBooks</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container module-page">
        <div class="module-header">
            <div>
                <h1><i class="fas fa-shield-alt" style="color: #394eff;"></i> Admin Panel</h1>
                <p class="module-subtitle">Complete system management.</p>
            </div>
            <div class="role-badge">ADMIN</div>
        </div>
        
        <div class="admin-grid">
            <a href="${pageContext.request.contextPath}/admin/users.jsp" class="admin-card">
                <i class="fas fa-users"></i>
                <h3>Users</h3>
                <p>Manage accounts</p>
            </a>
            <a href="${pageContext.request.contextPath}/admin/reports.jsp" class="admin-card">
                <i class="fas fa-chart-bar"></i>
                <h3>Reports</h3>
                <p>Statistics</p>
            </a>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>