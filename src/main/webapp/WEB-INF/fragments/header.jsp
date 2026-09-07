<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>

<header class="app-header">
    <div class="logo">
        <a href="${pageContext.request.contextPath}/index.jsp">
            <i class="fas fa-book-open"></i>
            <span>WiseBooks</span>
        </a>
    </div>

    <jsp:include page="/WEB-INF/fragments/navbar.jsp" />

    <div class="header-actions">
        <form action="${pageContext.request.contextPath}/search.jsp" method="GET" class="search-box">
            <input type="text" name="q" placeholder="Search..." required>
            <button type="submit"><i class="fas fa-search"></i></button>
        </form>
        
        <div class="user-actions">
            <c:choose>
                <c:when test="${sessionScope.user != null}">
                    <span class="user-name">
                        <i class="fas fa-user-circle"></i> ${sessionScope.user.username}
                    </span>
                    <a href="${pageContext.request.contextPath}/profile.jsp" class="btn-outline">
                        <i class="fas fa-user"></i>
                    </a>
                    <a href="${pageContext.request.contextPath}/logout" class="btn-outline" style="border-color:#e74c3c;color:#e74c3c;">
                        <i class="fas fa-sign-out-alt"></i> Logout
                    </a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login.jsp" class="btn-outline">
                        <i class="fas fa-sign-in-alt"></i> Login
                    </a>
                    <a href="${pageContext.request.contextPath}/register.jsp" class="btn-primary">
                        <i class="fas fa-user-plus"></i> Register
                    </a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</header>