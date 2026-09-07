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
    
    <main class="container" style="padding: 2.5rem 0 4rem;">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:2rem;flex-wrap:wrap;">
            <div>
                <h1 style="font-size:2.2rem;font-weight:700;color:#0b1a33;">
                    <i class="fas fa-users" style="color:#394eff;"></i> Authors
                </h1>
                <p style="color:#6a7a92;font-size:1.1rem;">Manage authors in the library system.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/authors/new.jsp" class="btn-primary" style="padding:0.7rem 2rem;text-decoration:none;display:inline-flex;align-items:center;gap:0.5rem;">
                    <i class="fas fa-plus"></i> New Author
                </a>
            </div>
        </div>
        
        <form action="${pageContext.request.contextPath}/authors" method="GET" class="search-box" style="margin-bottom:2rem;">
            <input type="text" name="search" placeholder="Search authors by name..." value="${param.search}" style="flex:1;border:none;padding:0.9rem 0;font-size:1rem;outline:none;background:transparent;">
            <button type="submit"><i class="fas fa-search"></i> Search</button>
        </form>
        
        <div style="display:grid;grid-template-columns:repeat(auto-fill,minmax(250px,1fr));gap:2rem;">
            <c:forEach var="i" begin="1" end="6">
                <div class="author-card">
                    <div class="author-avatar">
                        <i class="fas fa-user"></i>
                    </div>
                    <div class="author-name">Author ${i}</div>
                    <div class="author-nationality">Nationality</div>
                    <div class="author-books-count"><i class="fas fa-book"></i> ${i * 3} books</div>
                    <div style="margin-top:1rem;display:flex;gap:0.6rem;justify-content:center;">
                        <a href="${pageContext.request.contextPath}/authors/detail.jsp?id=${i}" class="btn-outline" style="padding:0.4rem 1.5rem;font-size:0.85rem;text-decoration:none;">View</a>
                        <a href="${pageContext.request.contextPath}/authors/edit.jsp?id=${i}" class="btn-outline" style="padding:0.4rem 1.5rem;font-size:0.85rem;border-color:#f39c12;color:#f39c12;text-decoration:none;">Edit</a>
                        <a href="#" class="btn-outline" style="padding:0.4rem 1.5rem;font-size:0.85rem;border-color:#e74c3c;color:#e74c3c;text-decoration:none;" onclick="return confirm('Delete this author?')">Delete</a>
                    </div>
                </div>
            </c:forEach>
        </div>
        
        <c:if test="${empty authors}">
            <div style="text-align:center;padding:4rem 0;color:#6a7a92;">
                <i class="fas fa-users" style="font-size:4rem;color:#eef3ff;display:block;margin-bottom:1rem;"></i>
                <h3 style="color:#0b1a33;margin-bottom:0.5rem;">No authors registered</h3>
                <p>Add your first author using the "New Author" button.</p>
            </div>
        </c:if>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>