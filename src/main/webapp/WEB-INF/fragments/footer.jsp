<%@page contentType="text/html" pageEncoding="UTF-8"%>

<footer class="app-footer">
    <div class="footer-content">
        <div class="footer-brand">
            <div class="logo">
                <i class="fas fa-book-open"></i>
                <span>WiseBooks</span>
            </div>
            <p>Modern and efficient library management system.</p>
        </div>
        <div class="footer-links">
            <div class="footer-column">
                <h4>Navigation</h4>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/index.jsp">Home</a></li>
                    <li><a href="${pageContext.request.contextPath}/books/index.jsp">Books</a></li>
                    <li><a href="${pageContext.request.contextPath}/authors/index.jsp">Authors</a></li>
                    <li><a href="${pageContext.request.contextPath}/collections.jsp">Collections</a></li>
                </ul>
            </div>
            <div class="footer-column">
                <h4>Administration</h4>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/admin/index.jsp">Admin Panel</a></li>
                    <li><a href="${pageContext.request.contextPath}/employee/index.jsp">Employee Panel</a></li>
                </ul>
            </div>
            <div class="footer-column">
                <h4>Legal</h4>
                <ul>
                    <li><a href="${pageContext.request.contextPath}/terms.jsp">Terms</a></li>
                    <li><a href="${pageContext.request.contextPath}/privacy.jsp">Privacy</a></li>
                </ul>
            </div>
            <div class="footer-column">
                <h4>Follow Us</h4>
                <div class="social-links">
                    <a href="#"><i class="fab fa-github"></i></a>
                    <a href="#"><i class="fab fa-twitter"></i></a>
                    <a href="#"><i class="fab fa-linkedin"></i></a>
                </div>
            </div>
        </div>
    </div>
    <div class="footer-bottom">
        <span>© 2025 WiseBooks · All rights reserved</span>
        <span>v2.0</span>
    </div>
</footer>