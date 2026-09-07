<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>

<nav class="main-nav">
    <ul>
        <li>
            <a href="${pageContext.request.contextPath}/index.jsp" class="${pageContext.request.servletPath == '/index.jsp' ? 'active' : ''}">
                <i class="fas fa-home"></i> Home
            </a>
        </li>
        <li class="dropdown">
            <a href="${pageContext.request.contextPath}/books/index.jsp" class="${pageContext.request.servletPath.contains('/books/') ? 'active' : ''}">
                <i class="fas fa-book"></i> Books <i class="fas fa-chevron-down"></i>
            </a>
            <ul class="dropdown-menu">
                <li><a href="${pageContext.request.contextPath}/books/index.jsp">Catalog</a></li>
                <li><a href="${pageContext.request.contextPath}/books/new.jsp">New Book</a></li>
                <li><a href="${pageContext.request.contextPath}/discover.jsp">Discover</a></li>
            </ul>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/authors/index.jsp">
                <i class="fas fa-users"></i> Authors
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/collections.jsp">
                <i class="fas fa-layer-group"></i> Collections
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/favorites.jsp">
                <i class="fas fa-heart"></i> Favorites
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/events.jsp">
                <i class="fas fa-calendar-alt"></i> Events
            </a>
        </li>
        
        <c:if test="${sessionScope.user != null && sessionScope.user.role == 'ADMIN'}">
            <li class="dropdown">
                <a href="${pageContext.request.contextPath}/admin/index.jsp" style="color:#e74c3c;">
                    <i class="fas fa-shield-alt"></i> Admin <i class="fas fa-chevron-down"></i>
                </a>
                <ul class="dropdown-menu">
                    <li><a href="${pageContext.request.contextPath}/admin/index.jsp">Dashboard</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/users.jsp">Users</a></li>
                    <li><a href="${pageContext.request.contextPath}/admin/reports.jsp">Reports</a></li>
                </ul>
            </li>
        </c:if>
        
        <c:if test="${sessionScope.user != null && sessionScope.user.role == 'EMPLOYEE'}">
            <li>
                <a href="${pageContext.request.contextPath}/employee/index.jsp" style="color:#f39c12;">
                    <i class="fas fa-user-tie"></i> Employee
                </a>
            </li>
        </c:if>
    </ul>
</nav>