package com.mycompany.wisebooks.dao;

import com.mycompany.wisebooks.entity.Rol;
import com.mycompany.wisebooks.entity.TipoDocumento;
import com.mycompany.wisebooks.entity.Usuario;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/*
 * Acceso a la tabla usuarios. Solo habla con la base de datos:
 * aqui no van reglas de negocio (eso es trabajo del service).
 *
 */
public class UsuarioDAO {

    /* Marca un usuario nuevo para guardarse y lo devuelve (ya con su id). */
    public Usuario guardar(EntityManager em, Usuario usuario) {
        em.persist(usuario);
        return usuario;
    }

    /* Guarda los cambios de un usuario que ya existe. */
    public Usuario actualizar(EntityManager em, Usuario usuario) {
        return em.merge(usuario);
    }

    /* Busca por id. Optional = "puede que no exista". */
    public Optional<Usuario> buscarPorId(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(Usuario.class, id));
    }

    /* Busca por correo (sin distinguir mayusculas). */
    public Optional<Usuario> buscarPorCorreo(EntityManager em, String correo) {
        List<Usuario> resultado = em.createQuery(
                "SELECT u FROM Usuario u WHERE LOWER(u.correo) = LOWER(:correo)",
                Usuario.class)
                .setParameter("correo", correo)
                .setMaxResults(1)
                .getResultList();
        return resultado.stream().findFirst();
    }

    /* true si ya hay un usuario con ese correo. */
    public boolean existeCorreo(EntityManager em, String correo) {
        Long total = em.createQuery(
                "SELECT COUNT(u) FROM Usuario u WHERE LOWER(u.correo) = LOWER(:correo)",
                Long.class)
                .setParameter("correo", correo)
                .getSingleResult();
        return total > 0;
    }

    /* true si ya hay un usuario con ese tipo y numero de documento. */
    public boolean existeDocumento(EntityManager em, TipoDocumento tipo, String documento) {
        Long total = em.createQuery(
                "SELECT COUNT(u) FROM Usuario u "
                + "WHERE u.tipoDocumento = :tipo AND u.documento = :documento",
                Long.class)
                .setParameter("tipo", tipo)
                .setParameter("documento", documento)
                .getSingleResult();
        return total > 0;
    }

    /* true si existe al menos un usuario con ese rol. */
    public boolean existeRol(EntityManager em, Rol rol) {
        Long total = em.createQuery(
                "SELECT COUNT(u) FROM Usuario u WHERE u.rol = :rol", Long.class)
                .setParameter("rol", rol)
                .getSingleResult();
        return total > 0;
    }
}
