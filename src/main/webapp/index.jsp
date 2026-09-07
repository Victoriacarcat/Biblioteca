<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WiseBooks - Library System</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />
    
    <!-- ===== HERO SECTION ===== -->
    <section class="hero">
        <div class="hero-content">
            <span class="badge"><i class="fas fa-sparkles"></i> Digital Library</span>
            <h1>Tu biblioteca digital <span>siempre abierta</span></h1>
            <p>Miles de libros, revistas y audiolibros a un clic. Descubre, aprende y comparte</p>
            <div class="hero-actions">
                <a href="${pageContext.request.contextPath}/books/index.jsp" class="btn-primary">View Books</a>
                <a href="${pageContext.request.contextPath}/discover.jsp" class="btn-outline">Discover</a>
            </div>
            <div class="hero-stats">
                <div class="stat">
                    <span class="number">1,284</span>
                    <span class="label">Books</span>
                </div>
                <div class="stat">
                    <span class="number">45</span>
                    <span class="label">Active Loans</span>
                </div>
                <div class="stat">
                    <span class="number">12</span>
                    <span class="label">Students</span>
                </div>
            </div>
        </div>
        <div class="hero-visual">
            <img src="${pageContext.request.contextPath}/images/banner.png" alt="Library Banner">
        </div>
    </section>

    <!-- ===== CATEGORIES ===== -->
    <div class="categories">
        <span class="pill"><i class="fas fa-tag"></i> All</span>
        <span class="pill"><i class="fas fa-flask"></i> Science</span>
        <span class="pill"><i class="fas fa-history"></i> History</span>
        <span class="pill"><i class="fas fa-paint-brush"></i> Art</span>
        <span class="pill"><i class="fas fa-code"></i> Technology</span>
        <span class="pill"><i class="fas fa-heart"></i> Romance</span>
        <span class="pill"><i class="fas fa-mask"></i> Mystery</span>
        <span class="pill"><i class="fas fa-dragon"></i> Fantasy</span>
    </div>

    <!-- ===== FEATURED BOOKS ===== -->
    <section class="library-grid">
        <div class="section-header">
            <h2><i class="fas fa-star" style="color: #394eff;"></i> Featured Books</h2>
            <a href="${pageContext.request.contextPath}/books/index.jsp">View All <i class="fas fa-arrow-right"></i></a>
        </div>
        <div class="grid">
            <!-- Book 1 -->
            <div class="book-card">
                <div class="book-cover">
                    <img src="https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=600&q=80" alt="Book" class="book-cover-img">
                </div>
                <div class="book-title">Cien años de soledad</div>
                <div class="book-author">Gabriel García Márquez</div>
                <div class="book-status available"><i class="fas fa-check-circle"></i> Available</div>
            </div>
            <!-- Book 2 -->
            <div class="book-card">
                <div class="book-cover">
                    <img src="https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=600&q=80" alt="Book" class="book-cover-img">
                </div>
                <div class="book-title">1984</div>
                <div class="book-author">George Orwell</div>
                <div class="book-status available"><i class="fas fa-check-circle"></i> Available</div>
            </div>
            <!-- Book 3 -->
            <div class="book-card">
                <div class="book-cover">
                    <img src="https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=600&q=80" alt="Book" class="book-cover-img">
                </div>
                <div class="book-title">El principito</div>
                <div class="book-author">Antoine de Saint-Exupéry</div>
                <div class="book-status available"><i class="fas fa-check-circle"></i> Available</div>
            </div>
            <!-- Book 4 -->
            <div class="book-card">
                <div class="book-cover">
                    <img src="https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=600&q=80" alt="Book" class="book-cover-img">
                </div>
                <div class="book-title">Don Quijote</div>
                <div class="book-author">Miguel de Cervantes</div>
                <div class="book-status loaned"><i class="fas fa-clock"></i> Loaned</div>
            </div>
        </div>
    </section>

    <!-- ===== STATISTICS ===== -->
    <section class="stats-section">
        <div class="stats-container">
            <div class="stat-item">
                <i class="fas fa-book-open"></i>
                <span class="stat-number">12,845</span>
                <span class="stat-label">Books Available</span>
            </div>
            <div class="stat-item">
                <i class="fas fa-users"></i>
                <span class="stat-number">3,421</span>
                <span class="stat-label">Active Readers</span>
            </div>
            <div class="stat-item">
                <i class="fas fa-hand-holding-heart"></i>
                <span class="stat-number">892</span>
                <span class="stat-label">Books Loaned</span>
            </div>
            <div class="stat-item">
                <i class="fas fa-calendar-check"></i>
                <span class="stat-number">156</span>
                <span class="stat-label">Events This Year</span>
            </div>
        </div>
    </section>

    <!-- ===== FEATURES ===== -->
    <section class="features">
        <h2 class="features-title"><i class="fas fa-cogs" style="color: #394eff;"></i> What We Offer</h2>
        <div class="features-grid">
            <div class="feature-card">
                <div class="icon"><i class="fas fa-book"></i></div>
                <h3>Complete Catalog</h3>
                <p>Access thousands of books across all categories.</p>
            </div>
            <div class="feature-card">
                <div class="icon"><i class="fas fa-hand-holding-heart"></i></div>
                <h3>Fast Loans</h3>
                <p>Manage loans and returns with ease.</p>
            </div>
            <div class="feature-card">
                <div class="icon"><i class="fas fa-user-graduate"></i></div>
                <h3>Student Management</h3>
                <p>Keep track of all library users.</p>
            </div>
            <div class="feature-card">
                <div class="icon"><i class="fas fa-chart-line"></i></div>
                <h3>Detailed Reports</h3>
                <p>Get insights with comprehensive statistics.</p>
            </div>
        </div>
    </section>

    <!-- ===== TESTIMONIALS ===== -->
    <section class="testimonials">
        <h2 class="testimonials-title"><i class="fas fa-quote-left" style="color: #394eff;"></i> What Our Readers Say</h2>
        <div class="testimonials-grid">
            <div class="testimonial-card">
                <div class="testimonial-avatar">
                    <i class="fas fa-user-circle"></i>
                </div>
                <p class="testimonial-text">"WiseBooks has completely transformed how I manage my reading. The catalog is amazing!"</p>
                <span class="testimonial-author">— María González</span>
            </div>
            <div class="testimonial-card">
                <div class="testimonial-avatar">
                    <i class="fas fa-user-circle"></i>
                </div>
                <p class="testimonial-text">"The loan system is so easy to use. I can find and borrow books in minutes."</p>
                <span class="testimonial-author">— Carlos Pérez</span>
            </div>
            <div class="testimonial-card">
                <div class="testimonial-avatar">
                    <i class="fas fa-user-circle"></i>
                </div>
                <p class="testimonial-text">"I love the recommendations! I've discovered so many amazing books."</p>
                <span class="testimonial-author">— Laura Martínez</span>
            </div>
        </div>
    </section>

    <!-- ===== NEWSLETTER / CTA ===== -->
    <section class="cta-section">
        <div class="cta-container">
            <h2>Stay Updated</h2>
            <p>Subscribe to our newsletter and never miss new arrivals or events.</p>
            <form action="#" method="POST" class="cta-form">
                <input type="email" placeholder="Enter your email" required>
                <button type="submit" class="btn-primary">Subscribe</button>
            </form>
        </div>
    </section>

    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>