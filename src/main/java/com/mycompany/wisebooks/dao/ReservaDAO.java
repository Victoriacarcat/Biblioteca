/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.dao;

import com.mycompany.wisebooks.entity.EstadoReserva;
import com.mycompany.wisebooks.entity.Reserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/*
 * Acceso a la tabla reservas. Recibe el EntityManager del service
 * (no abre transacciones ni contiene reglas de negocio).
 */
public class ReservaDAO {

    // Estados en los que una reserva sigue "viva"
    private static final List<EstadoReserva> VIVAS
            = List.of(EstadoReserva.PENDIENTE, EstadoReserva.APROBADA);

    public Reserva guardar(EntityManager em, Reserva reserva) {
        em.persist(reserva);
        return reserva;
    }

    public Optional<Reserva> buscarPorId(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(Reserva.class, id));
    }

    /* true si el usuario ya tiene una reserva viva de ese libro. */
    public boolean existeViva(EntityManager em, Long idUsuario, Long idLibro) {
        Long total = em.createQuery(
                "SELECT COUNT(r) FROM Reserva r "
                + "WHERE r.usuario.id = :usuario AND r.libro.id = :libro AND r.estado IN (:estados)",
                Long.class)
                .setParameter("usuario", idUsuario)
                .setParameter("libro", idLibro)
                .setParameter("estados", VIVAS)
                .getSingleResult();
        return total > 0;
    }

    /* Cuantas reservas vivas (pendientes o aprobadas) tiene el usuario. */
    public long contarVivasDeUsuario(EntityManager em, Long idUsuario) {
        return em.createQuery(
                "SELECT COUNT(r) FROM Reserva r "
                + "WHERE r.usuario.id = :usuario AND r.estado IN (:estados)",
                Long.class)
                .setParameter("usuario", idUsuario)
                .setParameter("estados", VIVAS)
                .getSingleResult();
    }

    /* true si OTRO usuario esta esperando (PENDIENTE) una copia de ese libro. */
    public boolean existePendienteDeOtro(EntityManager em, Long idLibro, Long idUsuarioExcluido) {
        Long total = em.createQuery(
                "SELECT COUNT(r) FROM Reserva r "
                + "WHERE r.libro.id = :libro AND r.estado = :estado AND r.usuario.id <> :usuario",
                Long.class)
                .setParameter("libro", idLibro)
                .setParameter("estado", EstadoReserva.PENDIENTE)
                .setParameter("usuario", idUsuarioExcluido)
                .getSingleResult();
        return total > 0;
    }

    /* Reservas aprobadas cuyo plazo de retiro ya paso (hay que vencerlas). */
    public List<Reserva> buscarAprobadasVencidas(EntityManager em, LocalDateTime ahora) {
        return em.createQuery(
                "SELECT r FROM Reserva r "
                + "WHERE r.estado = :estado AND r.fechaLimiteRetiro < :ahora",
                Reserva.class)
                .setParameter("estado", EstadoReserva.APROBADA)
                .setParameter("ahora", ahora)
                .getResultList();
    }

    /* Todas las reservas de un usuario, la mas reciente primero. */
    public List<Reserva> listarPorUsuario(EntityManager em, Long idUsuario) {
        return em.createQuery(
                "SELECT r FROM Reserva r JOIN FETCH r.libro JOIN FETCH r.usuario "
                + "WHERE r.usuario.id = :usuario ORDER BY r.fechaReserva DESC",
                Reserva.class)
                .setParameter("usuario", idUsuario)
                .getResultList();
    }

    /* Reservas de todos (para el personal). Si estado es null, trae todas. */
    public List<Reserva> listar(EntityManager em, EstadoReserva estado) {
        String jpql = "SELECT r FROM Reserva r JOIN FETCH r.libro JOIN FETCH r.usuario"
                + (estado == null ? "" : " WHERE r.estado = :estado")
                + " ORDER BY r.fechaReserva DESC";
        TypedQuery<Reserva> consulta = em.createQuery(jpql, Reserva.class);
        if (estado != null) {
            consulta.setParameter("estado", estado);
        }
        return consulta.getResultList();
    }
}
