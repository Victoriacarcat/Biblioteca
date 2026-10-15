/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.dto;

import com.mycompany.wisebooks.entity.Autor;
import com.mycompany.wisebooks.entity.Libro;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Lo que se muestra de un libro (catalogo, detalle, inventario).
 * IMPORTANTE: desde(...) debe llamarse mientras el EntityManager sigue abierto,
 * porque lee las relaciones (editorial, categoria, genero, autores).
 */
public class LibroDTO {

    private Long id;
    private String titulo;
    private String isbn;
    private LocalDate fechaPublicacion;
    private String descripcion;
    private String portadaUrl;
    private int stockTotal;
    private int stockDisponible;
    private String ubicacion;
    private String editorial;
    private String categoria;
    private String genero;
    private List<String> autores;
    private boolean activo;

    public static LibroDTO desde(Libro l) {
        LibroDTO dto = new LibroDTO();
        dto.id = l.getId();
        dto.titulo = l.getTitulo();
        dto.isbn = l.getIsbn();
        dto.fechaPublicacion = l.getFechaPublicacion();
        dto.descripcion = l.getDescripcion();
        dto.portadaUrl = l.getPortadaUrl();
        dto.stockTotal = l.getStockTotal();
        dto.stockDisponible = l.getStockDisponible();
        dto.ubicacion = l.getUbicacion();
        dto.editorial = l.getEditorial() == null ? null : l.getEditorial().getNombre();
        dto.categoria = l.getCategoria().getNombre();
        dto.genero = l.getGenero() == null ? null : l.getGenero().getNombre();
        dto.autores = l.getAutores().stream()
                .map(Autor::getNombre)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
        dto.activo = l.isActivo();
        return dto;
    }

    /* true si hay al menos una copia libre (sirve para ${libro.disponible} en JSP). */
    public boolean isDisponible() {
        return stockDisponible > 0;
    }

    /* Autores en una sola linea: "Autor 1, Autor 2". */
    public String getAutoresTexto() {
        return String.join(", ", autores);
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getIsbn() {
        return isbn;
    }

    public LocalDate getFechaPublicacion() {
        return fechaPublicacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getPortadaUrl() {
        return portadaUrl;
    }

    public int getStockTotal() {
        return stockTotal;
    }

    public int getStockDisponible() {
        return stockDisponible;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public String getEditorial() {
        return editorial;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getGenero() {
        return genero;
    }

    public List<String> getAutores() {
        return autores;
    }

    public boolean isActivo() {
        return activo;
    }
}
