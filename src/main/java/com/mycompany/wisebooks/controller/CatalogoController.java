package com.mycompany.wisebooks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.wisebooks.service.LibroService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * Listas del catalogo: Categoria, Genero, Editorial y Autor (publico).
 * GET /api/catalogos?tipo=categorias   -> [{id, nombre}]
 * GET /api/catalogos?tipo=generos      -> [{id, nombre}]
 * GET /api/catalogos?tipo=editoriales  -> [{id, nombre}]
 * GET /api/catalogos?tipo=autores      -> [{id, nombre}]
 * No se crean aqui: se crean solos al guardar un libro con un nombre nuevo.
 */
@WebServlet("/api/catalogos")
public class CatalogoController extends HttpServlet {

    private final LibroService service = new LibroService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String tipo = String.valueOf(req.getParameter("tipo"));

        Object lista = switch (tipo) {
            case "categorias" -> service.listarCategorias();
            case "generos" -> service.listarGeneros();
            case "editoriales" -> service.listarEditoriales();
            case "autores" -> service.listarAutores();
            default -> throw new IllegalArgumentException("Tipo de catálogo desconocido: " + tipo);
        };

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), lista);
    }
}
