/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.dao;

import com.mycompany.wisebooks.entity.EstadoPrestamo;
import com.mycompany.wisebooks.entity.Prestamo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/*
 * Acceso a la tabla prestamos. Recibe el EntityManager del service
 * (no abre transacciones ni contiene reglas de negocio).
 */
public class PrestamoDAO {

    public Prestamo guardar(EntityManager em, Prestamo prestamo) {
        em.persist(prestamo);
        return prestamo;
    }

    public Optional<Prestamo> buscarPorId(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(Prestamo.class, id));
    }

    /* Cuantos prestamos ACTIVOS (incluye los vencidos sin devolver) tiene el usuario. */
    public long contarActivosDeUsuario(EntityManager em, Long idUsuario) {
        return em.createQuery(
                "SELECT COUNT(p) FROM Prestamo p WHERE p.usuario.id = :usuario AND p.estado = :estado",
                Long.class)
                .setParameter("usuario", idUsuario)
                .setParameter("estado", EstadoPrestamo.ACTIVO)
                .getSingleResult();
    }

    /* Cuantos prestamos ACTIVOS del usuario ya pasaron su fecha limite. */
    public long contarVencidosDeUsuario(EntityManager em, Long idUsuario, LocalDateTime ahora) {
        return em.createQuery(
                "SELECT COUNT(p) FROM Prestamo p "
                + "WHERE p.usuario.id = :usuario AND p.estado = :estado AND p.fechaLimite < :ahora",
                Long.class)
                .setParameter("usuario", idUsuario)
                .setParameter("estado", EstadoPrestamo.ACTIVO)
                .setParameter("ahora", ahora)
                .getSingleResult();
    }

    /* true si el usuario ya tiene prestado (activo) ese libro. */
    public boolean existeActivoDelLibro(EntityManager em, Long idUsuario, Long idLibro) {
        Long total = em.createQuery(
                "SELECT COUNT(p) FROM Prestamo p "
                + "WHERE p.usuario.id = :usuario AND p.libro.id = :libro AND p.estado = :estado",
                Long.class)
                .setParameter("usuario", idUsuario)
                .setParameter("libro", idLibro)
                .setParameter("estado", EstadoPrestamo.ACTIVO)
                .getSingleResult();
        return total > 0;
    }

    /* Todos los prestamos de un usuario, el mas reciente primero. */
    public List<Prestamo> listarPorUsuario(EntityManager em, Long idUsuario) {
        return em.createQuery(
                "SELECT p FROM Prestamo p JOIN FETCH p.libro JOIN FETCH p.usuario "
                + "WHERE p.usuario.id = :usuario ORDER BY p.fechaPrestamo DESC",
                Prestamo.class)
                .setParameter("usuario", idUsuario)
                .getResultList();
    }

    /* Prestamos de todos (para el personal). Si estado es null, trae todos. */
    public List<Prestamo> listar(EntityManager em, EstadoPrestamo estado) {
        String jpql = "SELECT p FROM Prestamo p JOIN FETCH p.libro JOIN FETCH p.usuario"
                + (estado == null ? "" : " WHERE p.estado = :estado")
                + " ORDER BY p.fechaPrestamo DESC";
        TypedQuery<Prestamo> consulta = em.createQuery(jpql, Prestamo.class);
        if (estado != null) {
            consulta.setParameter("estado", estado);
        }
        return consulta.getResultList();
    }

    /* Prestamos activos cuya fecha limite ya paso ("vencidos"), los mas atrasados primero. */
    public List<Prestamo> listarVencidos(EntityManager em, LocalDateTime ahora) {
        return em.createQuery(
                "SELECT p FROM Prestamo p JOIN FETCH p.libro JOIN FETCH p.usuario "
                + "WHERE p.estado = :estado AND p.fechaLimite < :ahora ORDER BY p.fechaLimite ASC",
                Prestamo.class)
                .setParameter("estado", EstadoPrestamo.ACTIVO)
                .setParameter("ahora", ahora)
                .getResultList();
    }
}
