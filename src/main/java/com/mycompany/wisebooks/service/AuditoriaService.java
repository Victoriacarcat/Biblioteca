/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.service;

import com.mycompany.wisebooks.dao.AuditoriaDAO;
import com.mycompany.wisebooks.entity.AccionAuditoria;
import com.mycompany.wisebooks.entity.Auditoria;
import com.mycompany.wisebooks.entity.Usuario;
import com.mycompany.wisebooks.util.Texto;
import jakarta.persistence.EntityManager;

/*
 * Anota en la bitacora quien hizo que y cuando.
 *
 * No abre su propia transaccion: usa el EntityManager del service que la llama.
 * Asi la accion y su registro de auditoria se guardan juntos (o ninguno).
 *
 * Uso dentro de otro service:
 *   auditoriaService.registrar(em, usuario, AccionAuditoria.CREAR, "Libro", libro.getId(), "...");
 */
public class AuditoriaService {

    // Deben coincidir con el largo de las columnas en la entidad Auditoria
    private static final int MAX_ENTIDAD = 50;
    private static final int MAX_DETALLE = 500;

    private final AuditoriaDAO auditoriaDAO = new AuditoriaDAO();

    public void registrar(EntityManager em, Usuario actor, AccionAuditoria accion,
            String entidad, Long idRegistro, String detalle) {
        Auditoria registro = new Auditoria();
        registro.setUsuario(actor);
        registro.setAccion(accion);
        registro.setEntidad(Texto.recortar(entidad, MAX_ENTIDAD));
        registro.setIdRegistro(idRegistro);
        registro.setDetalle(Texto.recortar(detalle, MAX_DETALLE));
        auditoriaDAO.guardar(em, registro);
    }
}
