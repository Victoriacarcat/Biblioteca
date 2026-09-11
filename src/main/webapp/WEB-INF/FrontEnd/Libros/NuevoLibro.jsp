<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nuevo libro · WiseBooks</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />

    <main class="main">
        <div class="page-header">
            <div>
                <h1 class="page-title">Agregar libro</h1>
                <p class="page-subtitle">Registra un nuevo título en el catálogo.</p>
            </div>
        </div>

        <div class="form-card">
            <div class="form-section">
                <h3 class="form-section-title">Información principal</h3>
                <div class="form-grid">
                    <div class="form-group full">
                        <label class="form-label">Título</label>
                        <input type="text" class="form-input">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Autor</label>
                        <input type="text" class="form-input">
                    </div>
                    <div class="form-group">
                        <label class="form-label">ISBN</label>
                        <input type="text" class="form-input">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Editorial</label>
                        <input type="text" class="form-input">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Año de publicación</label>
                        <input type="number" class="form-input">
                    </div>
                    <div class="form-group full">
                        <label class="form-label">Categoría</label>
                        <select class="form-select">
                            <option>Novela</option>
                            <option>Ciencia ficción</option>
                            <option>Fantasía</option>
                            <option>Infantil</option>
                            <option>Historia</option>
                            <option>Tecnología</option>
                        </select>
                    </div>
                </div>
            </div>

            <div class="form-section">
                <h3 class="form-section-title">Stock y ubicación</h3>
                <div class="form-grid">
                    <div class="form-group">
                        <label class="form-label">Cantidad de ejemplares</label>
                        <input type="number" class="form-input" value="1">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Ubicación física</label>
                        <input type="text" class="form-input" placeholder="Ej: Estante A-12">
                    </div>
                </div>
            </div>

            <div class="form-section">
                <h3 class="form-section-title">Imagen y descripción</h3>
                <div class="form-grid">
                    <div class="form-group full">
                        <label class="form-label">URL de la portada</label>
                        <input type="url" class="form-input" placeholder="https://...">
                    </div>
                    <div class="form-group full">
                        <label class="form-label">Descripción</label>
                        <textarea class="form-textarea" placeholder="Sinopsis del libro..."></textarea>
                    </div>
                </div>
            </div>

            <div class="form-actions">
                <button class="btn-primary">Guardar libro</button>
                <a href="${pageContext.request.contextPath}/libros.jsp" class="btn-outline">Cancelar</a>
            </div>
        </div>
    </main>

    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>