package com.mycompany.wisebooks.dto;

import com.mycompany.wisebooks.entity.EstadoSancion;
import com.mycompany.wisebooks.entity.Sancion;
import com.mycompany.wisebooks.entity.TipoSancion;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * Lo que se muestra de una sancion (mis sanciones, lista del personal).
 * IMPORTANTE: desde(...) debe llamarse mientras el EntityManager sigue abierto,
 * porque lee el usuario, el prestamo y quien la registro (relaciones LAZY).
 */
public class SancionDTO {

    private Long id;
    private Long idUsuario;
    private String nombreUsuario;
    private String correoUsuario;
    private Long idPrestamo;
    private String tituloLibro;
    private TipoSancion tipo;
    private Integer diasRetraso;
    private BigDecimal importe;
    private EstadoSancion estado;
    private LocalDateTime fechaEmision;
    private LocalDateTime fechaPago;
    private String registradoPor;
    private String detalle;

    public static SancionDTO desde(Sancion s) {
        SancionDTO dto = new SancionDTO();
        dto.id = s.getId();
        dto.idUsuario = s.getUsuario().getId();
        dto.nombreUsuario = s.getUsuario().getNombre() + " " + s.getUsuario().getApellidos();
        dto.correoUsuario = s.getUsuario().getCorreo();
        if (s.getPrestamo() != null) {
            dto.idPrestamo = s.getPrestamo().getId();
            dto.tituloLibro = s.getPrestamo().getLibro().getTitulo();
        }
        dto.tipo = s.getTipo();
        dto.diasRetraso = s.getDiasRetraso();
        dto.importe = s.getImporte();
        dto.estado = s.getEstado();
        dto.fechaEmision = s.getFechaEmision();
        dto.fechaPago = s.getFechaPago();
        // Vacio = la genero el sistema (mora automatica)
        dto.registradoPor = s.getRegistradoPor() == null ? "Sistema"
                : s.getRegistradoPor().getNombre() + " " + s.getRegistradoPor().getApellidos();
        dto.detalle = s.getDetalle();
        return dto;
    }

    /* Motivo legible para la tabla: "Mora", "Libro dañado"... */
    public String getMotivo() {
        return switch (tipo) {
            case MORA -> "Mora";
            case LIBRO_DANADO -> "Libro dañado";
            case LIBRO_PERDIDO -> "Libro perdido";
            case OTRO -> "Otro";
        };
    }

    /* true si todavia se puede pagar o anular (sirve para mostrar botones en la JSP). */
    public boolean isPendiente() {
        return estado == EstadoSancion.PENDIENTE;
    }

    public Long getId() {
        return id;
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

    public Long getIdPrestamo() {
        return idPrestamo;
    }

    public String getTituloLibro() {
        return tituloLibro;
    }

    public TipoSancion getTipo() {
        return tipo;
    }

    public Integer getDiasRetraso() {
        return diasRetraso;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    public EstadoSancion getEstado() {
        return estado;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public String getRegistradoPor() {
        return registradoPor;
    }

    public String getDetalle() {
        return detalle;
    }
}
