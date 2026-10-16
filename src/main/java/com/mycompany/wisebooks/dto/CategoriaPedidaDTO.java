package com.mycompany.wisebooks.dto;

/*
 * Una categoria y cuantos prestamos tuvieron sus libros
 * (para "Categorias mas pedidas" del dashboard).
 */
public class CategoriaPedidaDTO {

    private final String nombre;
    private final long prestamos;

    public CategoriaPedidaDTO(String nombre, long prestamos) {
        this.nombre = nombre;
        this.prestamos = prestamos;
    }

    public String getNombre() {
        return nombre;
    }

    public long getPrestamos() {
        return prestamos;
    }
}
