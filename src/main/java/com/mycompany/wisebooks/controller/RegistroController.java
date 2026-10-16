package com.mycompany.wisebooks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.wisebooks.dto.RegistroDTO;
import com.mycompany.wisebooks.service.UsuarioService;
import com.mycompany.wisebooks.util.Json;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * POST /api/registro  RegistroDTO  -> 201 UsuarioDTO
 * Registro publico: siempre crea un LECTOR y NO inicia sesion.
 */
@WebServlet("/api/registro")
public class RegistroController extends HttpServlet {

    private final UsuarioService service = new UsuarioService();
    private final ObjectMapper mapper = Json.mapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RegistroDTO registro = mapper.readValue(req.getInputStream(), RegistroDTO.class);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        Json.escribir(resp, service.registrar(registro));
    }
}
