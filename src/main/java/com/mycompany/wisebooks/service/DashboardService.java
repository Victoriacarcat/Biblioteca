package com.mycompany.wisebooks.service;

import com.mycompany.wisebooks.dao.DashboardDAO;
import com.mycompany.wisebooks.dao.PrestamoDAO;
import com.mycompany.wisebooks.dao.ReservaDAO;
import com.mycompany.wisebooks.dao.SancionDAO;
import com.mycompany.wisebooks.dto.CategoriaPedidaDTO;
import com.mycompany.wisebooks.dto.EstadisticasDTO;
import com.mycompany.wisebooks.dto.PrestamoDTO;
import com.mycompany.wisebooks.dto.ReservaDTO;
import com.mycompany.wisebooks.dto.ResumenLectorDTO;
import com.mycompany.wisebooks.dto.SemanaCirculacionDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.EstadoPrestamo;
import com.mycompany.wisebooks.entity.EstadoReserva;
import com.mycompany.wisebooks.entity.Prestamo;
import com.mycompany.wisebooks.entity.Reserva;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.entity.Usuario;
import com.mycompany.wisebooks.util.Autorizacion;
import com.mycompany.wisebooks.util.Transaccion;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*
 * Numeros del dashboard. Solo consultas: lo unico que puede cambiar es el
 * vencimiento de reservas, que se aplica antes de contar (como en el resto
 * de services), para que las copias apartadas sean las reales.
 *
 *   - estadisticas(actor): datos generales. El visitante y el lector ven los
 *     publicos; el personal ve tambien prestamos, reservas, sanciones y
 *     circulacion.
 *   - resumenLector(actor): el resumen personal del lector.
 */
public class DashboardService {

    // Cuantas categorias se muestran en "Categorias mas pedidas"
    private static final int MAX_CATEGORIAS_PEDIDAS = 5;
    // Cuantas semanas muestra el grafico de circulacion (la actual y las anteriores)
    private static final int SEMANAS_CIRCULACION = 8;

    private final DashboardDAO dashboardDAO = new DashboardDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final SancionDAO sancionDAO = new SancionDAO();
    private final ReservaService reservaService = new ReservaService();

    /* "actor" puede ser null (visitante). */
    public EstadisticasDTO estadisticas(UsuarioDTO actor) {
        boolean personal = Autorizacion.esPersonal(actor);
        if (personal) {
            reservaService.vencerVencidas();
        }
        return Transaccion.consultar(em -> {
            LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            EstadisticasDTO dto = new EstadisticasDTO(
                    dashboardDAO.contarLibrosActivos(em),
                    dashboardDAO.contarLectoresActivos(em),
                    dashboardDAO.contarCategoriasActivas(em),
                    dashboardDAO.contarLibrosCreadosDesde(em, inicioMes),
                    categoriasMasPedidas(em));

            if (personal) {
                // esPersonal solo mira el DTO de la sesion: aqui se confirma contra la base
                Autorizacion.exigirRol(em, actor, Rol.ADMINISTRADOR, Rol.BIBLIOTECARIO);
                long[] copias = dashboardDAO.sumarCopias(em);
                dto.agregarDatosPersonal(
                        copias[0],
                        copias[1],
                        dashboardDAO.contarPrestamosActivos(em),
                        dashboardDAO.contarPrestamosVencidos(em, LocalDateTime.now()),
                        dashboardDAO.contarReservasPendientes(em),
                        dashboardDAO.contarSancionesPendientes(em),
                        dashboardDAO.sumarSancionesPendientes(em));
                agregarCirculacion(em, dto);
            }
            return dto;
        });
    }

    // ---- Ayudas de estadisticas ----

    private List<CategoriaPedidaDTO> categoriasMasPedidas(EntityManager em) {
        return dashboardDAO.categoriasMasPedidas(em, MAX_CATEGORIAS_PEDIDAS).stream()
                .map(fila -> new CategoriaPedidaDTO((String) fila[0], (Long) fila[1]))
                .toList();
    }

    /* Calcula los datos de circulacion y los agrega al DTO (solo personal). */
    private void agregarCirculacion(EntityManager em, EstadisticasDTO dto) {
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();
        long prestamosHoy = dashboardDAO.contarPrestamosEntre(em, inicioHoy, inicioHoy.plusDays(1));

        // Duracion media y porcentaje a tiempo, con las fechas de los prestamos devueltos
        List<Object[]> devueltos = dashboardDAO.fechasDeDevueltos(em);
        Double duracionMedia = null;
        Integer porcentajeATiempo = null;
        if (!devueltos.isEmpty()) {
            long diasTotales = 0;
            long aTiempo = 0;
            for (Object[] fila : devueltos) {
                LocalDate prestamo = ((LocalDateTime) fila[0]).toLocalDate();
                LocalDate limite = ((LocalDateTime) fila[1]).toLocalDate();
                LocalDate devolucion = ((LocalDateTime) fila[2]).toLocalDate();
                diasTotales += ChronoUnit.DAYS.between(prestamo, devolucion);
                // Misma regla que la mora: devolver el dia limite (a cualquier hora) es a tiempo
                if (!devolucion.isAfter(limite)) {
                    aTiempo++;
                }
            }
            duracionMedia = redondearUnDecimal((double) diasTotales / devueltos.size());
            porcentajeATiempo = (int) Math.round(100.0 * aTiempo / devueltos.size());
        }

        long lectores = dto.getLectoresActivos();
        Double prestamosPorLector = lectores == 0 ? null
                : redondearUnDecimal((double) dashboardDAO.contarPrestamosNoAnulados(em) / lectores);

        dto.agregarCirculacion(
                dashboardDAO.contarReservasAprobadas(em),
                prestamosHoy,
                circulacionSemanal(em),
                duracionMedia,
                porcentajeATiempo,
                prestamosPorLector);
    }

    /*
     * Prestamos y devoluciones de las ultimas SEMANAS_CIRCULACION semanas
     * (de lunes a domingo), de la mas antigua a la actual.
     * Son dos COUNT por semana: con 8 semanas, 16 consultas pequenas.
     */
    private List<SemanaCirculacionDTO> circulacionSemanal(EntityManager em) {
        LocalDate lunesActual = LocalDate.now().with(DayOfWeek.MONDAY);
        List<SemanaCirculacionDTO> semanas = new ArrayList<>();
        for (int i = SEMANAS_CIRCULACION - 1; i >= 0; i--) {
            LocalDate lunes = lunesActual.minusWeeks(i);
            LocalDateTime desde = lunes.atStartOfDay();
            LocalDateTime hasta = lunes.plusWeeks(1).atStartOfDay();
            semanas.add(new SemanaCirculacionDTO(lunes,
                    dashboardDAO.contarPrestamosEntre(em, desde, hasta),
                    dashboardDAO.contarDevolucionesEntre(em, desde, hasta)));
        }
        return semanas;
    }

    private static double redondearUnDecimal(double valor) {
        return Math.round(valor * 10) / 10.0;
    }

    /* Resumen personal del lector que inicio sesion. */
    public ResumenLectorDTO resumenLector(UsuarioDTO actor) {
        // Antes de mostrar "puedes recoger tu reserva", se vencen las que ya pasaron el plazo
        reservaService.vencerVencidas();
        return Transaccion.consultar(em -> {
            Usuario lector = Autorizacion.exigirLector(em, actor);
            Long id = lector.getId();
            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime inicioAnio = LocalDate.now().withDayOfYear(1).atStartOfDay();

            List<PrestamoDTO> proximas = proximasDevoluciones(em, id);
            List<ReservaDTO> reservas = reservasVivas(em, id);

            // Suma de la mora estimada de los vencidos (cada PrestamoDTO la calcula)
            BigDecimal moraEstimada = BigDecimal.ZERO;
            for (PrestamoDTO prestamo : proximas) {
                if (prestamo.getMoraEstimada() != null) {
                    moraEstimada = moraEstimada.add(prestamo.getMoraEstimada());
                }
            }
            long paraRetirar = reservas.stream()
                    .filter(r -> r.getEstado() == EstadoReserva.APROBADA)
                    .count();

            return new ResumenLectorDTO(
                    lector.getEstado(),
                    prestamoDAO.contarActivosDeUsuario(em, id),
                    prestamoDAO.contarVencidosDeUsuario(em, id, ahora),
                    moraEstimada,
                    reservas.size(),
                    paraRetirar,
                    sancionDAO.contarPendientesDeUsuario(em, id),
                    sancionDAO.sumarPendientesDeUsuario(em, id),
                    dashboardDAO.contarDevueltosDeUsuarioDesde(em, id, inicioAnio),
                    proximas,
                    reservas);
        });
    }

    /*
     * Reservas pendientes y aprobadas del lector. Primero las aprobadas (la
     * que se debe recoger antes, arriba) y despues las pendientes (la mas
     * antigua arriba).
     */
    private List<ReservaDTO> reservasVivas(EntityManager em, Long idUsuario) {
        List<Reserva> aprobadas = new ArrayList<>();
        List<Reserva> pendientes = new ArrayList<>();
        for (Reserva reserva : reservaDAO.listarPorUsuario(em, idUsuario)) {
            if (reserva.getEstado() == EstadoReserva.APROBADA) {
                aprobadas.add(reserva);
            } else if (reserva.getEstado() == EstadoReserva.PENDIENTE) {
                pendientes.add(reserva);
            }
        }
        aprobadas.sort(Comparator.comparing(Reserva::getFechaLimiteRetiro));
        pendientes.sort(Comparator.comparing(Reserva::getFechaReserva));

        List<ReservaDTO> resultado = new ArrayList<>();
        for (Reserva reserva : aprobadas) {
            resultado.add(ReservaDTO.desde(reserva));
        }
        for (Reserva reserva : pendientes) {
            resultado.add(ReservaDTO.desde(reserva));
        }
        return resultado;
    }

    private List<PrestamoDTO> proximasDevoluciones(EntityManager em, Long idUsuario) {
        return prestamoDAO.listarPorUsuario(em, idUsuario).stream()
                .filter(p -> p.getEstado() == EstadoPrestamo.ACTIVO)
                .sorted(Comparator.comparing(Prestamo::getFechaLimite))
                .map(PrestamoDTO::desde)
                .toList();
    }
}
