/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.dao;

import com.mycompany.wisebooks.dto.FiltroLibroDTO;
import com.mycompany.wisebooks.entity.Libro;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/*
 * Acceso a la tabla libros. A diferencia de UsuarioDAO, aqui NO se abre el
 * EntityManager ni la transaccion: los recibe del service, para que varias
 * operaciones (libro + catalogos + auditoria) se guarden juntas.
 */
public class LibroDAO {

    /* Marca el libro para guardarse (se confirma al terminar la transaccion). */
    public Libro guardar(EntityManager em, Libro libro) {
        em.persist(libro);
        return libro;
    }

    public Optional<Libro> buscarPorId(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(Libro.class, id));
    }

    /* true si otro libro (distinto de excluirId) ya tiene ese ISBN. */
    public boolean existeIsbn(EntityManager em, String isbn, Long excluirId) {
        String jpql = "SELECT COUNT(l) FROM Libro l WHERE l.isbn = :isbn";
        if (excluirId != null) {
            jpql += " AND l.id <> :excluirId";
        }
        TypedQuery<Long> consulta = em.createQuery(jpql, Long.class)
                .setParameter("isbn", isbn);
        if (excluirId != null) {
            consulta.setParameter("excluirId", excluirId);
        }
        return consulta.getSingleResult() > 0;
    }

    /*
     * Busqueda del catalogo: arma la consulta segun los filtros recibidos.
     * El texto busca en titulo, ISBN y nombre de autor (sin distinguir mayusculas).
     */
    public List<Libro> buscar(EntityManager em, FiltroLibroDTO filtro, boolean incluirInactivos) {
        String texto = filtro.getTexto() == null ? "" : filtro.getTexto().trim().toLowerCase();

        StringBuilder jpql = new StringBuilder(
                "SELECT DISTINCT l FROM Libro l "
                + "JOIN FETCH l.categoria "
                + "LEFT JOIN FETCH l.editorial "
                + "LEFT JOIN FETCH l.genero "
                + "LEFT JOIN l.autores a "
                + "WHERE 1 = 1");
        if (!incluirInactivos) {
            jpql.append(" AND l.activo = true");
        }
        if (!texto.isEmpty()) {
            jpql.append(" AND (LOWER(l.titulo) LIKE :texto"
                    + " OR LOWER(l.isbn) LIKE :texto"
                    + " OR LOWER(a.nombre) LIKE :texto)");
        }
        if (filtro.getIdCategoria() != null) {
            jpql.append(" AND l.categoria.id = :idCategoria");
        }
        if (filtro.getIdGenero() != null) {
            jpql.append(" AND l.genero.id = :idGenero");
        }
        if (filtro.isSoloDisponibles()) {
            jpql.append(" AND l.stockDisponible > 0");
        }
        jpql.append(" ORDER BY l.titulo");

        TypedQuery<Libro> consulta = em.createQuery(jpql.toString(), Libro.class);
        if (!texto.isEmpty()) {
            consulta.setParameter("texto", "%" + texto + "%");
        }
        if (filtro.getIdCategoria() != null) {
            consulta.setParameter("idCategoria", filtro.getIdCategoria());
        }
        if (filtro.getIdGenero() != null) {
            consulta.setParameter("idGenero", filtro.getIdGenero());
        }
        return consulta.getResultList();
    }
}
