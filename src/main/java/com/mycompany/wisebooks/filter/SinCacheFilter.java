package com.mycompany.wisebooks.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
 * Filtro de todas las paginas (/FrontEnd/...).
 *
 * Le pide al navegador que no guarde las paginas en su cache. Asi, despues de
 * cerrar sesion, el boton "Atras" no puede mostrar una pagina guardada con
 * los datos de la sesion anterior: el navegador tiene que pedirla de nuevo
 * al servidor, que ya no tiene la sesion.
 *
 *   Cache-Control: no-store   no guardar la pagina (navegadores actuales)
 *   Pragma: no-cache          lo mismo para navegadores antiguos (HTTP/1.0)
 *   Expires: 0                la pagina ya esta "vencida"
 *
 * No aplica a /Recursos/... (JS, CSS, imagenes): no tienen datos personales
 * y conviene que el navegador los guarde para que las paginas carguen rapido.
 */
@WebFilter("/FrontEnd/*")
public class SinCacheFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse resp = (HttpServletResponse) response;
        // Se ponen antes de generar la pagina: despues ya podrian estar enviados
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        chain.doFilter(request, response);
    }
}
