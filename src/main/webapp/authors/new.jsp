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
    
    <main class="container" style="padding: 2.5rem 0 4rem; max-width: 600px;">
        <div style="margin-bottom:1.5rem;">
            <a href="${pageContext.request.contextPath}/books/index.jsp" style="color:#394eff;font-weight:500;text-decoration:none;">
                <i class="fas fa-arrow-left"></i> Back to Catalog
            </a>
        </div>
        
        <div style="background:#fff;border-radius:20px;padding:2.5rem;border:1px solid #eef2f6;">
            <h1 style="font-size:2rem;font-weight:700;color:#0b1a33;margin-bottom:0.5rem;">
                <i class="fas fa-plus-circle" style="color:#394eff;"></i> New Book
            </h1>
            <p style="color:#6a7a92;margin-bottom:2rem;">Add a new book to the catalog.</p>
            
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
                    </select>
                </div>
                <div style="display:flex;gap:1rem;margin-top:1.5rem;">
                    <button type="submit" class="btn-primary" style="padding:0.8rem 2rem;border:none;cursor:pointer;">
                        <i class="fas fa-save"></i> Save Book
                    </button>
                    <a href="${pageContext.request.contextPath}/books/index.jsp" class="btn-outline" style="padding:0.8rem 2rem;text-decoration:none;text-align:center;">
                        <i class="fas fa-times"></i> Cancel
                    </a>
                </div>
            </form>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>