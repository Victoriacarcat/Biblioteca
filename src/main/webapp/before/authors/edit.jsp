<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Author - WiseBooks</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container" style="padding: 2.5rem 0 4rem; max-width: 600px;">
        <div style="margin-bottom:1.5rem;">
            <a href="${pageContext.request.contextPath}/authors/index.jsp" style="color:#394eff;font-weight:500;">
                <i class="fas fa-arrow-left"></i> Back to Authors
            </a>
        </div>
        
        <div style="background:#fff;border-radius:20px;padding:2.5rem;border:1px solid #eef2f6;">
            <h1 style="font-size:2rem;font-weight:700;color:#0b1a33;margin-bottom:0.5rem;">
                <i class="fas fa-user-edit" style="color:#394eff;"></i> Edit Author
            </h1>
            <p style="color:#6a7a92;margin-bottom:2rem;">Update author information.</p>
            
            <form action="${pageContext.request.contextPath}/authors" method="POST">
                <input type="hidden" name="id" value="${param.id}">
                
                <div class="form-group" style="margin-bottom:1.2rem;">
                    <label style="font-weight:600;color:#0b1a33;display:block;margin-bottom:0.4rem;">Full Name *</label>
                    <input type="text" name="name" value="Gabriel García Márquez" required style="width:100%;padding:0.8rem 1.2rem;border:1px solid #eef2f6;border-radius:12px;font-size:1rem;transition:0.2s;">
                </div>
                
                <div class="form-group" style="margin-bottom:1.2rem;">
                    <label style="font-weight:600;color:#0b1a33;display:block;margin-bottom:0.4rem;">Nationality</label>
                    <input type="text" name="nationality" value="Colombian" style="width:100%;padding:0.8rem 1.2rem;border:1px solid #eef2f6;border-radius:12px;font-size:1rem;transition:0.2s;">
                </div>
                
                <div class="form-group" style="margin-bottom:1.2rem;">
                    <label style="font-weight:600;color:#0b1a33;display:block;margin-bottom:0.4rem;">Birth Year</label>
                    <input type="number" name="birthYear" value="1927" style="width:100%;padding:0.8rem 1.2rem;border:1px solid #eef2f6;border-radius:12px;font-size:1rem;transition:0.2s;">
                </div>
                
                <div class="form-group" style="margin-bottom:1.2rem;">
                    <label style="font-weight:600;color:#0b1a33;display:block;margin-bottom:0.4rem;">Biography</label>
                    <textarea name="biography" rows="4" style="width:100%;padding:0.8rem 1.2rem;border:1px solid #eef2f6;border-radius:12px;font-size:1rem;transition:0.2s;font-family:inherit;resize:vertical;">Gabriel José García Márquez was a Colombian novelist, short-story writer, screenwriter, and journalist.</textarea>
                </div>
                
                <div style="display:flex;gap:1rem;margin-top:1.5rem;">
                    <button type="submit" class="btn-primary" style="padding:0.8rem 2rem;border:none;cursor:pointer;">
                        <i class="fas fa-sync-alt"></i> Update Author
                    </button>
                    <a href="${pageContext.request.contextPath}/authors/index.jsp" class="btn-outline" style="padding:0.8rem 2rem;text-decoration:none;text-align:center;">
                        <i class="fas fa-times"></i> Cancel
                    </a>
                </div>
            </form>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>