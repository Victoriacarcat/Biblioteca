/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.service;

import com.mycompany.wisebooks.dao.AuditoriaDAO;
import com.mycompany.wisebooks.dto.AuditoriaDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.AccionAuditoria;
import com.mycompany.wisebooks.entity.Auditoria;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.entity.Usuario;
import com.mycompany.wisebooks.util.Autorizacion;
import com.mycompany.wisebooks.util.Texto;
import com.mycompany.wisebooks.util.Transaccion;
import com.mycompany.wisebooks.util.ValidacionException;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

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

    // Tope de filas por consulta, para no traer toda la bitacora de golpe
    private static final int MAX_RESULTADOS = 500;

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

    // Consultas (solo administrador)

    /*
     * Bitacora, la mas reciente primero. Filtros opcionales (null = no filtra).
     * "hasta" incluye todo ese dia. Trae como maximo MAX_RESULTADOS filas.
     */
    public List<AuditoriaDTO> listar(UsuarioDTO actor, Long idUsuario, AccionAuditoria accion,
            LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new ValidacionException("La fecha inicial no puede ser posterior a la final.");
        }
        return Transaccion.consultar(em -> {
            Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR);
            return auditoriaDAO.listar(em, idUsuario, accion,
                    desde == null ? null : desde.atStartOfDay(),
                    hasta == null ? null : hasta.plusDays(1).atStartOfDay(),
                    MAX_RESULTADOS).stream()
                    .map(AuditoriaDTO::desde)
                    .toList();
        });
    }

    /* Historial de un registro puntual, por ejemplo ("Libro", 5). */
    public List<AuditoriaDTO> historialDe(UsuarioDTO actor, String entidad, Long idRegistro) {
        return Transaccion.consultar(em -> {
            Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR);
            return auditoriaDAO.listarPorRegistro(em, entidad, idRegistro).stream()
                    .map(AuditoriaDTO::desde)
                    .toList();
        });
    }
}
