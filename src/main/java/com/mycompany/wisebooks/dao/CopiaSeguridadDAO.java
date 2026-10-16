package com.mycompany.wisebooks.dao;

import com.mycompany.wisebooks.entity.CopiaSeguridad;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

/*
 * Acceso a la tabla copias_seguridad (solo el registro; el archivo .sql vive
 * en disco). Recibe el EntityManager del service.
 */
public class CopiaSeguridadDAO {

    public CopiaSeguridad guardar(EntityManager em, CopiaSeguridad copia) {
        em.persist(copia);
        return copia;
    }

    public Optional<CopiaSeguridad> buscarPorId(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(CopiaSeguridad.class, id));
    }

    /* true si ya hay un respaldo registrado con ese nombre de archivo. */
    public boolean existeNombreArchivo(EntityManager em, String nombreArchivo) {
        Long total = em.createQuery(
                "SELECT COUNT(c) FROM CopiaSeguridad c WHERE c.nombreArchivo = :nombre",
                Long.class)
                .setParameter("nombre", nombreArchivo)
                .getSingleResult();
        return total > 0;
    }

    /* Todos los respaldos, el mas reciente primero. */
    public List<CopiaSeguridad> listar(EntityManager em) {
        return em.createQuery(
                "SELECT c FROM CopiaSeguridad c JOIN FETCH c.creadoPor ORDER BY c.creadoEn DESC",
                CopiaSeguridad.class)
                .getResultList();
    }
}
