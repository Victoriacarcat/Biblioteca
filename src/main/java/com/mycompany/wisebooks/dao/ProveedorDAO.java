package com.mycompany.wisebooks.dao;

import com.mycompany.wisebooks.entity.Proveedor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/*
 * Acceso a la tabla proveedores. Recibe el EntityManager del service
 * (no abre transacciones ni contiene reglas de negocio).
 */
public class ProveedorDAO {

    public Proveedor guardar(EntityManager em, Proveedor proveedor) {
        em.persist(proveedor);
        return proveedor;
    }

    public Optional<Proveedor> buscarPorId(EntityManager em, Long id) {
        return Optional.ofNullable(em.find(Proveedor.class, id));
    }

    /* true si otro proveedor (distinto de excluirId) ya tiene ese nombre (sin distinguir mayusculas). */
    public boolean existeNombre(EntityManager em, String nombre, Long excluirId) {
        return existe(em, "LOWER(p.nombre) = LOWER(:valor)", nombre, excluirId);
    }

    /* true si otro proveedor (distinto de excluirId) ya tiene ese NIT. */
    public boolean existeNit(EntityManager em, String nit, Long excluirId) {
        return existe(em, "p.nit = :valor", nit, excluirId);
    }

    private boolean existe(EntityManager em, String condicion, String valor, Long excluirId) {
        String jpql = "SELECT COUNT(p) FROM Proveedor p WHERE " + condicion;
        if (excluirId != null) {
            jpql += " AND p.id <> :excluirId";
        }
        TypedQuery<Long> consulta = em.createQuery(jpql, Long.class)
                .setParameter("valor", valor);
        if (excluirId != null) {
            consulta.setParameter("excluirId", excluirId);
        }
        return consulta.getSingleResult() > 0;
    }

    /*
     * Lista en orden alfabetico. El texto (opcional) busca en nombre, NIT y
     * persona de contacto.
     */
    public List<Proveedor> listar(EntityManager em, String texto, boolean incluirInactivos) {
        String filtro = texto == null ? "" : texto.trim().toLowerCase();

        StringBuilder jpql = new StringBuilder("SELECT p FROM Proveedor p WHERE 1 = 1");
        if (!incluirInactivos) {
            jpql.append(" AND p.activo = true");
        }
        if (!filtro.isEmpty()) {
            jpql.append(" AND (LOWER(p.nombre) LIKE :texto"
                    + " OR LOWER(p.nit) LIKE :texto"
                    + " OR LOWER(p.personaContacto) LIKE :texto)");
        }
        jpql.append(" ORDER BY p.nombre");

        TypedQuery<Proveedor> consulta = em.createQuery(jpql.toString(), Proveedor.class);
        if (!filtro.isEmpty()) {
            consulta.setParameter("texto", "%" + filtro + "%");
        }
        return consulta.getResultList();
    }
}
