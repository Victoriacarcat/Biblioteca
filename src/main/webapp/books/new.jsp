<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>New Book - WiseBooks</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container" style="padding: 2.5rem 0 4rem; max-width: 600px;">
        <h1 style="font-size:2.2rem;font-weight:700;color:#0b1a33;margin-bottom:0.5rem;">
            <i class="fas fa-plus-circle" style="color:#394eff;"></i> New Book
        </h1>
        <p style="color:#6a7a92;font-size:1.1rem;margin-bottom:2rem;">Add a new book to the catalog.</p>
        
        <form action="${pageContext.request.contextPath}/books" method="POST" style="background:#fff;padding:2rem;border-radius:16px;border:1px solid #eef2f6;">
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
            <button type="submit" class="btn-primary" style="padding:0.8rem 2rem;width:100%;justify-content:center;">
                <i class="fas fa-save"></i> Save Book
            </button>
        </form>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>