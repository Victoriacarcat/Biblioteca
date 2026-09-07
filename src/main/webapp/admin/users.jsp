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
    <title>Manage Users - WiseBooks</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container module-page">
        <div class="module-header">
            <div>
                <h1><i class="fas fa-users" style="color: #394eff;"></i> Manage Users</h1>
                <p class="module-subtitle">View and manage system users.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/admin/index.jsp" class="btn-outline">
                    <i class="fas fa-arrow-left"></i> Back to Dashboard
                </a>
            </div>
        </div>
        
        <div class="table-responsive">
            <table class="table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>1</td>
                        <td>admin</td>
                        <td>admin@wisebooks.com</td>
                        <td><span class="badge-admin">ADMIN</span></td>
                        <td><span class="badge-active">Active</span></td>
                        <td>
                            <a href="#" class="btn-outline btn-sm">Edit</a>
                            <a href="#" class="btn-outline btn-sm btn-danger">Delete</a>
                        </td>
                    </tr>
                    <tr>
                        <td>2</td>
                        <td>employee</td>
                        <td>employee@wisebooks.com</td>
                        <td><span class="badge-employee">EMPLOYEE</span></td>
                        <td><span class="badge-active">Active</span></td>
                        <td>
                            <a href="#" class="btn-outline btn-sm">Edit</a>
                            <a href="#" class="btn-outline btn-sm btn-danger">Delete</a>
                        </td>
                    </tr>
                    <tr>
                        <td>3</td>
                        <td>client</td>
                        <td>client@wisebooks.com</td>
                        <td><span class="badge-client">CLIENT</span></td>
                        <td><span class="badge-active">Active</span></td>
                        <td>
                            <a href="#" class="btn-outline btn-sm">Edit</a>
                            <a href="#" class="btn-outline btn-sm btn-danger">Delete</a>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>