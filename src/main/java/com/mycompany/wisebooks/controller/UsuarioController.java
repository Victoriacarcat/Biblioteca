package com.mycompany.wisebooks.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mycompany.wisebooks.dto.RegistroDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.EstadoUsuario;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.service.UsuarioService;
import com.mycompany.wisebooks.util.Json;
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
 *   POST /api/usuarios  RegistroDTO + {rol}         -> 201 crea una cuenta (LECTOR, BIBLIOTECARIO o ADMINISTRADOR)
 *   PUT  /api/usuarios?id=5&accion=rol     {rol}    -> cambia el rol (LECTOR o BIBLIOTECARIO)
 *   PUT  /api/usuarios?id=5&accion=estado  {estado} -> ACTIVO, SUSPENDIDO o INACTIVO
 *   El rol y el estado de un administrador que ya existe no se cambian aqui.
 * Administrador y bibliotecario:
 *   GET  /api/usuarios?tipo=lectores&texto=ana      -> lectores que coinciden (max. 20)
 * Cualquier usuario con sesion:
 *   PUT  /api/usuarios?accion=contrasena  {actual, nueva}  -> cambia su propia contrasena
 * (El registro publico de lectores es /api/registro.)
 */
@WebServlet("/api/usuarios")
public class UsuarioController extends HttpServlet {

    private final UsuarioService service = new UsuarioService();
    private final ObjectMapper mapper = Json.mapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UsuarioDTO usuario = SesionUtil.usuario(req);
        String id = req.getParameter("id");
        String rol = req.getParameter("rol");

        Object respuesta;
        if ("lectores".equals(req.getParameter("tipo"))) {
            respuesta = service.buscarLectores(usuario, req.getParameter("texto"));
        } else if (id != null) {
            respuesta = service.obtener(usuario, Long.parseLong(id));
        } else {
            respuesta = service.listar(usuario, req.getParameter("texto"), rol == null ? null : Rol.valueOf(rol.toUpperCase()));
        }

        Json.escribir(resp, respuesta);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ObjectNode json = (ObjectNode) mapper.readTree(req.getInputStream());
        Rol rol = leerRol(json);
        RegistroDTO registro = mapper.treeToValue(json, RegistroDTO.class);

        UsuarioDTO creado = service.crearCuenta(SesionUtil.usuario(req), registro, rol);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        Json.escribir(resp, creado);
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

        Json.escribir(resp, actualizado);
    }

    // El rol llega como "rol" en el JSON; se separa porque RegistroDTO no tiene ese campo
    private Rol leerRol(ObjectNode json) {
        Rol rol = json.hasNonNull("rol") ? Rol.valueOf(json.get("rol").asText().toUpperCase()) : null;
        json.remove("rol");
        return rol;
    }
}
