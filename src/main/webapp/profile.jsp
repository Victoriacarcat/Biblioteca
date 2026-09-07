<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Profile - WiseBooks</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container form-page">
        <div class="form-container">
            <h1><i class="fas fa-user" style="color: #394eff;"></i> My Profile</h1>
            <p class="form-subtitle">Manage your account information.</p>
            
            <form action="${pageContext.request.contextPath}/profile" method="POST">
                <div class="form-group">
                    <label>Full Name</label>
                    <input type="text" name="fullName" value="${sessionScope.user.fullName}" required>
                </div>
                <div class="form-group">
                    <label>Email</label>
                    <input type="email" name="email" value="${sessionScope.user.email}" required>
                </div>
                <div class="form-group">
                    <label>Username</label>
                    <input type="text" name="username" value="${sessionScope.user.username}" required readonly>
                </div>
                <div class="form-group">
                    <label>Role</label>
                    <input type="text" value="${sessionScope.user.role.name}" disabled style="background:#f8fafc;">
                </div>
                <div class="form-actions">
                    <button type="submit" class="btn-primary">Update Profile</button>
                    <a href="${pageContext.request.contextPath}/index.jsp" class="btn-outline">Cancel</a>
                </div>
            </form>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>