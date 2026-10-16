package com.mycompany.wisebooks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.service.DashboardService;
import com.mycompany.wisebooks.util.SesionUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * Numeros del dashboard.
 * GET /api/estadisticas              -> generales (publico; el personal ve tambien los internos)
 * GET /api/estadisticas?tipo=lector  -> resumen personal del lector
 */
@WebServlet("/api/estadisticas")
public class EstadisticasController extends HttpServlet {

    private final DashboardService service = new DashboardService();
    private final ObjectMapper mapper;

    public EstadisticasController() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UsuarioDTO usuario = SesionUtil.usuario(req);

        Object estadisticas = "lector".equals(req.getParameter("tipo"))
                ? service.resumenLector(usuario)
                : service.estadisticas(usuario);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), estadisticas);
    }
}
