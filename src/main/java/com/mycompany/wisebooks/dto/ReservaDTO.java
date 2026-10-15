/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.dto;

import com.mycompany.wisebooks.entity.EstadoReserva;
import com.mycompany.wisebooks.entity.Reserva;
import java.time.LocalDateTime;

/*
 * Lo que se muestra de una reserva (mis reservas, lista del personal).
 * IMPORTANTE: desde(...) debe llamarse mientras el EntityManager sigue abierto,
 * porque lee el libro, el usuario y quien la resolvio (relaciones LAZY).
 */
public class ReservaDTO {

    private Long id;
    private Long idLibro;
    private String tituloLibro;
    private Long idUsuario;
    private String nombreUsuario;
    private LocalDateTime fechaReserva;
    private LocalDateTime fechaLimiteRetiro;
    private EstadoReserva estado;
    private String resueltaPor;
    private LocalDateTime resueltaEn;
    private String observaciones;

    public static ReservaDTO desde(Reserva r) {
        ReservaDTO dto = new ReservaDTO();
        dto.id = r.getId();
        dto.idLibro = r.getLibro().getId();
        dto.tituloLibro = r.getLibro().getTitulo();
        dto.idUsuario = r.getUsuario().getId();
        dto.nombreUsuario = r.getUsuario().getNombre() + " " + r.getUsuario().getApellidos();
        dto.fechaReserva = r.getFechaReserva();
        dto.fechaLimiteRetiro = r.getFechaLimiteRetiro();
        dto.estado = r.getEstado();
        dto.resueltaPor = r.getResueltaPor() == null ? null
                : r.getResueltaPor().getNombre() + " " + r.getResueltaPor().getApellidos();
        dto.resueltaEn = r.getResueltaEn();
        dto.observaciones = r.getObservaciones();
        return dto;
    }

    /* true si todavia se puede cancelar (sirve para mostrar el boton en la JSP). */
    public boolean isCancelable() {
        return estado == EstadoReserva.PENDIENTE || estado == EstadoReserva.APROBADA;
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

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public LocalDateTime getFechaLimiteRetiro() {
        return fechaLimiteRetiro;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public String getResueltaPor() {
        return resueltaPor;
    }

    public LocalDateTime getResueltaEn() {
        return resueltaEn;
    }

    public String getObservaciones() {
        return observaciones;
    }
}
