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
    <title>Authors - WiseBooks</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container module-page">
        <div class="module-header">
            <div>
                <h1><i class="fas fa-users" style="color: #394eff;"></i> Authors</h1>
                <p class="module-subtitle">Manage authors in the library system.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/authors/new.jsp" class="btn-primary">New Author</a>
            </div>
        </div>
        
        <form action="${pageContext.request.contextPath}/authors" method="GET" class="search-box">
            <input type="text" name="search" placeholder="Search authors by name..." value="${param.search}">
            <button type="submit"><i class="fas fa-search"></i> Search</button>
        </form>
        
        <div class="authors-grid">
            <c:forEach var="author" items="${authors}">
                <div class="author-card">
                    <div class="author-avatar">
                        <i class="fas fa-user"></i>
                    </div>
                    <div class="author-name">${author.name}</div>
                    <div class="author-nationality">${author.nationality}</div>
                    <div class="author-books-count"><i class="fas fa-book"></i> ${author.bookCount} books</div>
                    <div class="author-actions">
                        <a href="${pageContext.request.contextPath}/authors/detail.jsp?id=${author.id}" class="btn-outline btn-sm">View</a>
                        <a href="${pageContext.request.contextPath}/authors/edit.jsp?id=${author.id}" class="btn-outline btn-sm btn-warning">Edit</a>
                        <a href="${pageContext.request.contextPath}/authors?action=delete&id=${author.id}" class="btn-outline btn-sm btn-danger" onclick="return confirm('Delete this author?')">Delete</a>
                    </div>
                </div>
            </c:forEach>
        </div>
        
        <c:if test="${empty authors}">
            <div class="empty-state">
                <i class="fas fa-users"></i>
                <h3>No authors registered</h3>
                <p>Add your first author using the "New Author" button.</p>
            </div>
        </c:if>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>