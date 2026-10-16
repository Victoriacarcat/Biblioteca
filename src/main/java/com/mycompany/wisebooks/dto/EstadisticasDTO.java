package com.mycompany.wisebooks.dto;

import java.math.BigDecimal;
import java.util.List;

/*
 * Numeros generales de la biblioteca para el dashboard.
 * Para el visitante y el lector solo se llenan los datos publicos (libros,
 * lectores, categorias, nuevos titulos y categorias mas pedidas); los del
 * personal quedan en null.
 */
public class EstadisticasDTO {

    // Publicos
    private long totalLibros;
    private long lectoresActivos;
    private long categoriasActivas;
    private long nuevosTitulosEsteMes;
    private List<CategoriaPedidaDTO> categoriasMasPedidas;

    // Solo personal (null para el visitante)
    private Long copiasTotales;
    private Long copiasDisponibles;
    private Long copiasApartadas;        // por reservas aprobadas sin retirar
    private Long prestamosActivos;
    private Long prestamosVencidos;
    private Long prestamosHoy;
    private Long reservasPendientes;
    private Long sancionesPendientes;
    private BigDecimal montoSancionesPendientes;

    // Circulacion (solo personal)
    private List<SemanaCirculacionDTO> circulacionSemanal;
    private Double duracionMediaDias;    // null si todavia no hay devoluciones
    private Integer porcentajeATiempo;   // null si todavia no hay devoluciones
    private Double prestamosPorLector;   // null si no hay lectores activos

    public EstadisticasDTO(long totalLibros, long lectoresActivos, long categoriasActivas,
            long nuevosTitulosEsteMes, List<CategoriaPedidaDTO> categoriasMasPedidas) {
        this.totalLibros = totalLibros;
        this.lectoresActivos = lectoresActivos;
        this.categoriasActivas = categoriasActivas;
        this.nuevosTitulosEsteMes = nuevosTitulosEsteMes;
        this.categoriasMasPedidas = categoriasMasPedidas;
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

    /* Completa los datos de circulacion (solo personal). */
    public void agregarCirculacion(long copiasApartadas, long prestamosHoy,
            List<SemanaCirculacionDTO> circulacionSemanal, Double duracionMediaDias,
            Integer porcentajeATiempo, Double prestamosPorLector) {
        this.copiasApartadas = copiasApartadas;
        this.prestamosHoy = prestamosHoy;
        this.circulacionSemanal = circulacionSemanal;
        this.duracionMediaDias = duracionMediaDias;
        this.porcentajeATiempo = porcentajeATiempo;
        this.prestamosPorLector = prestamosPorLector;
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

    public List<CategoriaPedidaDTO> getCategoriasMasPedidas() {
        return categoriasMasPedidas;
    }

    public Long getCopiasApartadas() {
        return copiasApartadas;
    }

    public Long getPrestamosHoy() {
        return prestamosHoy;
    }

    public List<SemanaCirculacionDTO> getCirculacionSemanal() {
        return circulacionSemanal;
    }

    public Double getDuracionMediaDias() {
        return duracionMediaDias;
    }

    public Integer getPorcentajeATiempo() {
        return porcentajeATiempo;
    }

    public Double getPrestamosPorLector() {
        return prestamosPorLector;
    }
}
