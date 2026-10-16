package com.mycompany.wisebooks.dto;

import java.math.BigDecimal;

/*
 * Numeros generales de la biblioteca para el dashboard.
 * Para el visitante solo se llenan los datos publicos (libros, lectores,
 * categorias y nuevos titulos); los del personal quedan en null.
 */
public class EstadisticasDTO {

    // Publicos
    private long totalLibros;
    private long lectoresActivos;
    private long categoriasActivas;
    private long nuevosTitulosEsteMes;

    // Solo personal (null para el visitante)
    private Long copiasTotales;
    private Long copiasDisponibles;
    private Long prestamosActivos;
    private Long prestamosVencidos;
    private Long reservasPendientes;
    private Long sancionesPendientes;
    private BigDecimal montoSancionesPendientes;

    public EstadisticasDTO(long totalLibros, long lectoresActivos, long categoriasActivas,
            long nuevosTitulosEsteMes) {
        this.totalLibros = totalLibros;
        this.lectoresActivos = lectoresActivos;
        this.categoriasActivas = categoriasActivas;
        this.nuevosTitulosEsteMes = nuevosTitulosEsteMes;
    }

    /* Completa los datos que solo ve el personal. */
    public void agregarDatosPersonal(long copiasTotales, long copiasDisponibles, long prestamosActivos,
            long prestamosVencidos, long reservasPendientes, long sancionesPendientes,
            BigDecimal montoSancionesPendientes) {
        this.copiasTotales = copiasTotales;
        this.copiasDisponibles = copiasDisponibles;
        this.prestamosActivos = prestamosActivos;
        this.prestamosVencidos = prestamosVencidos;
        this.reservasPendientes = reservasPendientes;
        this.sancionesPendientes = sancionesPendientes;
        this.montoSancionesPendientes = montoSancionesPendientes;
    }

    /* Porcentaje de copias prestadas o apartadas (0 a 100), para la barra del KPI. */
    public Integer getPorcentajeOcupacion() {
        if (copiasTotales == null || copiasTotales == 0) {
            return copiasTotales == null ? null : 0;
        }
        return (int) Math.round(100.0 * (copiasTotales - copiasDisponibles) / copiasTotales);
    }

    public long getTotalLibros() {
        return totalLibros;
    }

    public long getLectoresActivos() {
        return lectoresActivos;
    }

    public long getCategoriasActivas() {
        return categoriasActivas;
    }

    public long getNuevosTitulosEsteMes() {
        return nuevosTitulosEsteMes;
    }

    public Long getCopiasTotales() {
        return copiasTotales;
    }

    public Long getCopiasDisponibles() {
        return copiasDisponibles;
    }

    public Long getPrestamosActivos() {
        return prestamosActivos;
    }

    public Long getPrestamosVencidos() {
        return prestamosVencidos;
    }

    public Long getReservasPendientes() {
        return reservasPendientes;
    }

    public Long getSancionesPendientes() {
        return sancionesPendientes;
    }

    public BigDecimal getMontoSancionesPendientes() {
        return montoSancionesPendientes;
    }
}
