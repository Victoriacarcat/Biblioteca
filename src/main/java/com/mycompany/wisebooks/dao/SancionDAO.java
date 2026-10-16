package com.mycompany.wisebooks.dao;

import com.mycompany.wisebooks.entity.EstadoSancion;
import com.mycompany.wisebooks.entity.Sancion;
import com.mycompany.wisebooks.entity.TipoSancion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/*
 * Acceso a la tabla sanciones. Recibe el EntityManager del service
 * (no abre transacciones ni contiene reglas de negocio).
 */
public class SancionDAO {

    public Sancion guardar(EntityManager em, Sancion sancion) {
        em.persist(sancion);
        return sancion;
    }

    public Optional<Sancion> buscarPorId(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(Sancion.class, id));
    }

    /*
     * true si ese prestamo ya tiene una sancion de ese tipo
     * (la tabla no permite dos: uk_sanciones_prestamo_tipo).
     */
    public boolean existeDelPrestamo(EntityManager em, Long idPrestamo, TipoSancion tipo) {
        Long total = em.createQuery(
                "SELECT COUNT(s) FROM Sancion s WHERE s.prestamo.id = :prestamo AND s.tipo = :tipo",
                Long.class)
                .setParameter("prestamo", idPrestamo)
                .setParameter("tipo", tipo)
                .getSingleResult();
        return total > 0;
    }

    /* Total en COP que el usuario debe por sanciones PENDIENTES (0 si no debe nada). */
    public BigDecimal sumarPendientesDeUsuario(EntityManager em, Long idUsuario) {
        BigDecimal total = em.createQuery(
                "SELECT SUM(s.importe) FROM Sancion s WHERE s.usuario.id = :usuario AND s.estado = :estado",
                BigDecimal.class)
                .setParameter("usuario", idUsuario)
                .setParameter("estado", EstadoSancion.PENDIENTE)
                .getSingleResult();
        return total == null ? BigDecimal.ZERO : total;
    }

    /* Cuantas sanciones PENDIENTES tiene el usuario. */
    public long contarPendientesDeUsuario(EntityManager em, Long idUsuario) {
        return em.createQuery(
                "SELECT COUNT(s) FROM Sancion s WHERE s.usuario.id = :usuario AND s.estado = :estado",
                Long.class)
                .setParameter("usuario", idUsuario)
                .setParameter("estado", EstadoSancion.PENDIENTE)
                .getSingleResult();
    }

    /* Todas las sanciones de un usuario, la mas reciente primero. */
    public List<Sancion> listarPorUsuario(EntityManager em, Long idUsuario) {
        return em.createQuery(
                "SELECT s FROM Sancion s JOIN FETCH s.usuario "
                + "LEFT JOIN FETCH s.prestamo p LEFT JOIN FETCH p.libro "
                + "WHERE s.usuario.id = :usuario ORDER BY s.fechaEmision DESC",
                Sancion.class)
                .setParameter("usuario", idUsuario)
                .getResultList();
    }

    /* Sanciones de todos (para el personal). Si estado es null, trae todas. */
    public List<Sancion> listar(EntityManager em, EstadoSancion estado) {
        String jpql = "SELECT s FROM Sancion s JOIN FETCH s.usuario "
                + "LEFT JOIN FETCH s.prestamo p LEFT JOIN FETCH p.libro"
                + (estado == null ? "" : " WHERE s.estado = :estado")
                + " ORDER BY s.fechaEmision DESC";
        TypedQuery<Sancion> consulta = em.createQuery(jpql, Sancion.class);
        if (estado != null) {
            consulta.setParameter("estado", estado);
        }
        return consulta.getResultList();
    }
}
