package com.mycompany.wisebooks.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.wisebooks.dto.FiltroLibroDTO;
import com.mycompany.wisebooks.dto.LibroDTO;
import com.mycompany.wisebooks.dto.LibroFormDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.service.LibroService;
import com.mycompany.wisebooks.util.SesionUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * GET  /api/libros?texto=&idCategoria=&idGenero=&soloDisponibles=true  -> lista (publico)
 * GET  /api/libros?id=5                                                -> detalle (publico)
 * POST /api/libros                        LibroFormDTO                 -> 201 crear (admin)
 * PUT  /api/libros?id=5                   LibroFormDTO                 -> editar (admin)
 * PUT  /api/libros?id=5&accion=activo     {activo: true|false}         -> activar/desactivar (admin)
 * PUT  /api/libros?id=5&accion=copias     {cantidad}                   -> agregar copias (personal)
 * PUT  /api/libros?id=5&accion=retiro     {cantidad}                   -> retirar copias (personal)
 */
@WebServlet("/api/libros")
public class LibroController extends HttpServlet {

    private final LibroService service = new LibroService();
    private final ObjectMapper mapper;

    public LibroController() {
        // Soporte para LocalDate (fechaPublicacion)
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UsuarioDTO usuario = SesionUtil.usuario(req);
        String id = req.getParameter("id");

        Object respuesta;
        if (id != null) {
            respuesta = service.obtener(usuario, Long.parseLong(id));
        } else {
            FiltroLibroDTO filtro = new FiltroLibroDTO();
            filtro.setTexto(req.getParameter("texto"));
            filtro.setIdCategoria(leerLong(req.getParameter("idCategoria")));
            filtro.setIdGenero(leerLong(req.getParameter("idGenero")));
            filtro.setSoloDisponibles("true".equals(req.getParameter("soloDisponibles")));
            respuesta = service.buscar(usuario, filtro);
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LibroFormDTO form = mapper.readValue(req.getInputStream(), LibroFormDTO.class);

        LibroDTO libro = service.crear(SesionUtil.usuario(req), form);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), libro);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UsuarioDTO usuario = SesionUtil.usuario(req);
        Long id = Long.parseLong(req.getParameter("id"));
        String accion = req.getParameter("accion");

        LibroDTO libro;
        if (accion == null) {
            libro = service.editar(usuario, id, mapper.readValue(req.getInputStream(), LibroFormDTO.class));
        } else {
            JsonNode json = mapper.readTree(req.getInputStream());
            switch (accion) {
                case "activo" -> libro = service.cambiarActivo(usuario, id, json.path("activo").asBoolean());
                case "copias" -> libro = service.agregarCopias(usuario, id, json.path("cantidad").asInt());
                case "retiro" -> libro = service.retirarCopias(usuario, id, json.path("cantidad").asInt());
                default -> throw new IllegalArgumentException("Acción desconocida: " + accion);
            }
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), libro);
    }

    private Long leerLong(String valor) {
        return (valor == null || valor.isBlank()) ? null : Long.parseLong(valor);
    }
}
