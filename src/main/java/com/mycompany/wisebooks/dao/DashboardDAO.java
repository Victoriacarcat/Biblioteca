package com.mycompany.wisebooks.dao;

import com.mycompany.wisebooks.entity.EstadoPrestamo;
import com.mycompany.wisebooks.entity.EstadoReserva;
import com.mycompany.wisebooks.entity.EstadoSancion;
import com.mycompany.wisebooks.entity.EstadoUsuario;
import com.mycompany.wisebooks.entity.Rol;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/*
 * Conteos para el dashboard. Solo lectura: casi todos los metodos son un
 * COUNT o un SUM, asi no se cargan filas completas en memoria. La excepcion
 * es fechasDeDevueltos, que trae solo tres columnas de fechas.
 * Recibe el EntityManager del service.
 */
public class DashboardDAO {

    public long contarLibrosActivos(EntityManager em) {
        return em.createQuery("SELECT COUNT(l) FROM Libro l WHERE l.activo = true", Long.class)
                .getSingleResult();
    }

    /* Copias en la biblioteca: [0] = total, [1] = disponibles (solo libros activos). */
    public long[] sumarCopias(EntityManager em) {
        Object[] fila = em.createQuery(
                "SELECT COALESCE(SUM(l.stockTotal), 0), COALESCE(SUM(l.stockDisponible), 0) "
                + "FROM Libro l WHERE l.activo = true", Object[].class)
                .getSingleResult();
        return new long[]{((Number) fila[0]).longValue(), ((Number) fila[1]).longValue()};
    }

    public long contarLibrosCreadosDesde(EntityManager em, LocalDateTime desde) {
        return em.createQuery(
                "SELECT COUNT(l) FROM Libro l WHERE l.activo = true AND l.creadoEn >= :desde", Long.class)
                .setParameter("desde", desde)
                .getSingleResult();
    }

    public long contarCategoriasActivas(EntityManager em) {
        return em.createQuery("SELECT COUNT(c) FROM Categoria c WHERE c.activo = true", Long.class)
                .getSingleResult();
    }

    public long contarLectoresActivos(EntityManager em) {
        return em.createQuery(
                "SELECT COUNT(u) FROM Usuario u WHERE u.rol = :rol AND u.estado = :estado", Long.class)
                .setParameter("rol", Rol.LECTOR)
                .setParameter("estado", EstadoUsuario.ACTIVO)
                .getSingleResult();
    }

    public long contarPrestamosActivos(EntityManager em) {
        return em.createQuery("SELECT COUNT(p) FROM Prestamo p WHERE p.estado = :estado", Long.class)
                .setParameter("estado", EstadoPrestamo.ACTIVO)
                .getSingleResult();
    }

    public long contarPrestamosVencidos(EntityManager em, LocalDateTime ahora) {
        return em.createQuery(
                "SELECT COUNT(p) FROM Prestamo p WHERE p.estado = :estado AND p.fechaLimite < :ahora", Long.class)
                .setParameter("estado", EstadoPrestamo.ACTIVO)
                .setParameter("ahora", ahora)
                .getSingleResult();
    }

    public long contarReservasPendientes(EntityManager em) {
        return em.createQuery("SELECT COUNT(r) FROM Reserva r WHERE r.estado = :estado", Long.class)
                .setParameter("estado", EstadoReserva.PENDIENTE)
                .getSingleResult();
    }

    public long contarSancionesPendientes(EntityManager em) {
        return em.createQuery("SELECT COUNT(s) FROM Sancion s WHERE s.estado = :estado", Long.class)
                .setParameter("estado", EstadoSancion.PENDIENTE)
                .getSingleResult();
    }

    /* Total en COP que se debe a la biblioteca por sanciones pendientes. */
    public BigDecimal sumarSancionesPendientes(EntityManager em) {
        BigDecimal total = em.createQuery(
                "SELECT SUM(s.importe) FROM Sancion s WHERE s.estado = :estado", BigDecimal.class)
                .setParameter("estado", EstadoSancion.PENDIENTE)
                .getSingleResult();
        return total == null ? BigDecimal.ZERO : total;
    }

    // ---- Circulacion ----

    /* Copias apartadas: cada reserva APROBADA (sin retirar) aparta una copia. */
    public long contarReservasAprobadas(EntityManager em) {
        return em.createQuery("SELECT COUNT(r) FROM Reserva r WHERE r.estado = :estado", Long.class)
                .setParameter("estado", EstadoReserva.APROBADA)
                .getSingleResult();
    }

    /* Prestamos entregados entre dos momentos [desde, hasta). Los anulados no cuentan (fueron errores). */
    public long contarPrestamosEntre(EntityManager em, LocalDateTime desde, LocalDateTime hasta) {
        return em.createQuery(
                "SELECT COUNT(p) FROM Prestamo p WHERE p.estado <> :anulado "
                + "AND p.fechaPrestamo >= :desde AND p.fechaPrestamo < :hasta", Long.class)
                .setParameter("anulado", EstadoPrestamo.ANULADO)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getSingleResult();
    }

    /* Libros devueltos entre dos momentos [desde, hasta). */
    public long contarDevolucionesEntre(EntityManager em, LocalDateTime desde, LocalDateTime hasta) {
        return em.createQuery(
                "SELECT COUNT(p) FROM Prestamo p WHERE p.estado = :devuelto "
                + "AND p.fechaDevolucion >= :desde AND p.fechaDevolucion < :hasta", Long.class)
                .setParameter("devuelto", EstadoPrestamo.DEVUELTO)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getSingleResult();
    }

    /* Total de prestamos registrados, sin contar los anulados. */
    public long contarPrestamosNoAnulados(EntityManager em) {
        return em.createQuery("SELECT COUNT(p) FROM Prestamo p WHERE p.estado <> :anulado", Long.class)
                .setParameter("anulado", EstadoPrestamo.ANULADO)
                .getSingleResult();
    }

    /*
     * Categorias con mas prestamos (sin contar anulados), de mayor a menor.
     * Cada fila es [nombre (String), cantidad (Long)]. Trae como maximo "limite".
     */
    public List<Object[]> categoriasMasPedidas(EntityManager em, int limite) {
        return em.createQuery(
                "SELECT c.nombre, COUNT(p) FROM Prestamo p JOIN p.libro l JOIN l.categoria c "
                + "WHERE p.estado <> :anulado "
                + "GROUP BY c.nombre ORDER BY COUNT(p) DESC, c.nombre", Object[].class)
                .setParameter("anulado", EstadoPrestamo.ANULADO)
                .setMaxResults(limite)
                .getResultList();
    }

    /*
     * Fechas de los prestamos ya devueltos: cada fila es
     * [fechaPrestamo, fechaLimite, fechaDevolucion] (LocalDateTime).
     * El service calcula con ellas la duracion media y el porcentaje devuelto
     * a tiempo (JPQL no tiene una forma estandar de restar fechas).
     */
    public List<Object[]> fechasDeDevueltos(EntityManager em) {
        return em.createQuery(
                "SELECT p.fechaPrestamo, p.fechaLimite, p.fechaDevolucion FROM Prestamo p "
                + "WHERE p.estado = :devuelto", Object[].class)
                .setParameter("devuelto", EstadoPrestamo.DEVUELTO)
                .getResultList();
    }

    /* Libros que el lector devolvio desde una fecha (por ejemplo, "leidos este ano"). */
    public long contarDevueltosDeUsuarioDesde(EntityManager em, Long idUsuario, LocalDateTime desde) {
        return em.createQuery(
                "SELECT COUNT(p) FROM Prestamo p WHERE p.usuario.id = :usuario "
                + "AND p.estado = :estado AND p.fechaDevolucion >= :desde", Long.class)
                .setParameter("usuario", idUsuario)
                .setParameter("estado", EstadoPrestamo.DEVUELTO)
                .setParameter("desde", desde)
                .getSingleResult();
    }
}
