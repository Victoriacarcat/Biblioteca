package com.mycompany.wisebooks.service;

import com.mycompany.wisebooks.dao.SancionDAO;
import com.mycompany.wisebooks.dto.SancionDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.AccionAuditoria;
import com.mycompany.wisebooks.entity.EstadoSancion;
import com.mycompany.wisebooks.entity.Prestamo;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.entity.Sancion;
import com.mycompany.wisebooks.entity.TipoSancion;
import com.mycompany.wisebooks.entity.Usuario;
import com.mycompany.wisebooks.util.Autorizacion;
import com.mycompany.wisebooks.util.ReglasBiblioteca;
import com.mycompany.wisebooks.util.Texto;
import com.mycompany.wisebooks.util.Transaccion;
import com.mycompany.wisebooks.util.ValidacionException;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/*
 * Reglas de negocio de las sanciones (multas).
 *
 * Quien puede hacer que:
 *   - Ver las propias: el LECTOR (activo o suspendido).
 *   - Ver todas, registrar (dano u otro motivo) y marcar como pagada:
 *     ADMINISTRADOR y BIBLIOTECARIO.
 *   - Anular: solo el ADMINISTRADOR.
 *
 * De donde salen:
 *   - MORA: la genera el sistema al devolver tarde (PrestamoService.devolver).
 *   - LIBRO_PERDIDO: se crea al marcar un prestamo como perdido (PrestamoService).
 *   - LIBRO_DANADO y OTRO: las registra el personal a mano.
 *
 * Ciclo: PENDIENTE --pagar--> PAGADA
 *        PENDIENTE --anular--> ANULADA
 *
 * Una sancion es solo un registro de cobro: no bloquea al usuario.
 */
public class SancionService {

    private static final int MAX_DETALLE = 255;

    // La columna importe es DECIMAL(10,2): como maximo 99.999.999,99
    private static final BigDecimal MAX_IMPORTE = new BigDecimal("99999999.99");

    private final SancionDAO sancionDAO = new SancionDAO();
    private final AuditoriaService auditoriaService = new AuditoriaService();

    // Consultas

    /* Las sanciones del lector que inicio sesion, la mas reciente primero. */
    public List<SancionDTO> misSanciones(UsuarioDTO actor) {
        return Transaccion.consultar(em -> {
            Usuario lector = Autorizacion.exigirLector(em, actor);
            return sancionDAO.listarPorUsuario(em, lector.getId()).stream()
                    .map(SancionDTO::desde)
                    .toList();
        });
    }

    /* Todas las sanciones (solo personal). Si estado es null, trae todas. */
    public List<SancionDTO> listar(UsuarioDTO actor, EstadoSancion estado) {
        return Transaccion.consultar(em -> {
            Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR, Rol.BIBLIOTECARIO);
            return sancionDAO.listar(em, estado).stream()
                    .map(SancionDTO::desde)
                    .toList();
        });
    }

    // Acciones del personal

    /*
     * Registra a mano una sancion por LIBRO_DANADO u OTRO motivo.
     * Si se indica idPrestamo, la sancion queda ligada a el y se cobra a
     * quien tenia el libro (idUsuario puede ir null). Sin prestamo, idUsuario
     * es obligatorio. MORA y LIBRO_PERDIDO no se registran aqui: las genera
     * PrestamoService.
     */
    public SancionDTO registrar(UsuarioDTO actor, Long idUsuario, Long idPrestamo,
            TipoSancion tipo, BigDecimal importe, String detalle) {
        return Transaccion.ejecutar(em -> {
            Usuario personal = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR, Rol.BIBLIOTECARIO);

            if (tipo != TipoSancion.LIBRO_DANADO && tipo != TipoSancion.OTRO) {
                throw new ValidacionException("Solo se pueden registrar a mano sanciones por libro dañado u otro motivo.");
            }
            exigirImporte(importe);
            String detalleLimpio = Texto.opcional(detalle, "El detalle", MAX_DETALLE);
            if (tipo == TipoSancion.OTRO && detalleLimpio == null) {
                throw new ValidacionException("Describe el motivo de la sanción.");
            }

            Prestamo prestamo = null;
            Usuario sancionado;
            if (idPrestamo != null) {
                prestamo = em.find(Prestamo.class, idPrestamo);
                if (prestamo == null) {
                    throw new ValidacionException("El préstamo no existe.");
                }
                sancionado = prestamo.getUsuario();
                if (idUsuario != null && !idUsuario.equals(sancionado.getId())) {
                    throw new ValidacionException("Ese préstamo no pertenece al usuario indicado.");
                }
                if (sancionDAO.existeDelPrestamo(em, prestamo.getId(), tipo)) {
                    throw new ValidacionException("Ese préstamo ya tiene una sanción de este tipo.");
                }
            } else {
                sancionado = (idUsuario == null) ? null : em.find(Usuario.class, idUsuario);
                if (sancionado == null) {
                    throw new ValidacionException("El usuario no existe.");
                }
            }
            if (sancionado.getRol() != Rol.LECTOR) {
                throw new ValidacionException("Solo se puede sancionar a lectores.");
            }

            Sancion sancion = crear(sancionado, prestamo, tipo, importe, personal, detalleLimpio);
            sancionDAO.guardar(em, sancion);

            auditoriaService.registrar(em, personal, AccionAuditoria.CREAR, "Sancion", sancion.getId(),
                    "Registró una sanción (" + tipo.name().toLowerCase() + ") de $" + importe.toPlainString()
                    + " a " + sancionado.getCorreo() + ".");
            return SancionDTO.desde(sancion);
        });
    }

    /* Marca una sancion PENDIENTE como pagada. */
    public SancionDTO pagar(UsuarioDTO actor, Long idSancion) {
        return Transaccion.ejecutar(em -> {
            Usuario personal = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR, Rol.BIBLIOTECARIO);
            Sancion sancion = exigirPendiente(em, idSancion, "pagar");

            sancion.setEstado(EstadoSancion.PAGADA);
            sancion.setFechaPago(LocalDateTime.now());

            auditoriaService.registrar(em, personal, AccionAuditoria.CAMBIO_ESTADO, "Sancion", sancion.getId(),
                    "Registró el pago de $" + sancion.getImporte().toPlainString()
                    + " de " + sancion.getUsuario().getCorreo() + ".");
            return SancionDTO.desde(sancion);
        });
    }

    /* Anula una sancion PENDIENTE (por ejemplo, si se registro por error). Solo administrador. */
    public SancionDTO anular(UsuarioDTO actor, Long idSancion, String motivo) {
        return Transaccion.ejecutar(em -> {
            Usuario admin = Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR);
            Sancion sancion = exigirPendiente(em, idSancion, "anular");
            String motivoLimpio = Texto.exigir(motivo, "El motivo de la anulación", MAX_DETALLE);

            sancion.setEstado(EstadoSancion.ANULADA);
            sancion.setDetalle(Texto.recortar(
                    (sancion.getDetalle() == null ? "" : sancion.getDetalle() + " | ")
                    + "Anulada: " + motivoLimpio, MAX_DETALLE));

            auditoriaService.registrar(em, admin, AccionAuditoria.CAMBIO_ESTADO, "Sancion", sancion.getId(),
                    "Anuló la sanción de " + sancion.getUsuario().getCorreo() + ". Motivo: " + motivoLimpio);
            return SancionDTO.desde(sancion);
        });
    }

    // Usadas por PrestamoService (dentro de SU transaccion)

    /*
     * Si el prestamo se devolvio despues de la fecha limite, crea la sancion
     * por MORA: TARIFA_MORA_DIARIA por cada dia de retraso. Se cuentan dias
     * de calendario (devolver al dia siguiente = 1 dia). La genera el sistema
     * (sin registradoPor). Devuelve la sancion, o null si no hubo retraso.
     */
    Sancion generarMoraSiCorresponde(EntityManager em, Prestamo prestamo, LocalDateTime devolucion) {
        long dias = ChronoUnit.DAYS.between(
                prestamo.getFechaLimite().toLocalDate(), devolucion.toLocalDate());
        if (dias <= 0 || sancionDAO.existeDelPrestamo(em, prestamo.getId(), TipoSancion.MORA)) {
            return null;
        }
        BigDecimal importe = ReglasBiblioteca.TARIFA_MORA_DIARIA.multiply(BigDecimal.valueOf(dias));

        Sancion mora = crear(prestamo.getUsuario(), prestamo, TipoSancion.MORA, importe, null,
                "Devolución con " + dias + (dias == 1 ? " día" : " días") + " de retraso.");
        mora.setDiasRetraso((int) dias);
        sancionDAO.guardar(em, mora);

        auditoriaService.registrar(em, null, AccionAuditoria.CREAR, "Sancion", mora.getId(),
                "Mora automática de $" + importe.toPlainString() + " a " + prestamo.getUsuario().getCorreo()
                + " (" + dias + " días de retraso).");
        return mora;
    }

    /* Crea la sancion por LIBRO_PERDIDO de un prestamo. */
    Sancion generarPorPerdida(EntityManager em, Prestamo prestamo, BigDecimal importe,
            Usuario personal, String detalle) {
        exigirImporte(importe);
        if (sancionDAO.existeDelPrestamo(em, prestamo.getId(), TipoSancion.LIBRO_PERDIDO)) {
            throw new ValidacionException("Ese préstamo ya tiene una sanción por pérdida.");
        }
        Sancion sancion = crear(prestamo.getUsuario(), prestamo, TipoSancion.LIBRO_PERDIDO, importe,
                personal, Texto.opcional(detalle, "El detalle", MAX_DETALLE));
        sancionDAO.guardar(em, sancion);
        return sancion;
    }

    // Ayudas internas

    private Sancion crear(Usuario usuario, Prestamo prestamo, TipoSancion tipo, BigDecimal importe,
            Usuario registradoPor, String detalle) {
        Sancion sancion = new Sancion();
        sancion.setUsuario(usuario);
        sancion.setPrestamo(prestamo);
        sancion.setTipo(tipo);
        sancion.setImporte(importe);
        sancion.setEstado(EstadoSancion.PENDIENTE);
        sancion.setRegistradoPor(registradoPor);
        sancion.setDetalle(detalle);
        return sancion;
    }

    private void exigirImporte(BigDecimal importe) {
        if (importe == null || importe.signum() <= 0) {
            throw new ValidacionException("El valor de la sanción debe ser mayor que cero.");
        }
        if (importe.compareTo(MAX_IMPORTE) > 0) {
            throw new ValidacionException("El valor de la sanción es demasiado alto.");
        }
        if (importe.stripTrailingZeros().scale() > 2) {
            throw new ValidacionException("El valor de la sanción admite como máximo 2 decimales.");
        }
    }

    private Sancion exigirPendiente(EntityManager em, Long idSancion, String accion) {
        Sancion sancion = (idSancion == null) ? null : sancionDAO.buscarPorId(em, idSancion).orElse(null);
        if (sancion == null) {
            throw new ValidacionException("La sanción no existe.");
        }
        if (sancion.getEstado() != EstadoSancion.PENDIENTE) {
            throw new ValidacionException("Solo se pueden " + accion + " sanciones pendientes (estado: "
                    + sancion.getEstado().name().toLowerCase() + ").");
        }
        return sancion;
    }
}
