/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.dao;

import com.mycompany.wisebooks.entity.AccionAuditoria;
import com.mycompany.wisebooks.entity.Auditoria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

/*
 * Acceso a la tabla auditoria. Recibe el EntityManager del service, para que
 * el registro de auditoria se guarde en la MISMA transaccion que la accion
 * que lo origina: o se guardan las dos cosas, o ninguna.
 */
public class AuditoriaDAO {

    /* Marca el registro para guardarse (se confirma al terminar la transaccion). */
    public Auditoria guardar(EntityManager em, Auditoria auditoria) {
        em.persist(auditoria);
        return auditoria;
    }

    /*
     * Bitacora para el administrador, la mas reciente primero. Todos los
     * filtros son opcionales (null = no filtra). "limite" evita traer miles
     * de filas de golpe.
     */
    public List<Auditoria> listar(EntityManager em, Long idUsuario, AccionAuditoria accion,
            LocalDateTime desde, LocalDateTime hasta, int limite) {
        StringBuilder jpql = new StringBuilder(
                "SELECT a FROM Auditoria a LEFT JOIN FETCH a.usuario WHERE 1 = 1");
        if (idUsuario != null) {
            jpql.append(" AND a.usuario.id = :usuario");
        }
        if (accion != null) {
            jpql.append(" AND a.accion = :accion");
        }
        if (desde != null) {
            jpql.append(" AND a.fecha >= :desde");
        }
        if (hasta != null) {
            jpql.append(" AND a.fecha < :hasta");
        }
        jpql.append(" ORDER BY a.fecha DESC");

        TypedQuery<Auditoria> consulta = em.createQuery(jpql.toString(), Auditoria.class)
                .setMaxResults(limite);
        if (idUsuario != null) {
            consulta.setParameter("usuario", idUsuario);
        }
        if (accion != null) {
            consulta.setParameter("accion", accion);
        }
        if (desde != null) {
            consulta.setParameter("desde", desde);
        }
        if (hasta != null) {
            consulta.setParameter("hasta", hasta);
        }
        return consulta.getResultList();
    }

    /* Historial de un registro puntual, por ejemplo entidad "Libro" con id 5. */
    public List<Auditoria> listarPorRegistro(EntityManager em, String entidad, Long idRegistro) {
        return em.createQuery(
                "SELECT a FROM Auditoria a LEFT JOIN FETCH a.usuario "
                + "WHERE a.entidad = :entidad AND a.idRegistro = :id ORDER BY a.fecha DESC",
                Auditoria.class)
                .setParameter("entidad", entidad)
                .setParameter("id", idRegistro)
                .getResultList();
    }
}
