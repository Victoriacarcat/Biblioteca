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
    <title>New Book - WiseBooks</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container form-page">
        <div class="back-link">
            <a href="${pageContext.request.contextPath}/books/index.jsp"><i class="fas fa-arrow-left"></i> Back to Catalog</a>
        </div>
        
        <div class="form-container">
            <h1><i class="fas fa-plus-circle" style="color: #394eff;"></i> New Book</h1>
            <p class="form-subtitle">Add a new book to the catalog.</p>
            
            <form action="${pageContext.request.contextPath}/books" method="POST">
                <div class="form-group">
                    <label>Title *</label>
                    <input type="text" name="title" required>
                </div>
                <div class="form-group">
                    <label>Author *</label>
                    <input type="text" name="author" required>
                </div>
                <div class="form-group">
                    <label>ISBN</label>
                    <input type="text" name="isbn">
                </div>
                <div class="form-group">
                    <label>Year</label>
                    <input type="number" name="year">
                </div>
                <div class="form-group">
                    <label>Category</label>
                    <select name="category">
                        <option value="Fiction">Fiction</option>
                        <option value="Non-Fiction">Non-Fiction</option>
                        <option value="Science">Science</option>
                        <option value="History">History</option>
                        <option value="Technology">Technology</option>
                        <option value="Fantasy">Fantasy</option>
                        <option value="Novel">Novel</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Stock</label>
                    <input type="number" name="stock" value="1" min="0">
                </div>
                <div class="form-group">
                    <label>Image URL</label>
                    <input type="url" name="imageUrl" placeholder="https://example.com/book-cover.jpg">
                </div>
                <div class="form-actions">
                    <button type="submit" class="btn-primary">Save Book</button>
                    <a href="${pageContext.request.contextPath}/books/index.jsp" class="btn-outline">Cancel</a>
                </div>
            </form>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>