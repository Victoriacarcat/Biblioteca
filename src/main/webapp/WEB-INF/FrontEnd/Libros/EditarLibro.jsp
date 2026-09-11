<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Editar libro · WiseBooks</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/fragments/header.jsp" />

    <main class="main">
        <div class="page-header">
            <div>
                <h1 class="page-title">Editar libro</h1>
                <p class="page-subtitle">Modifica la información del título seleccionado.</p>
            </div>
        </div>

        <div class="form-card">
            <div class="form-section">
                <h3 class="form-section-title">Información principal</h3>
                <div class="form-grid">
                    <div class="form-group full">
                        <label class="form-label">Título</label>
                        <input type="text" class="form-input" value="Cien años de soledad">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Autor</label>
                        <input type="text" class="form-input" value="Gabriel García Márquez">
                    </div>
                    <div class="form-group">
                        <label class="form-label">ISBN</label>
                        <input type="text" class="form-input" value="9788437604947">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Editorial</label>
                        <input type="text" class="form-input" value="Sudamericana">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Año de publicación</label>
                        <input type="number" class="form-input" value="1967">
                    </div>
                    <div class="form-group full">
                        <label class="form-label">Categoría</label>
                        <select class="form-select">
                            <option selected>Novela</option>
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
                        <input type="number" class="form-input" value="5">
                    </div>
                    <div class="form-group">
                        <label class="form-label">Ubicación física</label>
                        <input type="text" class="form-input" value="Estante A-12">
                    </div>
                </div>
            </div>

            <div class="form-section">
                <h3 class="form-section-title">Imagen y descripción</h3>
                <div class="form-grid">
                    <div class="form-group full">
                        <label class="form-label">URL de la portada</label>
                        <input type="url" class="form-input" value="https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c">
                    </div>
                    <div class="form-group full">
                        <label class="form-label">Descripción</label>
                        <textarea class="form-textarea">La historia de la familia Buendía a lo largo de siete generaciones en el pueblo ficticio de Macondo.</textarea>
                    </div>
                </div>
            </div>

            <div class="form-actions">
                <button class="btn-primary">Guardar cambios</button>
                <a href="${pageContext.request.contextPath}/libros.jsp" class="btn-outline">Cancelar</a>
            </div>
        </div>
    </main>

    <jsp:include page="/WEB-INF/fragments/footer.jsp" />
</body>
</html>