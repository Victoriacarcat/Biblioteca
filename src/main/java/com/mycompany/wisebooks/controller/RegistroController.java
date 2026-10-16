package com.mycompany.wisebooks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mycompany.wisebooks.dto.RegistroDTO;
import com.mycompany.wisebooks.service.UsuarioService;
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
    private final ObjectMapper mapper;

    public RegistroController() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        RegistroDTO registro = mapper.readValue(req.getInputStream(), RegistroDTO.class);

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), service.registrar(registro));
    }
}
