<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Employee Panel - WiseBooks</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container" style="padding: 2.5rem 0 4rem;">
        <h1 style="font-size:2.2rem;font-weight:700;color:#0b1a33;">
            <i class="fas fa-user-tie" style="color:#394eff;"></i> Employee Panel
        </h1>
        <p style="color:#6a7a92;font-size:1.1rem;margin-bottom:2rem;">Manage loans and returns.</p>
        
        <div style="display:grid;grid-template-columns:repeat(auto-fit,minmax(200px,1fr));gap:1.5rem;">
            <div style="background:#fff;padding:1.5rem;border-radius:16px;border:1px solid #eef2f6;text-align:center;">
                <i class="fas fa-hand-holding-heart" style="font-size:2rem;color:#394eff;"></i>
                <h3>Loans</h3>
                <p style="color:#6a7a92;">Register loans</p>
            </div>
            <div style="background:#fff;padding:1.5rem;border-radius:16px;border:1px solid #eef2f6;text-align:center;">
                <i class="fas fa-undo-alt" style="font-size:2rem;color:#394eff;"></i>
                <h3>Returns</h3>
                <p style="color:#6a7a92;">Process returns</p>
            </div>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>