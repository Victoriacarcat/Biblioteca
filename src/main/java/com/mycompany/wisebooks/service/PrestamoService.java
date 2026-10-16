package com.mycompany.wisebooks.service;

import com.mycompany.wisebooks.dao.PrestamoDAO;
import com.mycompany.wisebooks.dao.ReservaDAO;
import com.mycompany.wisebooks.dto.PrestamoDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.AccionAuditoria;
import com.mycompany.wisebooks.entity.EstadoPrestamo;
import com.mycompany.wisebooks.entity.EstadoReserva;
import com.mycompany.wisebooks.entity.EstadoUsuario;
import com.mycompany.wisebooks.entity.Libro;
import com.mycompany.wisebooks.entity.Prestamo;
import com.mycompany.wisebooks.entity.Reserva;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.entity.Sancion;
import com.mycompany.wisebooks.entity.Usuario;
import com.mycompany.wisebooks.util.Autorizacion;
import com.mycompany.wisebooks.util.PermisoException;
import com.mycompany.wisebooks.util.ReglasBiblioteca;
import com.mycompany.wisebooks.util.Texto;
import com.mycompany.wisebooks.util.Transaccion;
import com.mycompany.wisebooks.util.ValidacionException;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/*
 * Reglas de negocio de los prestamos.
 *
 * Quien puede hacer que:
 *   - Ver los propios y renovar el propio: el LECTOR.
 *   - Entregar, devolver, marcar como perdido y ver todos: ADMINISTRADOR y BIBLIOTECARIO.
 *   - Anular (prestamo registrado por error): solo el ADMINISTRADOR.
 *
 * Ciclo de un prestamo:
 *   (entrega) --> ACTIVO --devolver--> DEVUELTO (si fue tarde, se genera la MORA)
 *                 ACTIVO --perdido---> PERDIDO (sale del inventario y se genera la sancion)
 *                 ACTIVO --anular----> ANULADO (la copia vuelve al stock)
 *   "Vencido" no es un estado: es un ACTIVO con la fecha limite ya pasada.
 *
 * Para recibir un libro prestado el lector debe estar ACTIVO (un suspendido
 * no puede), no tener prestamos vencidos sin devolver y no pasar de
 * MAX_PRESTAMOS_ACTIVOS.
 */
public class PrestamoService {

    private static final int MAX_DETALLE = 255;

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final ReservaService reservaService = new ReservaService();
    private final SancionService sancionService = new SancionService();
    private final AuditoriaService auditoriaService = new AuditoriaService();

    // Consultas

    /* Los prestamos del lector que inicio sesion (activos e historicos), el mas reciente primero. */
    public List<PrestamoDTO> misPrestamos(UsuarioDTO actor) {
        return Transaccion.consultar(em -> {
            Usuario lector = Autorizacion.exigirLector(em, actor);
            return prestamoDAO.listarPorUsuario(em, lector.getId()).stream()
                    .map(PrestamoDTO::desde)
                    .toList();
        });
    }

    /* Todos los prestamos (solo personal). Si estado es null, trae todos. */
    public List<PrestamoDTO> listar(UsuarioDTO actor, EstadoPrestamo estado) {
        return Transaccion.consultar(em -> {
            Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR, Rol.BIBLIOTECARIO);
            return prestamoDAO.listar(em, estado).stream()
                    .map(PrestamoDTO::desde)
                    .toList();
        });
    }

    /* Prestamos activos con la fecha limite pasada, los mas atrasados primero (solo personal). */
    public List<PrestamoDTO> listarVencidos(UsuarioDTO actor) {
        return Transaccion.consultar(em -> {
            Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR, Rol.BIBLIOTECARIO);
            return prestamoDAO.listarVencidos(em, LocalDateTime.now()).stream()
                    .map(PrestamoDTO::desde)
                    .toList();
        });
    }

    // Entrega del libro (personal)

    /*
     * Entrega el libro de una reserva APROBADA. La copia ya estaba apartada
     * desde la aprobacion, asi que el stock no vuelve a bajar.
     * La reserva pasa a RETIRADA y queda ligada al prestamo.
     */
    public PrestamoDTO entregarReserva(UsuarioDTO actor, Long idReserva) {
        reservaService.vencerVencidas();
        return Transaccion.ejecutar(em -> {
            Usuario personal = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR, Rol.BIBLIOTECARIO);

            Reserva reserva = (idReserva == null) ? null : reservaDAO.buscarPorId(em, idReserva).orElse(null);
            if (reserva == null) {
                throw new ValidacionException("La reserva no existe.");
            }
            if (reserva.getEstado() != EstadoReserva.APROBADA) {
                throw new ValidacionException("Solo se entregan reservas aprobadas (estado: "
                        + reserva.getEstado().name().toLowerCase() + ").");
            }
            Usuario lector = reserva.getUsuario();
            exigirPuedeRecibir(em, lector, reserva.getLibro());

            reserva.setEstado(EstadoReserva.RETIRADA);
            Prestamo prestamo = nuevoPrestamo(lector, reserva.getLibro(), personal);
            prestamo.setReserva(reserva);
            prestamoDAO.guardar(em, prestamo);

            auditoriaService.registrar(em, personal, AccionAuditoria.CREAR, "Prestamo", prestamo.getId(),
                    "Entregó \"" + reserva.getLibro().getTitulo() + "\" a " + lector.getCorreo()
                    + " (reserva #" + reserva.getId() + ").");
            return PrestamoDTO.desde(prestamo);
        });
    }

    /*
     * Prestamo directo en el mostrador, sin reserva previa. Toma una copia
     * disponible. Si otro lector esta esperando ese libro (reserva PENDIENTE),
     * se le da prioridad: primero hay que atender su reserva.
     */
    public PrestamoDTO entregarDirecto(UsuarioDTO actor, Long idUsuario, Long idLibro) {
        reservaService.vencerVencidas();
        return Transaccion.ejecutar(em -> {
            Usuario personal = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR, Rol.BIBLIOTECARIO);

            Usuario lector = (idUsuario == null) ? null : em.find(Usuario.class, idUsuario);
            if (lector == null) {
                throw new ValidacionException("El usuario no existe.");
            }
            Libro libro = (idLibro == null) ? null : em.find(Libro.class, idLibro);
            if (libro == null || !libro.isActivo()) {
                throw new ValidacionException("El libro no existe.");
            }

            exigirPuedeRecibir(em, lector, libro);
            if (reservaDAO.existeViva(em, lector.getId(), libro.getId())) {
                throw new ValidacionException("Este lector tiene una reserva de este libro: "
                        + "apruébala y entrégalo desde la reserva.");
            }
            if (reservaDAO.existePendienteDeOtro(em, libro.getId(), lector.getId())) {
                throw new ValidacionException("Otro lector está esperando este libro (reserva pendiente). "
                        + "Atiende primero su reserva.");
            }
            if (libro.getStockDisponible() < 1) {
                throw new ValidacionException("No hay copias disponibles de este libro.");
            }

            libro.setStockDisponible(libro.getStockDisponible() - 1);
            Prestamo prestamo = nuevoPrestamo(lector, libro, personal);
            prestamoDAO.guardar(em, prestamo);

            auditoriaService.registrar(em, personal, AccionAuditoria.CREAR, "Prestamo", prestamo.getId(),
                    "Prestó \"" + libro.getTitulo() + "\" a " + lector.getCorreo() + ".");
            return PrestamoDTO.desde(prestamo);
        });
    }

    // Renovacion (lector)

    /*
     * El lector renueva SU prestamo: suma DIAS_RENOVACION a la fecha limite.
     * No se puede si esta vencido, si ya llego a MAX_RENOVACIONES, si la
     * cuenta esta suspendida o si otro lector esta esperando el libro.
     */
    public PrestamoDTO renovar(UsuarioDTO actor, Long idPrestamo) {
        return Transaccion.ejecutar(em -> {
            Usuario lector = Autorizacion.exigirLector(em, actor);
            Prestamo prestamo = exigirActivo(em, idPrestamo);

            if (!prestamo.getUsuario().getId().equals(lector.getId())) {
                throw new PermisoException("Este préstamo no es tuyo.");
            }
            if (lector.getEstado() != EstadoUsuario.ACTIVO) {
                throw new ValidacionException("Tu cuenta está suspendida: no puedes renovar préstamos.");
            }
            if (prestamo.getFechaLimite().isBefore(LocalDateTime.now())) {
                throw new ValidacionException("El préstamo está vencido: primero debes devolver el libro.");
            }
            if (prestamo.getRenovaciones() >= ReglasBiblioteca.MAX_RENOVACIONES) {
                throw new ValidacionException("Ya renovaste este préstamo "
                        + ReglasBiblioteca.MAX_RENOVACIONES + " veces, que es el máximo.");
            }
            if (reservaDAO.existePendienteDeOtro(em, prestamo.getLibro().getId(), lector.getId())) {
                throw new ValidacionException("Otro lector reservó este libro: no se puede renovar.");
            }

            prestamo.setFechaLimite(prestamo.getFechaLimite().plusDays(ReglasBiblioteca.DIAS_RENOVACION));
            prestamo.setRenovaciones(prestamo.getRenovaciones() + 1);

            auditoriaService.registrar(em, lector, AccionAuditoria.ACTUALIZAR, "Prestamo", prestamo.getId(),
                    "Renovó \"" + prestamo.getLibro().getTitulo() + "\" (renovación "
                    + prestamo.getRenovaciones() + " de " + ReglasBiblioteca.MAX_RENOVACIONES + ").");
            return PrestamoDTO.desde(prestamo);
        });
    }

    // Cierre del prestamo (personal)

    /*
     * Registra la devolucion: la copia vuelve al stock y, si se devolvio
     * despues de la fecha limite, se genera la sancion por MORA (en la misma
     * transaccion: o se guardan las dos cosas, o ninguna).
     * Si el libro vuelve danado, el personal registra aparte la sancion con
     * SancionService.registrar(..., LIBRO_DANADO, ...).
     */
    public PrestamoDTO devolver(UsuarioDTO actor, Long idPrestamo) {
        return Transaccion.ejecutar(em -> {
            Usuario personal = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR, Rol.BIBLIOTECARIO);
            Prestamo prestamo = exigirActivo(em, idPrestamo);

            LocalDateTime ahora = LocalDateTime.now();
            Libro libro = prestamo.getLibro();
            libro.setStockDisponible(libro.getStockDisponible() + 1);
            prestamo.setEstado(EstadoPrestamo.DEVUELTO);
            prestamo.setFechaDevolucion(ahora);
            prestamo.setRecibidoPor(personal);

            Sancion mora = sancionService.generarMoraSiCorresponde(em, prestamo, ahora);

            auditoriaService.registrar(em, personal, AccionAuditoria.CAMBIO_ESTADO, "Prestamo", prestamo.getId(),
                    "Recibió \"" + libro.getTitulo() + "\" de " + prestamo.getUsuario().getCorreo()
                    + (mora == null ? "." : ", con " + mora.getDiasRetraso() + " días de retraso."));
            return PrestamoDTO.desde(prestamo);
        });
    }

    /*
     * El lector perdio el libro: la copia sale del inventario (baja el stock
     * total) y se le cobra el valor indicado como sancion LIBRO_PERDIDO.
     */
    public PrestamoDTO marcarPerdido(UsuarioDTO actor, Long idPrestamo, BigDecimal importe, String detalle) {
        return Transaccion.ejecutar(em -> {
            Usuario personal = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR, Rol.BIBLIOTECARIO);
            Prestamo prestamo = exigirActivo(em, idPrestamo);

            // La copia estaba prestada (no cuenta en disponibles): solo baja el total
            Libro libro = prestamo.getLibro();
            libro.setStockTotal(libro.getStockTotal() - 1);
            prestamo.setEstado(EstadoPrestamo.PERDIDO);
            prestamo.setRecibidoPor(personal);

            Sancion sancion = sancionService.generarPorPerdida(em, prestamo, importe, personal, detalle);

            auditoriaService.registrar(em, personal, AccionAuditoria.CAMBIO_ESTADO, "Prestamo", prestamo.getId(),
                    "Marcó como perdido \"" + libro.getTitulo() + "\" de " + prestamo.getUsuario().getCorreo()
                    + ". Sanción de $" + sancion.getImporte().toPlainString() + ".");
            return PrestamoDTO.desde(prestamo);
        });
    }

    /* Anula un prestamo registrado por error: la copia vuelve al stock. Solo administrador. */
    public PrestamoDTO anular(UsuarioDTO actor, Long idPrestamo, String motivo) {
        return Transaccion.ejecutar(em -> {
            Usuario admin = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR);
            Prestamo prestamo = exigirActivo(em, idPrestamo);
            String motivoLimpio = Texto.exigir(motivo, "El motivo de la anulación", MAX_DETALLE);

            Libro libro = prestamo.getLibro();
            libro.setStockDisponible(libro.getStockDisponible() + 1);
            prestamo.setEstado(EstadoPrestamo.ANULADO);

            auditoriaService.registrar(em, admin, AccionAuditoria.CAMBIO_ESTADO, "Prestamo", prestamo.getId(),
                    "Anuló el préstamo de \"" + libro.getTitulo() + "\" a " + prestamo.getUsuario().getCorreo()
                    + ". Motivo: " + motivoLimpio);
            return PrestamoDTO.desde(prestamo);
        });
    }

    // Ayudas internas

    /* Comprueba que el lector puede llevarse un libro ahora mismo. */
    private void exigirPuedeRecibir(EntityManager em, Usuario lector, Libro libro) {
        if (lector.getRol() != Rol.LECTOR) {
            throw new ValidacionException("Solo se prestan libros a lectores.");
        }
        if (lector.getEstado() != EstadoUsuario.ACTIVO) {
            throw new ValidacionException("La cuenta del lector no está activa ("
                    + lector.getEstado().name().toLowerCase() + "): no puede recibir préstamos.");
        }
        if (prestamoDAO.contarVencidosDeUsuario(em, lector.getId(), LocalDateTime.now()) > 0) {
            throw new ValidacionException("El lector tiene préstamos vencidos sin devolver.");
        }
        if (prestamoDAO.contarActivosDeUsuario(em, lector.getId()) >= ReglasBiblioteca.MAX_PRESTAMOS_ACTIVOS) {
            throw new ValidacionException("El lector ya tiene " + ReglasBiblioteca.MAX_PRESTAMOS_ACTIVOS
                    + " préstamos activos, que es el máximo.");
        }
        if (prestamoDAO.existeActivoDelLibro(em, lector.getId(), libro.getId())) {
            throw new ValidacionException("El lector ya tiene este libro prestado.");
        }
    }

    private Prestamo nuevoPrestamo(Usuario lector, Libro libro, Usuario personal) {
        LocalDateTime ahora = LocalDateTime.now();
        Prestamo prestamo = new Prestamo();
        prestamo.setUsuario(lector);
        prestamo.setLibro(libro);
        prestamo.setRegistradoPor(personal);
        prestamo.setFechaPrestamo(ahora);
        prestamo.setFechaLimite(ahora.plusDays(ReglasBiblioteca.DIAS_PRESTAMO));
        return prestamo;
    }

    private Prestamo exigirActivo(EntityManager em, Long idPrestamo) {
        Prestamo prestamo = (idPrestamo == null) ? null : prestamoDAO.buscarPorId(em, idPrestamo).orElse(null);
        if (prestamo == null) {
            throw new ValidacionException("El préstamo no existe.");
        }
        if (prestamo.getEstado() != EstadoPrestamo.ACTIVO) {
            throw new ValidacionException("Este préstamo ya está cerrado (estado: "
                    + prestamo.getEstado().name().toLowerCase() + ").");
        }
        return prestamo;
    }
}
