<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Panel - WiseBooks</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container" style="padding: 2.5rem 0 4rem;">
        <div style="display:flex;justify-content:space-between;align-items:center;margin-bottom:2rem;">
            <div>
                <h1 style="font-size:2.2rem;font-weight:700;color:#0b1a33;">
                    <i class="fas fa-shield-alt" style="color:#394eff;"></i> Admin Panel
                </h1>
                <p style="color:#6a7a92;font-size:1.1rem;">Complete system management.</p>
            </div>
            <div style="background:#eef3ff;padding:0.5rem 1.2rem;border-radius:40px;">
                <span style="color:#394eff;font-weight:600;"><i class="fas fa-shield-alt"></i> ADMIN</span>
            </div>
        </div>
        
        <div style="display:grid;grid-template-columns:repeat(auto-fit,minmax(200px,1fr));gap:1.5rem;">
            <div style="background:#fff;padding:1.5rem;border-radius:16px;border:1px solid #eef2f6;text-align:center;">
                <i class="fas fa-users" style="font-size:2rem;color:#394eff;"></i>
                <h3>Users</h3>
                <p style="color:#6a7a92;">Manage accounts</p>
            </div>
            <div style="background:#fff;padding:1.5rem;border-radius:16px;border:1px solid #eef2f6;text-align:center;">
                <i class="fas fa-book" style="font-size:2rem;color:#394eff;"></i>
                <h3>Books</h3>
                <p style="color:#6a7a92;">Manage catalog</p>
            </div>
            <div style="background:#fff;padding:1.5rem;border-radius:16px;border:1px solid #eef2f6;text-align:center;">
                <i class="fas fa-chart-bar" style="font-size:2rem;color:#394eff;"></i>
                <h3>Reports</h3>
                <p style="color:#6a7a92;">Statistics</p>
            </div>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>