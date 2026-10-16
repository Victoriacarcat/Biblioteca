/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.dao;

import com.mycompany.wisebooks.entity.Autor;
import com.mycompany.wisebooks.entity.Categoria;
import com.mycompany.wisebooks.entity.Editorial;
import com.mycompany.wisebooks.entity.Genero;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/*
 * Acceso a los catalogos simples: editoriales, autores, categorias y generos.
 * Los cuatro tienen la misma forma (id, nombre unico, activo), asi que comparten
 * este DAO. Recibe el EntityManager del service, igual que LibroDAO.
 */
public class CatalogoDAO {

    /*
     * Busca por nombre sin distinguir mayusculas. "tipo" es la entidad
     * (Editorial.class, Autor.class, Categoria.class o Genero.class).
     * El nombre de la entidad sale de la clase, nunca del texto del usuario,
     * asi que armar la consulta con el no abre la puerta a inyeccion SQL.
     */
    public <T> Optional<T> buscarPorNombre(EntityManager em, Class<T> tipo, String nombre) {
        List<T> resultado = em.createQuery(
                "SELECT e FROM " + tipo.getSimpleName() + " e WHERE LOWER(e.nombre) = LOWER(:nombre)",
                tipo)
                .setParameter("nombre", nombre)
                .setMaxResults(1)
                .getResultList();
        return resultado.stream().findFirst();
    }

    /* Marca un elemento nuevo del catalogo para guardarse. */
    public void guardar(EntityManager em, Object catalogo) {
        em.persist(catalogo);
    }

    /* Categorias activas, en orden alfabetico (para listas desplegables). */
    public List<Categoria> listarCategoriasActivas(EntityManager em) {
        return em.createQuery(
                "SELECT c FROM Categoria c WHERE c.activo = true ORDER BY c.nombre",
                Categoria.class).getResultList();
    }

    /* Generos activos, en orden alfabetico. */
    public List<Genero> listarGenerosActivos(EntityManager em) {
        return em.createQuery(
                "SELECT g FROM Genero g WHERE g.activo = true ORDER BY g.nombre",
                Genero.class).getResultList();
    }

    /* Editoriales activas, en orden alfabetico. */
    public List<Editorial> listarEditorialesActivas(EntityManager em) {
        return em.createQuery(
                "SELECT e FROM Editorial e WHERE e.activo = true ORDER BY e.nombre",
                Editorial.class).getResultList();
    }

    /* Autores activos, en orden alfabetico. */
    public List<Autor> listarAutoresActivos(EntityManager em) {
        return em.createQuery(
                "SELECT a FROM Autor a WHERE a.activo = true ORDER BY a.nombre",
                Autor.class).getResultList();
    }
}
