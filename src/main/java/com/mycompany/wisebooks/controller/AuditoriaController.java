package com.mycompany.wisebooks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.AccionAuditoria;
import com.mycompany.wisebooks.service.AuditoriaService;
import com.mycompany.wisebooks.util.SesionUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

/*
 * Bitacora (solo administrador).
 * GET /api/auditoria?idUsuario=&accion=CREAR&desde=2026-10-01&hasta=2026-10-15  -> registros
 * GET /api/auditoria?entidad=Libro&idRegistro=5                                 -> historial de un registro
 */
@WebServlet("/api/auditoria")
public class AuditoriaController extends HttpServlet {

    private final AuditoriaService service = new AuditoriaService();
    private final ObjectMapper mapper;

    public AuditoriaController() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UsuarioDTO usuario = SesionUtil.usuario(req);
        String entidad = req.getParameter("entidad");

        Object registros;
        if (entidad != null) {
            registros = service.historialDe(usuario, entidad, Long.parseLong(req.getParameter("idRegistro")));
        } else {
            String idUsuario = req.getParameter("idUsuario");
            String accion = req.getParameter("accion");
            String desde = req.getParameter("desde");
            String hasta = req.getParameter("hasta");
            registros = service.listar(usuario,
                    idUsuario == null ? null : Long.parseLong(idUsuario),
                    accion == null ? null : AccionAuditoria.valueOf(accion.toUpperCase()),
                    desde == null ? null : LocalDate.parse(desde),
                    hasta == null ? null : LocalDate.parse(hasta));
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), registros);
    }
}
