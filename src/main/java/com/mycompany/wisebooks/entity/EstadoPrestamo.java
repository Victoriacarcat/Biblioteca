package com.mycompany.wisebooks.entity;
 
/**
 * Estado de un prestamo. 'Vencido' NO se guarda: se calcula con fecha_limite.
 */
public enum EstadoPrestamo {
    ACTIVO, DEVUELTO, PERDIDO, ANULADO
}
 