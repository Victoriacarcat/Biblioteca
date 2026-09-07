<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Discover - WiseBooks</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container module-page">
        <h1><i class="fas fa-compass" style="color: #394eff;"></i> Discover</h1>
        <p class="module-subtitle">Find new books and personalized recommendations.</p>
        
        <form action="${pageContext.request.contextPath}/discover" method="GET" class="search-box">
            <input type="text" name="q" placeholder="Search books, authors or categories...">
            <button type="submit"><i class="fas fa-search"></i> Search</button>
        </form>
        
        <div class="empty-state">
            <i class="fas fa-compass"></i>
            <p>Explore and discover amazing books.</p>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>