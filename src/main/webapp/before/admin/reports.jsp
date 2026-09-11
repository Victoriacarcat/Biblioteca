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
    <title>Reports - WiseBooks</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container module-page">
        <div class="module-header">
            <div>
                <h1><i class="fas fa-chart-bar" style="color: #394eff;"></i> Reports</h1>
                <p class="module-subtitle">View library statistics and reports.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/admin/index.jsp" class="btn-outline">
                    <i class="fas fa-arrow-left"></i> Back to Dashboard
                </a>
            </div>
        </div>
        
        <div class="empty-state">
            <i class="fas fa-chart-bar"></i>
            <p>Reports coming soon.</p>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>