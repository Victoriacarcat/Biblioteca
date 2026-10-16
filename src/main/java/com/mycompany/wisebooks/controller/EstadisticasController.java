package com.mycompany.wisebooks.controller;

import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.service.DashboardService;
import com.mycompany.wisebooks.util.Json;
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UsuarioDTO usuario = SesionUtil.usuario(req);

        Object estadisticas = "lector".equals(req.getParameter("tipo"))
                ? service.resumenLector(usuario)
                : service.estadisticas(usuario);

        Json.escribir(resp, estadisticas);
    }
}
