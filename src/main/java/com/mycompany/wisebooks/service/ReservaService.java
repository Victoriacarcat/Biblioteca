/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.service;

import com.mycompany.wisebooks.dao.PrestamoDAO;
import com.mycompany.wisebooks.dao.ReservaDAO;
import com.mycompany.wisebooks.dto.ReservaDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.AccionAuditoria;
import com.mycompany.wisebooks.entity.EstadoReserva;
import com.mycompany.wisebooks.entity.EstadoUsuario;
import com.mycompany.wisebooks.entity.Libro;
import com.mycompany.wisebooks.entity.Reserva;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.entity.Usuario;
import com.mycompany.wisebooks.util.Autorizacion;
import com.mycompany.wisebooks.util.PermisoException;
import com.mycompany.wisebooks.util.ReglasBiblioteca;
import com.mycompany.wisebooks.util.Texto;
import com.mycompany.wisebooks.util.Transaccion;
import com.mycompany.wisebooks.util.ValidacionException;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

/*
 * Reglas de negocio de las reservas.
 *
 * Quien puede hacer que:
 *   - Reservar, cancelar la propia y ver las propias: el LECTOR (activo o suspendido).
 *   - Aprobar, rechazar y ver todas: ADMINISTRADOR y BIBLIOTECARIO.
 *
 * Ciclo de una reserva:
 *   PENDIENTE --aprobar--> APROBADA (se aparta una copia y corren 3 dias para retirarla)
 *   PENDIENTE --rechazar--> RECHAZADA
 *   PENDIENTE o APROBADA --cancelar--> CANCELADA (si estaba aprobada, la copia se libera)
 *   APROBADA --pasan 3 dias sin retirar--> VENCIDA (la copia se libera)
 *   APROBADA --entrega del libro--> RETIRADA (lo hace PrestamoService)
 *
 * El vencimiento no usa un proceso programado: se aplica al consultar o actuar
 * (ver vencerVencidas).
 */
public class ReservaService {

    private static final int MAX_OBSERVACIONES = 255;

    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final AuditoriaService auditoriaService = new AuditoriaService();

    // Consultas

    /* Las reservas del lector que inicio sesion, la mas reciente primero. */
    public List<ReservaDTO> misReservas(UsuarioDTO actor) {
        vencerVencidas();
        return Transaccion.consultar(em -> {
            Usuario lector = Autorizacion.exigirLector(em, actor);
            return reservaDAO.listarPorUsuario(em, lector.getId()).stream()
                    .map(ReservaDTO::desde)
                    .toList();
        });
    }

    /* Todas las reservas (solo personal). Si estado es null, trae todas. */
    public List<ReservaDTO> listar(UsuarioDTO actor, EstadoReserva estado) {
        vencerVencidas();
        return Transaccion.consultar(em -> {
            Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR, Rol.BIBLIOTECARIO);
            return reservaDAO.listar(em, estado).stream()
                    .map(ReservaDTO::desde)
                    .toList();
        });
    }

    // Acciones del lector


    /* Crea una reserva PENDIENTE. Se puede reservar aunque haya copias disponibles. */
    public ReservaDTO crear(UsuarioDTO actor, Long idLibro) {
        vencerVencidas();
        return Transaccion.ejecutar(em -> {
            Usuario lector = Autorizacion.exigirLector(em, actor);

            Libro libro = (idLibro == null) ? null : em.find(Libro.class, idLibro);
            if (libro == null || !libro.isActivo()) {
                throw new ValidacionException("El libro no existe.");
            }
            if (reservaDAO.existeViva(em, lector.getId(), libro.getId())) {
                throw new ValidacionException("Ya tienes una reserva activa de este libro.");
            }
            if (prestamoDAO.existeActivoDelLibro(em, lector.getId(), libro.getId())) {
                throw new ValidacionException("Ya tienes este libro prestado.");
            }
            // Un suspendido si puede reservar, pero con limite
            if (lector.getEstado() == EstadoUsuario.SUSPENDIDO
                    && reservaDAO.contarVivasDeUsuario(em, lector.getId())
                    >= ReglasBiblioteca.MAX_RESERVAS_SUSPENDIDO) {
                throw new ValidacionException("Tu cuenta está suspendida: solo puedes tener "
                        + ReglasBiblioteca.MAX_RESERVAS_SUSPENDIDO + " reservas activas.");
            }

            Reserva reserva = new Reserva();
            reserva.setUsuario(lector);
            reserva.setLibro(libro);
            reservaDAO.guardar(em, reserva);

            auditoriaService.registrar(em, lector, AccionAuditoria.CREAR, "Reserva", reserva.getId(),
                    "Reservó \"" + libro.getTitulo() + "\".");
            return ReservaDTO.desde(reserva);
        });
    }

    /* El lector cancela SU reserva (pendiente o aprobada). */
    public ReservaDTO cancelar(UsuarioDTO actor, Long idReserva) {
        vencerVencidas();
        return Transaccion.ejecutar(em -> {
            Usuario lector = Autorizacion.exigirLector(em, actor);
            Reserva reserva = exigirReserva(em, idReserva);

            if (!reserva.getUsuario().getId().equals(lector.getId())) {
                throw new PermisoException("Esta reserva no es tuya.");
            }
            if (reserva.getEstado() != EstadoReserva.PENDIENTE
                    && reserva.getEstado() != EstadoReserva.APROBADA) {
                throw new ValidacionException("Esta reserva ya no se puede cancelar (estado: "
                        + reserva.getEstado().name().toLowerCase() + ").");
            }

            cerrar(reserva, EstadoReserva.CANCELADA);

            auditoriaService.registrar(em, lector, AccionAuditoria.CAMBIO_ESTADO, "Reserva", reserva.getId(),
                    "Canceló su reserva de \"" + reserva.getLibro().getTitulo() + "\".");
            return ReservaDTO.desde(reserva);
        });
    }

    // Acciones del personal

    /* Aprueba una reserva PENDIENTE: aparta una copia y fija el plazo de retiro. */
    public ReservaDTO aprobar(UsuarioDTO actor, Long idReserva) {
        vencerVencidas();
        return Transaccion.ejecutar(em -> {
            Usuario personal = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR, Rol.BIBLIOTECARIO);
            Reserva reserva = exigirReserva(em, idReserva);

            if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
                throw new ValidacionException("Solo se pueden aprobar reservas pendientes.");
            }
            Libro libro = reserva.getLibro();
            if (!libro.isActivo()) {
                throw new ValidacionException("El libro está desactivado.");
            }
            if (libro.getStockDisponible() < 1) {
                throw new ValidacionException("No hay copias disponibles. "
                        + "La reserva sigue pendiente hasta que se libere una.");
            }

            LocalDateTime ahora = LocalDateTime.now();
            libro.setStockDisponible(libro.getStockDisponible() - 1);
            reserva.setEstado(EstadoReserva.APROBADA);
            reserva.setFechaLimiteRetiro(ahora.plusDays(ReglasBiblioteca.DIAS_RETIRO_RESERVA));
            reserva.setResueltaPor(personal);
            reserva.setResueltaEn(ahora);

            auditoriaService.registrar(em, personal, AccionAuditoria.CAMBIO_ESTADO, "Reserva", reserva.getId(),
                    "Aprobó la reserva de \"" + libro.getTitulo() + "\". Plazo de retiro: "
                    + ReglasBiblioteca.DIAS_RETIRO_RESERVA + " días.");
            return ReservaDTO.desde(reserva);
        });
    }

    /* Rechaza una reserva PENDIENTE. Las observaciones son opcionales. */
    public ReservaDTO rechazar(UsuarioDTO actor, Long idReserva, String observaciones) {
        vencerVencidas();
        return Transaccion.ejecutar(em -> {
            Usuario personal = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR, Rol.BIBLIOTECARIO);
            Reserva reserva = exigirReserva(em, idReserva);

            if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
                throw new ValidacionException("Solo se pueden rechazar reservas pendientes.");
            }

            reserva.setEstado(EstadoReserva.RECHAZADA);
            reserva.setResueltaPor(personal);
            reserva.setResueltaEn(LocalDateTime.now());
            reserva.setObservaciones(Texto.opcional(observaciones, "Las observaciones", MAX_OBSERVACIONES));

            auditoriaService.registrar(em, personal, AccionAuditoria.CAMBIO_ESTADO, "Reserva", reserva.getId(),
                    "Rechazó la reserva de \"" + reserva.getLibro().getTitulo() + "\".");
            return ReservaDTO.desde(reserva);
        });
    }

    // Vencimiento automatico

    /*
     * Pasa a VENCIDA las reservas aprobadas cuyo plazo de retiro ya termino y
     * devuelve cada copia al stock. Se llama al inicio de cada operacion
     * (tambien desde PrestamoService), en su propia transaccion.
     * Si otra persona las vencio al mismo tiempo, se ignora el choque.
     */
    public void vencerVencidas() {
        try {
            Transaccion.ejecutarAccion(em -> {
                LocalDateTime ahora = LocalDateTime.now();
                for (Reserva reserva : reservaDAO.buscarAprobadasVencidas(em, ahora)) {
                    cerrar(reserva, EstadoReserva.VENCIDA);
                    reserva.setObservaciones("Venció: no se retiró en el plazo.");
                    // Sin actor: la accion la hace el sistema
                    auditoriaService.registrar(em, null, AccionAuditoria.CAMBIO_ESTADO, "Reserva",
                            reserva.getId(), "La reserva de \"" + reserva.getLibro().getTitulo()
                            + "\" venció sin retirarse.");
                }
            });
        } catch (ValidacionException e) {
            // Otra operacion ya las vencio: no hay nada mas que hacer
        }
    }

    // Ayudas internas
    /*
     * Cambia el estado a uno que cierra la reserva (cancelada, vencida...).
     * Si estaba APROBADA tenia una copia apartada: se devuelve al stock.
     */
    private void cerrar(Reserva reserva, EstadoReserva nuevoEstado) {
        if (reserva.getEstado() == EstadoReserva.APROBADA) {
            Libro libro = reserva.getLibro();
            libro.setStockDisponible(libro.getStockDisponible() + 1);
        }
        reserva.setEstado(nuevoEstado);
    }

    private Reserva exigirReserva(EntityManager em, Long idReserva) {
        if (idReserva == null) {
            throw new ValidacionException("La reserva no existe.");
        }
        return reservaDAO.buscarPorId(em, idReserva)
                .orElseThrow(() -> new ValidacionException("La reserva no existe."));
    }
}
