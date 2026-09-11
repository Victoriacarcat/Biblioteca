<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Terms & Conditions - WiseBooks</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container module-page">
        <h1><i class="fas fa-file-contract" style="color: #394eff;"></i> Terms & Conditions</h1>
        <p class="module-subtitle">Last updated: January 2025</p>
        
        <div class="form-container">
            <h3>1. Acceptance of Terms</h3>
            <p>By using WiseBooks, you agree to comply with and be bound by these terms and conditions.</p>
            
            <h3>2. Use of Service</h3>
            <p>WiseBooks provides library management services. Users must use the service responsibly and in accordance with applicable laws.</p>
            
            <h3>3. User Accounts</h3>
            <p>Users are responsible for maintaining the confidentiality of their account credentials and for all activities that occur under their account.</p>
            
            <h3>4. Intellectual Property</h3>
            <p>All content on WiseBooks is protected by copyright and intellectual property laws.</p>
            
            <h3>5. Privacy</h3>
            <p>Your privacy is important to us. Please review our Privacy Policy for more information.</p>
            
            <h3>6. Contact</h3>
            <p>For questions regarding these terms, please contact us at support@wisebooks.com</p>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>