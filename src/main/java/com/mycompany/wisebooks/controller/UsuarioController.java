package com.mycompany.wisebooks.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.wisebooks.dto.RegistroDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.EstadoUsuario;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.service.UsuarioService;
import com.mycompany.wisebooks.util.SesionUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * Usuarios.
 * Solo administrador:
 *   GET  /api/usuarios?texto=&rol=LECTOR            -> lista
 *   GET  /api/usuarios?id=5                         -> uno
 *   POST /api/usuarios  RegistroDTO + {rol}         -> 201 crea personal (BIBLIOTECARIO o ADMINISTRADOR)
 *   PUT  /api/usuarios?id=5&accion=rol     {rol}    -> cambia el rol
 *   PUT  /api/usuarios?id=5&accion=estado  {estado} -> ACTIVO, SUSPENDIDO o INACTIVO
 * Cualquier usuario con sesion:
 *   PUT  /api/usuarios?accion=contrasena  {actual, nueva}  -> cambia su propia contrasena
 * (El registro publico de lectores es /api/registro.)
 */
@WebServlet("/api/usuarios")
public class UsuarioController extends HttpServlet {

    private final UsuarioService service = new UsuarioService();
    private final ObjectMapper mapper;

    public UsuarioController() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UsuarioDTO usuario = SesionUtil.usuario(req);
        String id = req.getParameter("id");
        String rol = req.getParameter("rol");

        Object respuesta = (id != null)
                ? service.obtener(usuario, Long.parseLong(id))
                : service.listar(usuario, req.getParameter("texto"), rol == null ? null : Rol.valueOf(rol.toUpperCase()));

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ObjectNode json = (ObjectNode) mapper.readTree(req.getInputStream());
        Rol rol = leerRol(json);
        RegistroDTO registro = mapper.treeToValue(json, RegistroDTO.class);

        UsuarioDTO creado = service.crearPersonal(SesionUtil.usuario(req), registro, rol);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), creado);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UsuarioDTO usuario = SesionUtil.usuario(req);
        String accion = String.valueOf(req.getParameter("accion"));
        JsonNode json = mapper.readTree(req.getInputStream());

        // Cambiar la contrasena propia: sin id, la de quien inicio sesion (cualquier rol)
        if (accion.equals("contrasena")) {
            service.cambiarContrasena(usuario, json.path("actual").asText(null), json.path("nueva").asText(null));
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        Long id = Long.parseLong(req.getParameter("id"));
        UsuarioDTO actualizado = switch (accion) {
            case "rol" -> service.cambiarRol(usuario, id, Rol.valueOf(json.path("rol").asText().toUpperCase()));
            case "estado" -> service.cambiarEstado(usuario, id,
                    EstadoUsuario.valueOf(json.path("estado").asText().toUpperCase()));
            default -> throw new IllegalArgumentException("Acción desconocida: " + accion);
        };

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), actualizado);
    }

    // El rol llega como "rol" en el JSON; se separa porque RegistroDTO no tiene ese campo
    private Rol leerRol(ObjectNode json) {
        Rol rol = json.hasNonNull("rol") ? Rol.valueOf(json.get("rol").asText().toUpperCase()) : null;
        json.remove("rol");
        return rol;
    }
}
