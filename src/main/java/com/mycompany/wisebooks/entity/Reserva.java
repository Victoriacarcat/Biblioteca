/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import org.hibernate.annotations.Check;

/**
 * Reserva de un libro hecha por un lector. El personal la aprueba o rechaza. Al
 * aprobarse se aparta una copia (baja stockDisponible) y se fija
 * fechaLimiteRetiro (3 dias).
 */
@Entity
@Table(name = "reservas",
        indexes = @Index(name = "idx_reservas_estado_limite", columnList = "estado, fecha_limite_retiro"))
@Check(constraints = "fecha_limite_retiro IS NULL OR fecha_limite_retiro >= fecha_reserva")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_libro", nullable = false)
    private Libro libro;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDateTime fechaReserva;

    // Se llena al aprobar
    @Column(name = "fecha_limite_retiro")
    private LocalDateTime fechaLimiteRetiro;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    // Quien aprobo o rechazo, y cuando
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resuelta_por")
    private Usuario resueltaPor;

    @Column(name = "resuelta_en")
    private LocalDateTime resueltaEn;

    @Column(name = "observaciones", length = 255)
    private String observaciones;

    // Bloqueo optimista: si dos personas cambian el mismo registro a la vez,
    // la segunda falla con un mensaje claro en lugar de pisar a la primera.
    @Version
    @Column(name = "version", nullable = false)
    private long version;

    @PrePersist
    void alCrear() {
        if (fechaReserva == null) {
            fechaReserva = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public LocalDateTime getFechaLimiteRetiro() {
        return fechaLimiteRetiro;
    }

    public void setFechaLimiteRetiro(LocalDateTime fechaLimiteRetiro) {
        this.fechaLimiteRetiro = fechaLimiteRetiro;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public Usuario getResueltaPor() {
        return resueltaPor;
    }

    public void setResueltaPor(Usuario resueltaPor) {
        this.resueltaPor = resueltaPor;
    }

    public LocalDateTime getResueltaEn() {
        return resueltaEn;
    }

    public void setResueltaEn(LocalDateTime resueltaEn) {
        this.resueltaEn = resueltaEn;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
