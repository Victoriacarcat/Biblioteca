<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>

<header class="app-header">
    <div class="header-inner">
        <div class="logo">
            <a href="${pageContext.request.contextPath}/index.jsp">
                <img src="${pageContext.request.contextPath}/images/logo.png" alt="WiseBooks Logo">
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
                        <a href="${pageContext.request.contextPath}/profile.jsp" class="btn-outline btn-sm">
                            <i class="fas fa-user"></i>
                        </a>
                        <a href="${pageContext.request.contextPath}/logout" class="btn-outline btn-sm logout-btn">
                            <i class="fas fa-sign-out-alt"></i> Logout
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login.jsp" class="btn-outline btn-sm">
                            <i class="fas fa-sign-in-alt"></i> Login
                        </a>
                        <a href="${pageContext.request.contextPath}/register.jsp" class="btn-primary btn-sm">
                            <i class="fas fa-user-plus"></i> Register
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</header>