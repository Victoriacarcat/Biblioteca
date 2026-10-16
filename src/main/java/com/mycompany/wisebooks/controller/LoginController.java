package com.mycompany.wisebooks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.wisebooks.dto.LoginDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.service.UsuarioService;
import com.mycompany.wisebooks.util.Json;
import com.mycompany.wisebooks.util.SesionUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/*
 * POST /api/login   {correo, contrasena}  -> UsuarioDTO y abre la sesion
 * GET  /api/login                         -> {"usuario": UsuarioDTO o null} (quien inicio sesion)
 */
@WebServlet("/api/login")
public class LoginController extends HttpServlet {

    private final UsuarioService service = new UsuarioService();
    private final ObjectMapper mapper = Json.mapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // HashMap y no Map.of: Map.of no acepta null (visitante)
        Map<String, Object> sesion = new HashMap<>();
        sesion.put("usuario", SesionUtil.usuario(req));

        Json.escribir(resp, sesion);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LoginDTO login = mapper.readValue(req.getInputStream(), LoginDTO.class);

        // Si el correo o la contrasena fallan, el service lanza ValidacionException (400)
        UsuarioDTO usuario = service.iniciarSesion(login);

        HttpSession session = req.getSession(true);
        req.changeSessionId(); // id nuevo al iniciar sesion (evita fijacion de sesion)
        session.setAttribute(SesionUtil.ATRIBUTO_USUARIO, usuario); // DTO, no Entity

        Json.escribir(resp, usuario);
    }
}
