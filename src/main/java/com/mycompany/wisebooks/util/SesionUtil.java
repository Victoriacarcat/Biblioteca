package com.mycompany.wisebooks.util;

import com.mycompany.wisebooks.dto.UsuarioDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/*
 * Lee al usuario que inicio sesion (lo guarda LoginController en el atributo
 * "usuario"). Devuelve null si es un visitante: los services ya responden
 * "Debes iniciar sesion." cuando la accion lo exige.
 */
public final class SesionUtil {

    public static final String ATRIBUTO_USUARIO = "usuario";

    private SesionUtil() {
    }

    public static UsuarioDTO usuario(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (UsuarioDTO) session.getAttribute(ATRIBUTO_USUARIO);
    }
}
