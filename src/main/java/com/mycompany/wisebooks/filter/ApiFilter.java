package com.mycompany.wisebooks.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mycompany.wisebooks.util.PermisoException;
import com.mycompany.wisebooks.util.SesionUtil;
import com.mycompany.wisebooks.util.ValidacionException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Filtro de todas las llamadas a la API (/api/...).
 *
 * Asi los controllers quedan simples (sin try/catch): si un service lanza
 * una excepcion, aqui se convierte en un codigo HTTP con el mensaje en JSON
 * {"error": "..."} para que el JS lo muestre:
 *   400  ValidacionException (dato invalido o regla de negocio) o JSON mal escrito
 *   401  PermisoException sin sesion iniciada
 *   403  PermisoException con sesion (el rol no tiene permiso)
 *   500  cualquier otro error (el detalle va al log)
 *
 * Ademas, POST/PUT/DELETE deben enviar Content-Type: application/json.
 * Un formulario de otra pagina web no puede enviar ese tipo, asi nadie puede
 * usar la sesion abierta de un bibliotecario desde un sitio ajeno (CSRF).
 */
@WebFilter("/api/*")
public class ApiFilter implements Filter {

    private static final Logger LOG = Logger.getLogger(ApiFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        req.setCharacterEncoding("UTF-8");

        String metodo = req.getMethod();
        String tipo = req.getContentType();
        boolean modifica = metodo.equals("POST") || metodo.equals("PUT") || metodo.equals("DELETE");
        if (modifica && (tipo == null || !tipo.startsWith("application/json"))) {
            error(resp, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                    "Las peticiones que modifican datos deben enviar Content-Type: application/json.");
            return;
        }

        try {
            chain.doFilter(request, response);

        } catch (PermisoException e) {
            // Sin sesion = 401 (debe iniciar sesion); con sesion = 403 (no le alcanza el rol)
            int codigo = SesionUtil.usuario(req) == null
                    ? HttpServletResponse.SC_UNAUTHORIZED
                    : HttpServletResponse.SC_FORBIDDEN;
            error(resp, codigo, e.getMessage());

        } catch (ValidacionException e) {
            error(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());

        } catch (JsonProcessingException e) {
            error(resp, HttpServletResponse.SC_BAD_REQUEST, "Los datos enviados no tienen un formato JSON válido.");

        } catch (IllegalArgumentException e) {
            // Por ejemplo, ?id=abc (Long.parseLong) o ?estado=inventado (valueOf de un enum)
            error(resp, HttpServletResponse.SC_BAD_REQUEST, "Algún dato de la petición no es válido.");

        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Error en " + metodo + " " + req.getRequestURI(), e);
            error(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Ocurrió un error inesperado. Intenta de nuevo más tarde.");
        }
    }

    private void error(HttpServletResponse resp, int codigo, String mensaje) throws IOException {
        if (resp.isCommitted()) {
            return;
        }
        resp.resetBuffer();
        resp.setStatus(codigo);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        // Se escapan las comillas para que el JSON siga siendo valido
        String texto = (mensaje == null ? "Error" : mensaje).replace("\\", "\\\\").replace("\"", "\\\"");
        resp.getWriter().write("{\"error\":\"" + texto + "\"}");
    }
}
