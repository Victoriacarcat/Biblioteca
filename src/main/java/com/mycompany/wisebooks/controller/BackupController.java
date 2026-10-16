package com.mycompany.wisebooks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.wisebooks.dto.CopiaSeguridadDTO;
import com.mycompany.wisebooks.service.CopiaSeguridadService;
import com.mycompany.wisebooks.util.SesionUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/*
 * Copias de seguridad (solo administrador).
 * GET  /api/backup          -> lista de respaldos
 * GET  /api/backup?id=5     -> descarga el archivo .sql
 * POST /api/backup          -> 201 genera uno nuevo
 */
@WebServlet("/api/backup")
public class BackupController extends HttpServlet {

    private final CopiaSeguridadService service = new CopiaSeguridadService();
    private final ObjectMapper mapper;

    public BackupController() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");

        if (id == null) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            mapper.writeValue(resp.getWriter(), service.listar(SesionUtil.usuario(req)));
            return;
        }

        Path backup = service.obtenerArchivo(SesionUtil.usuario(req), Long.parseLong(id));

        resp.setContentType("application/sql");
        resp.setHeader("Content-Disposition", "attachment; filename=" + backup.getFileName());

        try (OutputStream os = resp.getOutputStream()) {
            Files.copy(backup, os);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CopiaSeguridadDTO copia = service.generar(SesionUtil.usuario(req));

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), copia);
    }
}
