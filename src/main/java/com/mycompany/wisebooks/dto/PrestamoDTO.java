package com.mycompany.wisebooks.dto;

import com.mycompany.wisebooks.entity.EstadoPrestamo;
import com.mycompany.wisebooks.entity.Prestamo;
import com.mycompany.wisebooks.entity.Usuario;
import com.mycompany.wisebooks.util.ReglasBiblioteca;
import java.time.LocalDateTime;

/*
 * Lo que se muestra de un prestamo (mis prestamos, lista del personal,
 * devoluciones e historial).
 * IMPORTANTE: desde(...) debe llamarse mientras el EntityManager sigue abierto,
 * porque lee el libro, el usuario y el personal que lo registro (relaciones LAZY).
 */
public class PrestamoDTO {

    private Long id;
    private Long idLibro;
    private String tituloLibro;
    private String portadaLibro;
    private Long idUsuario;
    private String nombreUsuario;
    private String correoUsuario;
    private Long idReserva;
    private String registradoPor;
    private String recibidoPor;
    private LocalDateTime fechaPrestamo;
    private LocalDateTime fechaLimite;
    private LocalDateTime fechaDevolucion;
    private int renovaciones;
    private EstadoPrestamo estado;

    public static PrestamoDTO desde(Prestamo p) {
        PrestamoDTO dto = new PrestamoDTO();
        dto.id = p.getId();
        dto.idLibro = p.getLibro().getId();
        dto.tituloLibro = p.getLibro().getTitulo();
        dto.portadaLibro = p.getLibro().getPortadaUrl();
        dto.idUsuario = p.getUsuario().getId();
        dto.nombreUsuario = nombreCompleto(p.getUsuario());
        dto.correoUsuario = p.getUsuario().getCorreo();
        dto.idReserva = p.getReserva() == null ? null : p.getReserva().getId();
        dto.registradoPor = nombreCompleto(p.getRegistradoPor());
        dto.recibidoPor = nombreCompleto(p.getRecibidoPor());
        dto.fechaPrestamo = p.getFechaPrestamo();
        dto.fechaLimite = p.getFechaLimite();
        dto.fechaDevolucion = p.getFechaDevolucion();
        dto.renovaciones = p.getRenovaciones();
        dto.estado = p.getEstado();
        return dto;
    }

    private static String nombreCompleto(Usuario u) {
        return u == null ? null : u.getNombre() + " " + u.getApellidos();
    }

    /* "Vencido" no se guarda: es un prestamo ACTIVO con la fecha limite ya pasada. */
    public boolean isVencido() {
        return estado == EstadoPrestamo.ACTIVO && fechaLimite.isBefore(LocalDateTime.now());
    }

    /* true si se devolvio despues de la fecha limite (sirve para el historial). */
    public boolean isDevueltoConRetraso() {
        return fechaDevolucion != null && fechaDevolucion.isAfter(fechaLimite);
    }

    /* true si todavia se puede renovar (sirve para mostrar el boton en la JSP). */
    public boolean isRenovable() {
        return estado == EstadoPrestamo.ACTIVO && !isVencido()
                && renovaciones < ReglasBiblioteca.MAX_RENOVACIONES;
    }

    public Long getId() {
        return id;
    }

    public Long getIdLibro() {
        return idLibro;
    }

    public String getTituloLibro() {
        return tituloLibro;
    }

    public String getPortadaLibro() {
        return portadaLibro;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getCorreoUsuario() {
        return correoUsuario;
    }

    public Long getIdReserva() {
        return idReserva;
    }

    public String getRegistradoPor() {
        return registradoPor;
    }

    public String getRecibidoPor() {
        return recibidoPor;
    }

    public LocalDateTime getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDateTime getFechaLimite() {
        return fechaLimite;
    }

    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    public int getRenovaciones() {
        return renovaciones;
    }

    public EstadoPrestamo getEstado() {
        return estado;
    }
}
