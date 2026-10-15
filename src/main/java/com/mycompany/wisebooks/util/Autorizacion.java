/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.util;

import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.EstadoUsuario;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.entity.Usuario;
import jakarta.persistence.EntityManager;

/*
 * Comprueba quien esta actuando y si su rol lo permite.
 * Vuelve a leer al usuario de la base: asi, si lo suspendieron o le cambiaron
 * el rol despues de iniciar sesion, el cambio se respeta de inmediato.
 */
public final class Autorizacion {

    private Autorizacion() {
    }

    /*
     * Exige que el actor este activo y tenga uno de los roles permitidos.
     * Devuelve el Usuario (entidad) para usarlo en la auditoria.
     */
    public static Usuario exigirRol(EntityManager em, UsuarioDTO actor, Rol... permitidos) {
        if (actor == null || actor.getId() == null) {
            throw new PermisoException("Debes iniciar sesión.");
        }
        Usuario usuario = em.find(Usuario.class, actor.getId());
        if (usuario == null || usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new PermisoException("Tu cuenta no está activa.");
        }
        for (Rol rol : permitidos) {
            if (usuario.getRol() == rol) {
                return usuario;
            }
        }
        throw new PermisoException("No tienes permiso para realizar esta acción.");
    }

    /*
     * Para acciones propias del lector (reservar, ver sus prestamos...).
     * Acepta cuentas ACTIVO y SUSPENDIDO (un suspendido todavia puede reservar,
     * con limite) y rechaza INACTIVO. Cada service decide que puede hacer
     * cada estado. Devuelve el Usuario (entidad).
     */
    public static Usuario exigirLector(EntityManager em, UsuarioDTO actor) {
        if (actor == null || actor.getId() == null) {
            throw new PermisoException("Debes iniciar sesión.");
        }
        Usuario usuario = em.find(Usuario.class, actor.getId());
        if (usuario == null || usuario.getEstado() == EstadoUsuario.INACTIVO) {
            throw new PermisoException("Tu cuenta no está activa.");
        }
        if (usuario.getRol() != Rol.LECTOR) {
            throw new PermisoException("Esta acción es solo para lectores.");
        }
        return usuario;
    }

    /* true si es bibliotecario o administrador (para consultas, sin ir a la base). */
    public static boolean esPersonal(UsuarioDTO actor) {
        return actor != null
                && (actor.getRol() == Rol.BIBLIOTECARIO || actor.getRol() == Rol.ADMINISTRADOR);
    }
}