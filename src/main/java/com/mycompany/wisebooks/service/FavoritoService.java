package com.mycompany.wisebooks.service;

import com.mycompany.wisebooks.dto.LibroDTO;
import com.mycompany.wisebooks.dto.UsuarioDTO;
import com.mycompany.wisebooks.entity.Libro;
import com.mycompany.wisebooks.entity.Usuario;
import com.mycompany.wisebooks.util.Autorizacion;
import com.mycompany.wisebooks.util.Transaccion;
import com.mycompany.wisebooks.util.ValidacionException;
import jakarta.persistence.EntityManager;
import java.util.Comparator;
import java.util.List;

/*
 * Libros favoritos del lector (tabla puente "favoritos", sin entidad propia:
 * se manejan desde Usuario.getFavoritos()). Exclusivo del LECTOR.
 * No se audita: es una preferencia personal, no una accion de la biblioteca.
 */
public class FavoritoService {

    /* Los favoritos del lector, en orden alfabetico. Los libros desactivados no se muestran. */
    public List<LibroDTO> misFavoritos(UsuarioDTO actor) {
        return Transaccion.consultar(em -> {
            Usuario lector = Autorizacion.exigirLector(em, actor);
            return lector.getFavoritos().stream()
                    .filter(Libro::isActivo)
                    .sorted(Comparator.comparing(Libro::getTitulo, String.CASE_INSENSITIVE_ORDER))
                    .map(LibroDTO::desde)
                    .toList();
        });
    }

    /* true si el libro esta en los favoritos del lector (para pintar el corazon en la JSP). */
    public boolean esFavorito(UsuarioDTO actor, Long idLibro) {
        if (actor == null || idLibro == null) {
            return false;
        }
        return Transaccion.consultar(em -> {
            Usuario lector = Autorizacion.exigirLector(em, actor);
            return lector.getFavoritos().stream().anyMatch(l -> l.getId().equals(idLibro));
        });
    }

    /* Agrega el libro a favoritos. Si ya estaba, no pasa nada. */
    public void agregar(UsuarioDTO actor, Long idLibro) {
        Transaccion.ejecutarAccion(em -> {
            Usuario lector = Autorizacion.exigirLector(em, actor);
            lector.getFavoritos().add(exigirLibroActivo(em, idLibro));
        });
    }

    /* Quita el libro de favoritos. Si no estaba, no pasa nada. */
    public void quitar(UsuarioDTO actor, Long idLibro) {
        Transaccion.ejecutarAccion(em -> {
            Usuario lector = Autorizacion.exigirLector(em, actor);
            lector.getFavoritos().removeIf(l -> l.getId().equals(idLibro));
        });
    }

    private Libro exigirLibroActivo(EntityManager em, Long idLibro) {
        Libro libro = (idLibro == null) ? null : em.find(Libro.class, idLibro);
        if (libro == null || !libro.isActivo()) {
            throw new ValidacionException("El libro no existe.");
        }
        return libro;
    }
}
