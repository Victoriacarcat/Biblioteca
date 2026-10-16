package com.mycompany.wisebooks.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * Un solo ObjectMapper para toda la API, ya configurado:
 *   - entiende LocalDate y LocalDateTime (JavaTimeModule)
 *   - escribe las fechas como texto "2026-10-15T14:30:00" y no como numeros
 * Asi ningun controller se queda sin esa configuracion.
 * ObjectMapper es seguro entre hilos una vez configurado.
 *
 * Uso en un controller:
 *   LibroFormDTO form = Json.mapper().readValue(req.getInputStream(), LibroFormDTO.class);
 *   Json.escribir(resp, libro);
 */
public final class Json {

    private static final ObjectMapper MAPPER = crear();

    private Json() {
    }

    private static ObjectMapper crear() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    public static ObjectMapper mapper() {
        return MAPPER;
    }

    /* Responde con "datos" en JSON (UTF-8). El codigo HTTP se fija antes, si no es 200. */
    public static void escribir(HttpServletResponse resp, Object datos) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        MAPPER.writeValue(resp.getWriter(), datos);
    }
}
