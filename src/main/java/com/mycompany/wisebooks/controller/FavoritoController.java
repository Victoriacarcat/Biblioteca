package com.mycompany.wisebooks.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.wisebooks.service.FavoritoService;
import com.mycompany.wisebooks.util.SesionUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/*
 * Favoritos del lector.
 * GET    /api/favoritos               -> sus libros favoritos
 * GET    /api/favoritos?idLibro=5     -> {"favorito": true|false}
 * POST   /api/favoritos  {idLibro}    -> agrega
 * DELETE /api/favoritos?idLibro=5     -> quita
 */
@WebServlet("/api/favoritos")
public class FavoritoController extends HttpServlet {

    private final FavoritoService service = new FavoritoService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idLibro = req.getParameter("idLibro");

        Object respuesta = (idLibro != null)
                ? Map.of("favorito", service.esFavorito(SesionUtil.usuario(req), Long.parseLong(idLibro)))
                : service.misFavoritos(SesionUtil.usuario(req));

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonNode json = mapper.readTree(req.getInputStream());

        service.agregar(SesionUtil.usuario(req), json.path("idLibro").asLong());

        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        String idLibro = req.getParameter("idLibro");

        service.quitar(SesionUtil.usuario(req), Long.parseLong(idLibro));

        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
