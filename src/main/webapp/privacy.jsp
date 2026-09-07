<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Privacy Policy - WiseBooks</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container module-page">
        <h1><i class="fas fa-shield-alt" style="color: #394eff;"></i> Privacy Policy</h1>
        <p class="module-subtitle">Last updated: January 2025</p>
        
        <div class="form-container">
            <h3>1. Information We Collect</h3>
            <p>We collect information you provide directly, such as your name, email address, and username.</p>
            
            <h3>2. How We Use Your Information</h3>
            <p>We use your information to provide library services, communicate with you, and improve our platform.</p>
            
            <h3>3. Data Security</h3>
            <p>We implement appropriate technical and organizational measures to protect your personal information.</p>
            
            <h3>4. Third-Party Services</h3>
            <p>We may use third-party services to help us operate our platform. These services have their own privacy policies.</p>
            
            <h3>5. Your Rights</h3>
            <p>You have the right to access, modify, or delete your personal information. Contact us to exercise these rights.</p>
            
            <h3>6. Contact</h3>
            <p>For privacy questions, please contact us at privacy@wisebooks.com</p>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>