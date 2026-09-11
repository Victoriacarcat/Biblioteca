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
    <title>New Author - WiseBooks</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container form-page">
        <div class="back-link">
            <a href="${pageContext.request.contextPath}/authors/index.jsp"><i class="fas fa-arrow-left"></i> Back to Authors</a>
        </div>
        
        <div class="form-container">
            <h1><i class="fas fa-user-plus" style="color: #394eff;"></i> New Author</h1>
            <p class="form-subtitle">Add a new author to the library system.</p>
            
            <form action="${pageContext.request.contextPath}/authors" method="POST">
                <div class="form-group">
                    <label>Full Name *</label>
                    <input type="text" name="name" required>
                </div>
                <div class="form-group">
                    <label>Nationality</label>
                    <input type="text" name="nationality">
                </div>
                <div class="form-group">
                    <label>Birth Year</label>
                    <input type="number" name="birthYear">
                </div>
                <div class="form-group">
                    <label>Biography</label>
                    <textarea name="biography" rows="4"></textarea>
                </div>
                <div class="form-actions">
                    <button type="submit" class="btn-primary">Save Author</button>
                    <a href="${pageContext.request.contextPath}/authors/index.jsp" class="btn-outline">Cancel</a>
                </div>
            </form>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>