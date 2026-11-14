package com.mycompany.wisebooks.dto;

import com.mycompany.wisebooks.entity.EstadoUsuario;
import java.math.BigDecimal;
import java.util.List;

/*
 * Lo que ve el lector en su dashboard: sus numeros, sus proximas
 * devoluciones y sus reservas vivas (para los avisos de "puedes recogerla").
 */
public class ResumenLectorDTO {

    // Estado de la cuenta leido de la base (no el de la sesion, que puede estar desactualizado)
    private final EstadoUsuario estadoCuenta;
    private final long prestamosActivos;
    private final long prestamosVencidos;
    // Mora que sumarian sus libros vencidos si los devolviera hoy (estimado)
    private final BigDecimal moraEstimada;
    // Reservas pendientes + aprobadas
    private final long reservasActivas;
    // Reservas aprobadas: la copia lo espera en la biblioteca
    private final long reservasParaRetirar;
    private final long sancionesPendientes;
    private final BigDecimal deudaPendiente;
    private final long leidosEsteAnio;
    // Prestamos activos, el que vence primero arriba
    private final List<PrestamoDTO> proximasDevoluciones;
    // Reservas pendientes y aprobadas: primero las aprobadas (la que vence antes arriba)
    private final List<ReservaDTO> reservasVivas;

    public ResumenLectorDTO(EstadoUsuario estadoCuenta, long prestamosActivos, long prestamosVencidos, BigDecimal moraEstimada,
            long reservasActivas, long reservasParaRetirar, long sancionesPendientes, BigDecimal deudaPendiente,
            long leidosEsteAnio, List<PrestamoDTO> proximasDevoluciones, List<ReservaDTO> reservasVivas) {
        this.estadoCuenta = estadoCuenta;
        this.prestamosActivos = prestamosActivos;
        this.prestamosVencidos = prestamosVencidos;
        this.moraEstimada = moraEstimada;
        this.reservasActivas = reservasActivas;
        this.reservasParaRetirar = reservasParaRetirar;
        this.sancionesPendientes = sancionesPendientes;
        this.deudaPendiente = deudaPendiente;
        this.leidosEsteAnio = leidosEsteAnio;
        this.proximasDevoluciones = proximasDevoluciones;
        this.reservasVivas = reservasVivas;
    }

    /* true si no debe nada ni tiene libros vencidos (para el mensaje "Todo al dia"). */
    public boolean isAlDia() {
        return prestamosVencidos == 0 && sancionesPendientes == 0;
    }

    public EstadoUsuario getEstadoCuenta() {
        return estadoCuenta;
    }

    public long getPrestamosActivos() {
        return prestamosActivos;
    }

    public long getPrestamosVencidos() {
        return prestamosVencidos;
    }

    public BigDecimal getMoraEstimada() {
        return moraEstimada;
    }

    public long getReservasActivas() {
        return reservasActivas;
    }

    public long getReservasParaRetirar() {
        return reservasParaRetirar;
    }

    public long getSancionesPendientes() {
        return sancionesPendientes;
    }

    public BigDecimal getDeudaPendiente() {
        return deudaPendiente;
    }

    public long getLeidosEsteAnio() {
        return leidosEsteAnio;
    }

    public List<PrestamoDTO> getProximasDevoluciones() {
        return proximasDevoluciones;
    }

    public List<ReservaDTO> getReservasVivas() {
        return reservasVivas;
    }
}
