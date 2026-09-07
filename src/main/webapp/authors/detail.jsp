<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Author Details - WiseBooks</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <main class="container" style="padding: 2.5rem 0 4rem; max-width: 800px;">
        <div style="margin-bottom:1.5rem;">
            <a href="${pageContext.request.contextPath}/authors/index.jsp" style="color:#394eff;font-weight:500;">
                <i class="fas fa-arrow-left"></i> Back to Authors
            </a>
        </div>
        
        <div style="background:#fff;border-radius:20px;padding:2.5rem;border:1px solid #eef2f6;">
            <div style="text-align:center;margin-bottom:2rem;">
                <div style="width:120px;height:120px;border-radius:50%;background:linear-gradient(135deg,#394eff,#6c8cff);color:#fff;display:flex;align-items:center;justify-content:center;font-size:3rem;margin:0 auto 1rem;">
                    <i class="fas fa-user"></i>
                </div>
                <h1 style="font-size:2rem;font-weight:700;color:#0b1a33;">Gabriel García Márquez</h1>
                <p style="color:#6a7a92;font-size:1.1rem;">Colombian | 1927 - 2014</p>
            </div>
            
            <div style="border-top:1px solid #eef2f6;padding-top:1.5rem;">
                <h3 style="color:#0b1a33;margin-bottom:1rem;"><i class="fas fa-info-circle" style="color:#394eff;"></i> Biography</h3>
                <p style="color:#4a5a72;line-height:1.8;">
                    Gabriel José García Márquez was a Colombian novelist, short-story writer, screenwriter, and journalist. 
                    He was one of the most significant authors of the 20th century and one of the best in the Spanish language. 
                    He was awarded the 1972 Neustadt International Prize for Literature and the 1982 Nobel Prize in Literature.
                </p>
            </div>
            
            <div style="border-top:1px solid #eef2f6;padding-top:1.5rem;margin-top:1.5rem;">
                <h3 style="color:#0b1a33;margin-bottom:1rem;"><i class="fas fa-book" style="color:#394eff;"></i> Books by this author</h3>
                <ul style="list-style:none;padding:0;">
                    <li style="padding:0.5rem 0;border-bottom:1px solid #f0f2f5;display:flex;justify-content:space-between;">
                        <span>One Hundred Years of Solitude</span>
                        <span style="color:#6a7a92;">1967</span>
                    </li>
                    <li style="padding:0.5rem 0;border-bottom:1px solid #f0f2f5;display:flex;justify-content:space-between;">
                        <span>Love in the Time of Cholera</span>
                        <span style="color:#6a7a92;">1985</span>
                    </li>
                    <li style="padding:0.5rem 0;display:flex;justify-content:space-between;">
                        <span>The Autumn of the Patriarch</span>
                        <span style="color:#6a7a92;">1975</span>
                    </li>
                </ul>
            </div>
            
            <div style="border-top:1px solid #eef2f6;padding-top:1.5rem;margin-top:1.5rem;display:flex;gap:1rem;">
                <a href="${pageContext.request.contextPath}/authors/edit.jsp" class="btn-primary" style="padding:0.6rem 2rem;text-decoration:none;">
                    <i class="fas fa-edit"></i> Edit Author
                </a>
                <a href="#" class="btn-outline" style="padding:0.6rem 2rem;border-color:#e74c3c;color:#e74c3c;text-decoration:none;" onclick="return confirm('Delete this author?')">
                    <i class="fas fa-trash-alt"></i> Delete
                </a>
            </div>
        </div>
    </main>
    
    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>