package com.mycompany.wisebooks.dto;

import java.math.BigDecimal;
import java.util.List;

/*
 * Lo que ve el lector en su dashboard: sus numeros y sus proximas devoluciones.
 */
public class ResumenLectorDTO {

    private final long prestamosActivos;
    private final long prestamosVencidos;
    private final long reservasActivas;
    private final long sancionesPendientes;
    private final BigDecimal deudaPendiente;
    private final long leidosEsteAnio;
    // Prestamos activos, el que vence primero arriba
    private final List<PrestamoDTO> proximasDevoluciones;

    public ResumenLectorDTO(long prestamosActivos, long prestamosVencidos, long reservasActivas,
            long sancionesPendientes, BigDecimal deudaPendiente, long leidosEsteAnio,
            List<PrestamoDTO> proximasDevoluciones) {
        this.prestamosActivos = prestamosActivos;
        this.prestamosVencidos = prestamosVencidos;
        this.reservasActivas = reservasActivas;
        this.sancionesPendientes = sancionesPendientes;
        this.deudaPendiente = deudaPendiente;
        this.leidosEsteAnio = leidosEsteAnio;
        this.proximasDevoluciones = proximasDevoluciones;
    }

    /* true si no debe nada ni tiene libros vencidos (para el mensaje "Todo al dia"). */
    public boolean isAlDia() {
        return prestamosVencidos == 0 && sancionesPendientes == 0;
    }

    public long getPrestamosActivos() {
        return prestamosActivos;
    }

    public long getPrestamosVencidos() {
        return prestamosVencidos;
    }

    public long getReservasActivas() {
        return reservasActivas;
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
}
