<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String p = request.getParameter("p");
    String target = "/WEB-INF/FrontEnd/";

    if (p == null || p.isEmpty() || p.equals("libros")) {
        target += "Libros/Libros.jsp";
    } else if (p.equals("nuevo-libro")) {
        target += "Libros/NuevoLibro.jsp";
    } else if (p.equals("editar-libro")) {
        target += "Libros/EditarLibro.jsp";
    } else if (p.equals("inventario")) {
        target += "Inventario/Inventario.jsp";
    } else if (p.equals("ejemplares")) {
        target += "Inventario/Ejemplares.jsp";
    } else if (p.equals("categorias")) {
        target += "Categorias/Categorias.jsp";
    } else if (p.equals("usuarios")) {
        target += "Usuarios/Usuarios.jsp";
    } else if (p.equals("reportes")) {
        target += "Reportes/Reportes.jsp";
    } else if (p.equals("mi-cuenta")) {
        target += "MiCuenta/MiCuenta.jsp";
    } else if (p.equals("configuracion")) {
        target += "Configuracion/Configuracion.jsp";
    } else {
        target += "Libros/Libros.jsp";
    }

    request.getRequestDispatcher(target).forward(request, response);
%>