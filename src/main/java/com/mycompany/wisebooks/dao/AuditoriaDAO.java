/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.wisebooks.dao;

import com.mycompany.wisebooks.entity.Auditoria;
import jakarta.persistence.EntityManager;

/*
 * Acceso a la tabla auditoria. Recibe el EntityManager del service, para que
 * el registro de auditoria se guarde en la MISMA transaccion que la accion
 * que lo origina: o se guardan las dos cosas, o ninguna.
 */
public class AuditoriaDAO {

    /* Marca el registro para guardarse (se confirma al terminar la transaccion). */
    public Auditoria guardar(EntityManager em, Auditoria auditoria) {
        em.persist(auditoria);
        return auditoria;
    }
}
