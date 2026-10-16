package com.mycompany.wisebooks.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.wisebooks.dto.PrestamoDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.EstadoPrestamo;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.service.PrestamoService;
import com.mycompany.wisebooks.util.Json;
import com.mycompany.wisebooks.util.SesionUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

/*
 * GET  /api/prestamos                    -> lector: sus prestamos | personal: todos (?estado=ACTIVO)
 * GET  /api/prestamos?vencidos=true      -> personal: los vencidos
 * POST /api/prestamos  {idUsuario, idLibro}  -> 201 prestamo directo (personal)
 * POST /api/prestamos  {idReserva}           -> 201 entrega de una reserva aprobada (personal)
 * PUT  /api/prestamos?id=5&accion=renovar    -> el lector renueva el suyo
 * PUT  /api/prestamos?id=5&accion=devolver   -> personal
 * PUT  /api/prestamos?id=5&accion=perdido    {importe, detalle}  -> personal
 * PUT  /api/prestamos?id=5&accion=anular     {motivo}            -> admin
 */
@WebServlet("/api/prestamos")
public class PrestamoController extends HttpServlet {

    private final PrestamoService service = new PrestamoService();
    private final ObjectMapper mapper = Json.mapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UsuarioDTO usuario = SesionUtil.usuario(req);
        String estado = req.getParameter("estado");

        Object prestamos;
        if (usuario == null || usuario.getRol() == Rol.LECTOR) {
            prestamos = service.misPrestamos(usuario);
        } else if ("true".equals(req.getParameter("vencidos"))) {
            prestamos = service.listarVencidos(usuario);
        } else {
            prestamos = service.listar(usuario, estado == null ? null : EstadoPrestamo.valueOf(estado.toUpperCase()));
        }

        Json.escribir(resp, prestamos);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        JsonNode json = mapper.readTree(req.getInputStream());
        UsuarioDTO usuario = SesionUtil.usuario(req);

        PrestamoDTO prestamo = json.hasNonNull("idReserva")
                ? service.entregarReserva(usuario, json.get("idReserva").asLong())
                : service.entregarDirecto(usuario, json.path("idUsuario").asLong(), json.path("idLibro").asLong());

        resp.setStatus(HttpServletResponse.SC_CREATED);
        Json.escribir(resp, prestamo);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UsuarioDTO usuario = SesionUtil.usuario(req);
        Long id = Long.parseLong(req.getParameter("id"));
        String accion = String.valueOf(req.getParameter("accion"));
        JsonNode json = mapper.readTree(req.getInputStream());

        PrestamoDTO prestamo = switch (accion) {
            case "renovar" -> service.renovar(usuario, id);
            case "devolver" -> service.devolver(usuario, id);
            case "perdido" -> service.marcarPerdido(usuario, id,
                    json.hasNonNull("importe") ? new BigDecimal(json.get("importe").asText()) : null,
                    json.path("detalle").asText(null));
            case "anular" -> service.anular(usuario, id, json.path("motivo").asText(null));
            default -> throw new IllegalArgumentException("Acción desconocida: " + accion);
        };

        Json.escribir(resp, prestamo);
    }
}
