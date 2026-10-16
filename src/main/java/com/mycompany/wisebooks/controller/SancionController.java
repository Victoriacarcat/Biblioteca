package com.mycompany.wisebooks.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.wisebooks.dto.SancionDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.EstadoSancion;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.entity.TipoSancion;
import com.mycompany.wisebooks.service.SancionService;
import com.mycompany.wisebooks.util.SesionUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

/*
 * GET  /api/sanciones                    -> lector: sus sanciones | personal: todas (?estado=PENDIENTE)
 * POST /api/sanciones  {idUsuario, idPrestamo, tipo, importe, detalle}  -> 201 (personal)
 *                      tipo = "LIBRO_DANADO" u "OTRO"
 * PUT  /api/sanciones?id=5&accion=pagar               -> personal
 * PUT  /api/sanciones?id=5&accion=anular  {motivo}    -> admin
 */
@WebServlet("/api/sanciones")
public class SancionController extends HttpServlet {

    private final SancionService service = new SancionService();
    private final ObjectMapper mapper;

    public SancionController() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UsuarioDTO usuario = SesionUtil.usuario(req);
        String estado = req.getParameter("estado");

        Object sanciones = (usuario == null || usuario.getRol() == Rol.LECTOR)
                ? service.misSanciones(usuario)
                : service.listar(usuario, estado == null ? null : EstadoSancion.valueOf(estado.toUpperCase()));

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), sanciones);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonNode json = mapper.readTree(req.getInputStream());

        SancionDTO sancion = service.registrar(SesionUtil.usuario(req),
                json.hasNonNull("idUsuario") ? json.get("idUsuario").asLong() : null,
                json.hasNonNull("idPrestamo") ? json.get("idPrestamo").asLong() : null,
                TipoSancion.valueOf(json.path("tipo").asText().toUpperCase()),
                json.hasNonNull("importe") ? new BigDecimal(json.get("importe").asText()) : null,
                json.path("detalle").asText(null));

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), sancion);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UsuarioDTO usuario = SesionUtil.usuario(req);
        Long id = Long.parseLong(req.getParameter("id"));
        String accion = String.valueOf(req.getParameter("accion"));
        JsonNode json = mapper.readTree(req.getInputStream());

        SancionDTO sancion = switch (accion) {
            case "pagar" -> service.pagar(usuario, id);
            case "anular" -> service.anular(usuario, id, json.path("motivo").asText(null));
            default -> throw new IllegalArgumentException("Acción desconocida: " + accion);
        };

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), sancion);
    }
}
