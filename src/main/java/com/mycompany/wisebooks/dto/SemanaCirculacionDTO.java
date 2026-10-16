package com.mycompany.wisebooks.dto;

import java.time.LocalDate;

/*
 * Prestamos entregados y libros devueltos en una semana (de lunes a domingo),
 * para el grafico "Circulacion por semana" del dashboard.
 */
public class SemanaCirculacionDTO {

    private final LocalDate inicio;     // lunes de esa semana
    private final long prestamos;
    private final long devoluciones;

    public SemanaCirculacionDTO(LocalDate inicio, long prestamos, long devoluciones) {
        this.inicio = inicio;
        this.prestamos = prestamos;
        this.devoluciones = devoluciones;
    }

    public LocalDate getInicio() {
        return inicio;
    }

    public long getPrestamos() {
        return prestamos;
    }

    public long getDevoluciones() {
        return devoluciones;
    }
}
