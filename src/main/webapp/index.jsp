<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WiseBooks - Library System</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <section class="hero">
        <div class="hero-content">
            <span class="badge"><i class="fas fa-sparkles"></i> Digital Library</span>
            <h1>Welcome to <span>WiseBooks</span></h1>
            <p>Discover, explore, and share books. Your favorite digital library.</p>
            <div class="hero-actions">
                <a href="${pageContext.request.contextPath}/books/index.jsp" class="btn-primary">View Books</a>
                <a href="${pageContext.request.contextPath}/discover.jsp" class="btn-outline">Discover</a>
            </div>
            <div class="hero-stats">
                <div class="stat">
                    <span class="number">1,284</span>
                    <span class="label">Books</span>
                </div>
                <div class="stat">
                    <span class="number">45</span>
                    <span class="label">Active Loans</span>
                </div>
                <div class="stat">
                    <span class="number">12</span>
                    <span class="label">Students</span>
                </div>
            </div>
        </div>
        <div class="hero-visual">
            <div class="book-stack">
                <div class="book"><i class="fas fa-book"></i> One Hundred Years of Solitude</div>
                <div class="book"><i class="fas fa-book"></i> The Little Prince</div>
                <div class="book"><i class="fas fa-book"></i> 1984</div>
            </div>
            <div class="ticker">
                <span><i class="fas fa-sync-alt"></i> Updated today</span>
                <span><i class="fas fa-users"></i> 3 active readers</span>
            </div>
        </div>
    </section>

    <div class="categories">
        <span class="pill"><i class="fas fa-tag"></i> All</span>
        <span class="pill"><i class="fas fa-flask"></i> Science</span>
        <span class="pill"><i class="fas fa-history"></i> History</span>
        <span class="pill"><i class="fas fa-code"></i> Technology</span>
        <span class="pill"><i class="fas fa-heart"></i> Romance</span>
    </div>

    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>