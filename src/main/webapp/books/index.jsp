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
    
    <main class="container module-page">
        <div class="module-header">
            <div>
                <h1><i class="fas fa-book" style="color: #394eff;"></i> Book Catalog</h1>
                <p class="module-subtitle">Explore our collection of books.</p>
            </div>
            <div>
                <a href="${pageContext.request.contextPath}/books/new.jsp" class="btn-primary">New Book</a>
            </div>
        </div>
        
        <form action="${pageContext.request.contextPath}/books" method="GET" class="search-box">
            <input type="text" name="search" placeholder="Search by title or author..." value="${param.search}">
            <button type="submit"><i class="fas fa-search"></i> Search</button>
        </form>
        
        <div class="grid">
            <c:forEach var="book" items="${books}">
                <div class="book-card">
                    <div class="book-cover">
                        <c:choose>
                            <c:when test="${not empty book.imageUrl}">
                                <img src="${book.imageUrl}" alt="${book.title}" class="book-cover-img">
                            </c:when>
                            <c:otherwise>
                                <div class="no-image">
                                    <i class="fas fa-book"></i>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="book-title">${book.title}</div>
                    <div class="book-author">${book.author}</div>
                    <div class="book-meta">
                        <span><i class="fas fa-tag"></i> ${book.category}</span>
                        <span><i class="fas fa-calendar-alt"></i> ${book.year}</span>
                    </div>
                    <div class="book-status ${book.stock > 0 ? 'available' : 'unavailable'}">
                        <i class="fas ${book.stock > 0 ? 'fa-check-circle' : 'fa-times-circle'}"></i>
                        ${book.stock > 0 ? 'Available' : 'Unavailable'}
                    </div>
                    <button class="add-circle"><i class="fas fa-plus"></i></button>
                </div>
            </c:forEach>
            
            <c:if test="${empty books}">
                <div class="empty-state empty-state-full">
                    <i class="fas fa-book"></i>
                    <h3>No books found</h3>
                    <p>Try adjusting your search or add a new book.</p>
                </div>
            </c:if>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>