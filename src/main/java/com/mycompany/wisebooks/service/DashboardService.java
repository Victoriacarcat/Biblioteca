package com.mycompany.wisebooks.service;

import com.mycompany.wisebooks.dao.DashboardDAO;
import com.mycompany.wisebooks.dao.PrestamoDAO;
import com.mycompany.wisebooks.dao.ReservaDAO;
import com.mycompany.wisebooks.dao.SancionDAO;
import com.mycompany.wisebooks.dto.EstadisticasDTO;
import com.mycompany.wisebooks.dto.PrestamoDTO;
import com.mycompany.wisebooks.dto.ResumenLectorDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.EstadoPrestamo;
import com.mycompany.wisebooks.entity.Prestamo;
import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.entity.Usuario;
import com.mycompany.wisebooks.util.Autorizacion;
import com.mycompany.wisebooks.util.Transaccion;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/*
 * Numeros del dashboard. Solo consultas, nada se modifica.
 *
 *   - estadisticas(actor): datos generales. El visitante y el lector ven los
 *     publicos; el personal ve tambien prestamos, reservas y sanciones.
 *   - resumenLector(actor): el resumen personal del lector.
 */
public class DashboardService {

    private final DashboardDAO dashboardDAO = new DashboardDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final ReservaDAO reservaDAO = new ReservaDAO();
    private final SancionDAO sancionDAO = new SancionDAO();

    /* "actor" puede ser null (visitante). */
    public EstadisticasDTO estadisticas(UsuarioDTO actor) {
        boolean personal = Autorizacion.esPersonal(actor);
        return Transaccion.consultar(em -> {
            LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            EstadisticasDTO dto = new EstadisticasDTO(
                    dashboardDAO.contarLibrosActivos(em),
                    dashboardDAO.contarLectoresActivos(em),
                    dashboardDAO.contarCategoriasActivas(em),
                    dashboardDAO.contarLibrosCreadosDesde(em, inicioMes));

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
            }
            return dto;
        });
    }

    /* Resumen personal del lector que inicio sesion. */
    public ResumenLectorDTO resumenLector(UsuarioDTO actor) {
        return Transaccion.consultar(em -> {
            Usuario lector = Autorizacion.exigirLector(em, actor);
            Long id = lector.getId();
            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime inicioAnio = LocalDate.now().withDayOfYear(1).atStartOfDay();

            return new ResumenLectorDTO(
                    prestamoDAO.contarActivosDeUsuario(em, id),
                    prestamoDAO.contarVencidosDeUsuario(em, id, ahora),
                    reservaDAO.contarVivasDeUsuario(em, id),
                    sancionDAO.contarPendientesDeUsuario(em, id),
                    sancionDAO.sumarPendientesDeUsuario(em, id),
                    dashboardDAO.contarDevueltosDeUsuarioDesde(em, id, inicioAnio),
                    proximasDevoluciones(em, id));
        });
    }

    private List<PrestamoDTO> proximasDevoluciones(EntityManager em, Long idUsuario) {
        return prestamoDAO.listarPorUsuario(em, idUsuario).stream()
                .filter(p -> p.getEstado() == EstadoPrestamo.ACTIVO)
                .sorted(Comparator.comparing(Prestamo::getFechaLimite))
                .map(PrestamoDTO::desde)
                .toList();
    }
}
