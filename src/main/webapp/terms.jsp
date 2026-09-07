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
    
    <main class="container" style="padding: 2.5rem 0 4rem;">
        <h1 style="font-size: 2.2rem; font-weight: 700; color: #0b1a33; margin-bottom: 0.5rem;">
            <i class="fas fa-file-contract" style="color: #394eff;"></i> Terms & Conditions
        </h1>
        <p style="color: #6a7a92; font-size: 1.1rem; margin-bottom: 2rem;">Last updated: January 2025</p>
        
        <div style="background: #fff; padding: 2rem; border-radius: 16px; border: 1px solid #eef2f6;">
            <h3 style="color: #0b1a33; margin-bottom: 1rem;">1. Acceptance of Terms</h3>
            <p style="color: #4a5a72; line-height: 1.8; margin-bottom: 1.5rem;">
                By using WiseBooks, you agree to comply with and be bound by these terms and conditions.
            </p>
            
            <h3 style="color: #0b1a33; margin-bottom: 1rem;">2. Use of Service</h3>
            <p style="color: #4a5a72; line-height: 1.8; margin-bottom: 1.5rem;">
                WiseBooks provides library management services. Users must use the service responsibly and in accordance with applicable laws.
            </p>
            
            <h3 style="color: #0b1a33; margin-bottom: 1rem;">3. User Accounts</h3>
            <p style="color: #4a5a72; line-height: 1.8; margin-bottom: 1.5rem;">
                Users are responsible for maintaining the confidentiality of their account credentials and for all activities that occur under their account.
            </p>
            
            <h3 style="color: #0b1a33; margin-bottom: 1rem;">4. Intellectual Property</h3>
            <p style="color: #4a5a72; line-height: 1.8; margin-bottom: 1.5rem;">
                All content on WiseBooks is protected by copyright and intellectual property laws.
            </p>
            
            <h3 style="color: #0b1a33; margin-bottom: 1rem;">5. Privacy</h3>
            <p style="color: #4a5a72; line-height: 1.8; margin-bottom: 1.5rem;">
                Your privacy is important to us. Please review our Privacy Policy for more information.
            </p>
            
            <h3 style="color: #0b1a33; margin-bottom: 1rem;">6. Contact</h3>
            <p style="color: #4a5a72; line-height: 1.8;">
                For questions regarding these terms, please contact us at support@wisebooks.com
            </p>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>