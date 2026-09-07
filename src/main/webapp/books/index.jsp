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
    <title>Book Catalog - WiseBooks</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container" style="padding: 2.5rem 0 4rem;">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:2rem;flex-wrap:wrap;">
            <div>
                <h1 style="font-size:2.2rem;font-weight:700;color:#0b1a33;">
                    <i class="fas fa-book" style="color:#394eff;"></i> Book Catalog
                </h1>
                <p style="color:#6a7a92;font-size:1.1rem;">Explore our collection of books.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/books/new.jsp" class="btn-primary" style="padding:0.7rem 2rem;text-decoration:none;display:inline-flex;align-items:center;gap:0.5rem;">
                    <i class="fas fa-plus"></i> New Book
                </a>
            </div>
        </div>
        
        <div class="grid" style="margin-top:1.5rem;">
            <c:forEach var="i" begin="1" end="8">
                <div class="book-card">
                    <div class="book-cover"><i class="fas fa-book" style="font-size:3rem;"></i></div>
                    <div class="book-title">Book ${i}</div>
                    <div class="book-author">Author ${i}</div>
                    <div class="book-meta">
                        <span><i class="fas fa-tag"></i> Fiction</span>
                        <span><i class="fas fa-calendar-alt"></i> 2024</span>
                    </div>
                    <div class="book-status">Available</div>
                    <button class="add-circle"><i class="fas fa-plus"></i></button>
                </div>
            </c:forEach>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>