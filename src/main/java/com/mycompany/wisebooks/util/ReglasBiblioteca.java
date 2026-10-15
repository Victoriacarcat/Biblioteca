/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.util;

import java.math.BigDecimal;

/**
 * Las reglas numericas de la biblioteca en un solo lugar.
 * Para cambiar una regla (por ejemplo, la multa) se cambia aqui y listo.
 */
public final class ReglasBiblioteca {

    private ReglasBiblioteca() {
    }

    // Prestamos
    public static final int DIAS_PRESTAMO = 14;
    public static final int DIAS_RENOVACION = 7;
    public static final int MAX_RENOVACIONES = 2;
    public static final int MAX_PRESTAMOS_ACTIVOS = 10;

    // Reservas
    public static final int DIAS_RETIRO_RESERVA = 3;          // desde que se aprueba
    public static final int MAX_RESERVAS_SUSPENDIDO = 2;      // reservas vivas de un suspendido

    // Multa por dia de retraso (COP)
    public static final BigDecimal TARIFA_MORA_DIARIA = new BigDecimal("500");
}