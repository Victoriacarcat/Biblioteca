package com.mycompany.wisebooks.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.wisebooks.dto.ProveedorDTO;
import com.mycompany.wisebooks.dto.ProveedorFormDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.service.ProveedorService;
import com.mycompany.wisebooks.util.SesionUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * GET  /api/proveedores?texto=&inactivos=true      -> lista (personal)
 * GET  /api/proveedores?id=5                       -> uno (personal)
 * POST /api/proveedores          ProveedorFormDTO  -> 201 crear (admin)
 * PUT  /api/proveedores?id=5     ProveedorFormDTO  -> editar (admin)
 * PUT  /api/proveedores?id=5&accion=activo  {activo: true|false}  -> activar/desactivar (admin)
 */
@WebServlet("/api/proveedores")
public class ProveedorController extends HttpServlet {

    private final ProveedorService service = new ProveedorService();
    private final ObjectMapper mapper;

    public ProveedorController() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UsuarioDTO usuario = SesionUtil.usuario(req);
        String id = req.getParameter("id");

        Object respuesta = (id != null)
                ? service.obtener(usuario, Long.parseLong(id))
                : service.listar(usuario, req.getParameter("texto"), "true".equals(req.getParameter("inactivos")));

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ProveedorFormDTO form = mapper.readValue(req.getInputStream(), ProveedorFormDTO.class);

        ProveedorDTO proveedor = service.crear(SesionUtil.usuario(req), form);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), proveedor);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UsuarioDTO usuario = SesionUtil.usuario(req);
        Long id = Long.parseLong(req.getParameter("id"));

        ProveedorDTO proveedor;
        if ("activo".equals(req.getParameter("accion"))) {
            JsonNode json = mapper.readTree(req.getInputStream());
            proveedor = service.cambiarActivo(usuario, id, json.path("activo").asBoolean());
        } else {
            proveedor = service.editar(usuario, id, mapper.readValue(req.getInputStream(), ProveedorFormDTO.class));
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), proveedor);
    }
}
