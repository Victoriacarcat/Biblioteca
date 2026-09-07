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
    
    <main class="container" style="padding: 2.5rem 0 4rem;">
        <h1 style="font-size: 2.2rem; font-weight: 700; color: #0b1a33; margin-bottom: 0.5rem;">
            <i class="fas fa-shield-alt" style="color: #394eff;"></i> Privacy Policy
        </h1>
        <p style="color: #6a7a92; font-size: 1.1rem; margin-bottom: 2rem;">Last updated: January 2025</p>
        
        <div style="background: #fff; padding: 2rem; border-radius: 16px; border: 1px solid #eef2f6;">
            <h3 style="color: #0b1a33; margin-bottom: 1rem;">1. Information We Collect</h3>
            <p style="color: #4a5a72; line-height: 1.8; margin-bottom: 1.5rem;">
                We collect information you provide directly, such as your name, email address, and username.
            </p>
            
            <h3 style="color: #0b1a33; margin-bottom: 1rem;">2. How We Use Your Information</h3>
            <p style="color: #4a5a72; line-height: 1.8; margin-bottom: 1.5rem;">
                We use your information to provide library services, communicate with you, and improve our platform.
            </p>
            
            <h3 style="color: #0b1a33; margin-bottom: 1rem;">3. Data Security</h3>
            <p style="color: #4a5a72; line-height: 1.8; margin-bottom: 1.5rem;">
                We implement appropriate technical and organizational measures to protect your personal information.
            </p>
            
            <h3 style="color: #0b1a33; margin-bottom: 1rem;">4. Third-Party Services</h3>
            <p style="color: #4a5a72; line-height: 1.8; margin-bottom: 1.5rem;">
                We may use third-party services to help us operate our platform. These services have their own privacy policies.
            </p>
            
            <h3 style="color: #0b1a33; margin-bottom: 1rem;">5. Your Rights</h3>
            <p style="color: #4a5a72; line-height: 1.8; margin-bottom: 1.5rem;">
                You have the right to access, modify, or delete your personal information. Contact us to exercise these rights.
            </p>
            
            <h3 style="color: #0b1a33; margin-bottom: 1rem;">6. Contact</h3>
            <p style="color: #4a5a72; line-height: 1.8;">
                For privacy questions, please contact us at privacy@wisebooks.com
            </p>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>