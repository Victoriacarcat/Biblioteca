package com.mycompany.wisebooks.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.wisebooks.dto.ReservaDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.EstadoReserva;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.service.ReservaService;
import com.mycompany.wisebooks.util.SesionUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * GET  /api/reservas                       -> lector: sus reservas | personal: todas (?estado=PENDIENTE)
 * POST /api/reservas         {idLibro}     -> 201 reservar (lector)
 * PUT  /api/reservas?id=5&accion=cancelar  -> el lector cancela la suya
 * PUT  /api/reservas?id=5&accion=aprobar   -> personal
 * PUT  /api/reservas?id=5&accion=rechazar  {observaciones}  -> personal
 */
@WebServlet("/api/reservas")
public class ReservaController extends HttpServlet {

    private final ReservaService service = new ReservaService();
    private final ObjectMapper mapper;

    public ReservaController() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UsuarioDTO usuario = SesionUtil.usuario(req);
        String estado = req.getParameter("estado");

        Object reservas = (usuario == null || usuario.getRol() == Rol.LECTOR)
                ? service.misReservas(usuario)
                : service.listar(usuario, estado == null ? null : EstadoReserva.valueOf(estado.toUpperCase()));

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), reservas);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonNode json = mapper.readTree(req.getInputStream());

        ReservaDTO reserva = service.crear(SesionUtil.usuario(req), json.path("idLibro").asLong());

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), reserva);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UsuarioDTO usuario = SesionUtil.usuario(req);
        Long id = Long.parseLong(req.getParameter("id"));
        String accion = String.valueOf(req.getParameter("accion"));
        JsonNode json = mapper.readTree(req.getInputStream());

        ReservaDTO reserva = switch (accion) {
            case "cancelar" -> service.cancelar(usuario, id);
            case "aprobar" -> service.aprobar(usuario, id);
            case "rechazar" -> service.rechazar(usuario, id, json.path("observaciones").asText(null));
            default -> throw new IllegalArgumentException("Acción desconocida: " + accion);
        };

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), reserva);
    }
}
